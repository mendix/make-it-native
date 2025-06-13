import fs from "fs";
import path from "path";
import { Octokit } from "@octokit/rest";
import simpleGit from "simple-git";

const docsRepo = {
  owner: "MendixMobile",
  repo: "docs",
};

const currentRepo = {
  owner: "mendix",
  repo: "make-it-native",
};

const VERSION = process.env.VERSION;
const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const PAT = process.env.PAT || GITHUB_TOKEN;

if (!VERSION) {
  console.error("VERSION env variable is required!");
  process.exit(1);
}

const CHANGELOG_BRANCH = `changelog-update-v${VERSION}`;

// Docs specific variables
const RELEASE_NOTES_BRANCH_NAME = `update-mobile-release-notes-v${VERSION}`;
const localDocsRepoPath = path.join(process.cwd(), "../test/", docsRepo.repo);
const TARGET_FILE = `${localDocsRepoPath}/content/en/docs/releasenotes/mobile/make-it-native-parent/make-it-native-10.md`;

const octokit = new Octokit({ auth: GITHUB_TOKEN });
const docsOctokit = new Octokit({ auth: PAT });

function getToday() {
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, "0");
  const dd = String(today.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
}

function extractUnreleased() {
  const changelogPath = path.resolve(__dirname, "../CHANGELOG.md");
  const changelog = fs.readFileSync(changelogPath, "utf-8");
  const unreleasedRegex =
    /^## \[Unreleased\](.*?)(?=^## \[\d+\.\d+\.\d+\][^\n]*|\Z)/ms;
  const match = changelog.match(unreleasedRegex);
  if (!match) throw new Error("No [Unreleased] section found!");
  const unreleasedContent = match[1].trim();
  if (!unreleasedContent) throw new Error("No changes under [Unreleased]!");
  return { changelog, unreleasedContent, changelogPath };
}

function updateChangelog({ changelog, unreleasedContent, changelogPath }) {
  const today = getToday();
  const newSection = `## [${VERSION}] Make it Native - ${today}\n${unreleasedContent}\n\n`;
  const unreleasedRegex =
    /^## \[Unreleased\](.*?)(?=^## \[\d+\.\d+\.\d+\][^\n]*|\Z)/ms;
  const updatedChangelog = changelog.replace(
    unreleasedRegex,
    `## [Unreleased]\n\n${newSection}`
  );
  fs.writeFileSync(changelogPath, updatedChangelog, "utf-8");
}

async function createRelease(unreleasedContent) {
  await octokit.git.createRef({
    owner: process.env.GITHUB_REPOSITORY_OWNER,
    repo: process.env.GITHUB_REPOSITORY.split("/")[1],
    ref: `refs/tags/v${VERSION}`,
    sha: process.env.GITHUB_SHA,
  });

  await octokit.repos.createRelease({
    owner: process.env.GITHUB_REPOSITORY_OWNER,
    repo: process.env.GITHUB_REPOSITORY.split("/")[1],
    tag_name: `v${VERSION}`,
    name: `v${VERSION}`,
    body: unreleasedContent,
    draft: false,
    prerelease: false,
  });
}

async function prChangelogUpdate() {
  const git = simpleGit();

  await git.checkoutLocalBranch(CHANGELOG_BRANCH);
  await git.add("CHANGELOG.md");
  await git.commit(`chore: update CHANGELOG for v${VERSION}`);
  await git.push("origin", CHANGELOG_BRANCH, ["--force"]);

  await octokit.pulls.create({
    owner: currentRepo.owner,
    repo: currentRepo.repo,
    title: `Update CHANGELOG for v${VERSION}`,
    head: CHANGELOG_BRANCH,
    base: "development",
    body: "**Note:** Please do not take any action on this pull request unless it has been reviewed and approved by a member of the Mobile team.",
    draft: true,
  });

  process.chdir("..");
}

async function updateDocs(unreleasedContent) {
  const currentPath = process.cwd();
  console.log("Cloning docs repo");
  const git = simpleGit();
  await git
    .outputHandler((command, stdout, stderr) => {
      stdout.pipe(process.stdout);
      stderr.pipe(process.stderr);

      stdout.on("data", (data) => {
        // Print data
        console.log(data.toString("utf8"));
      });
    })
    .clone(
      `https://x-access-token:${PAT}@github.com/${docsRepo.owner}/${docsRepo.repo}.git`,
      localDocsRepoPath,
      {
        "--depth": "1",
      }
    );

  console.log("Cloned docs repo");
  process.chdir(localDocsRepoPath);
  const docsGit = simpleGit();
  console.log("Checking out branch");
  await docsGit.checkoutLocalBranch(RELEASE_NOTES_BRANCH_NAME);
  console.log("Checked out branch");

  function injectUnreleasedToDoc(docPath, unreleasedContent) {
    const doc = fs.readFileSync(docPath, "utf-8");
    const frontmatterMatch = doc.match(/^---[\s\S]*?---/);
    if (!frontmatterMatch) throw new Error("Frontmatter not found!");
    const frontmatter = frontmatterMatch[0];
    const rest = doc.slice(frontmatter.length).trimStart();

    const firstParagraphMatch = rest.match(/^(.*?\n)(\s*\n)/s);
    if (!firstParagraphMatch) throw new Error("First paragraph not found!");
    const firstParagraph = firstParagraphMatch[1];
    const afterFirstParagraph = rest.slice(firstParagraph.length).trimStart();

    return `${frontmatter}\n\n${firstParagraph}\n${unreleasedContent}\n\n${afterFirstParagraph}`;
  }

  const newDocContent = injectUnreleasedToDoc(TARGET_FILE, unreleasedContent);
  console.log("Writing new doc content");
  fs.writeFileSync(TARGET_FILE, newDocContent, "utf-8");
  console.log("New doc content written");

  await docsGit.add(TARGET_FILE);
  console.log("Added file");
  await docsGit.commit(`docs: update mobile release notes for v${VERSION}`);
  console.log("Committed file");
  await docsGit.push("origin", RELEASE_NOTES_BRANCH_NAME, ["--force"]);
  console.log("Pushed file");

  const prBody = `
Automated sync of the latest release notes for v${VERSION} from [make-it-native](https://github.com/mendix/make-it-native).

---

**Note:**  
This pull request was automatically generated by an automation process managed by the Mobile team.
**Please do not take any action on this pull request unless it has been reviewed and approved by a member of the Mobile team.**
`;

  await docsOctokit.pulls.create({
    owner: docsRepo.owner,
    repo: docsRepo.repo,
    title: `Update mobile app release notes for v${VERSION}`,
    head: `${docsRepo.owner}:${RELEASE_NOTES_BRANCH_NAME}`,
    base: "development",
    body: prBody,
    draft: true,
  });

  process.chdir(currentPath);
  console.log("Changed directory");
}

(async () => {
  const { changelog, unreleasedContent, changelogPath } = extractUnreleased();

  await createRelease(unreleasedContent);
  updateChangelog({ changelog, unreleasedContent, changelogPath });
  await prChangelogUpdate();
  await updateDocs(unreleasedContent);

  console.log("Release, changelog update, and docs PR completed!");
})();
