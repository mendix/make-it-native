import fs from "fs";
import path from "path";
import { Octokit } from "@octokit/rest";
import { fileURLToPath } from "url";
import simpleGit, { GitConfigScope } from "simple-git";

const required = [
  "MIN_VERSION",
  "GITHUB_TOKEN",
  "GITHUB_SHA",
  "GITHUB_REPOSITORY",
  "GITHUB_REPOSITORY_OWNER",
  "PAT",
];

const missing = required.filter((k) => !process.env[k]);
if (missing.length) {
  console.error("Missing env vars:", missing.join(", "));
  process.exit(1);
}

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const MIN_VERSION = process.env.MIN_VERSION;
const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const GITHUB_PAT = process.env.PAT;

const GIT_AUTHOR_NAME = "MendixMobile";
const GIT_AUTHOR_EMAIL = "moo@mendix.com";

// Changelog Settings
const CHANGELOG_BRANCH_NAME = `update-changelog-v${MIN_VERSION}`;

const octokit = new Octokit({ auth: GITHUB_PAT });

function getToday() {
  const today = new Date();
  const yyyy = today.getFullYear();
  const mm = String(today.getMonth() + 1).padStart(2, "0");
  const dd = String(today.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
}

// Changelog
function extractUnreleasedChangelog() {
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
  const newSection = `## [${MIN_VERSION}] Make it Native - ${today}\n\n${unreleasedContent}\n\n`;
  const unreleasedRegex =
    /^## \[Unreleased\](.*?)(?=^## \[\d+\.\d+\.\d+\][^\n]*|\Z)/ms;
  const updatedChangelog = changelog.replace(
    unreleasedRegex,
    `## [Unreleased]\n\n${newSection}`
  );
  fs.writeFileSync(changelogPath, updatedChangelog, "utf-8");
}

async function createPRUpdateChangelog() {
  const git = simpleGit();

  await git.addConfig("user.name", "MendixMobile", ["--global"]);
  await git.addConfig("user.email", "moo@mendix.com", ["--global"]);

  await git.checkoutLocalBranch(CHANGELOG_BRANCH_NAME);

  await git.add("CHANGELOG.md");
  await git.commit(`chore: update CHANGELOG for v${MIN_VERSION}`);
  await git.push("origin", CHANGELOG_BRANCH_NAME, ["--force-with-lease"]);

  await octokit.pulls.create({
    owner: process.env.GITHUB_REPOSITORY_OWNER,
    repo: process.env.GITHUB_REPOSITORY.split("/")[1],
    title: `Update CHANGELOG for v${MIN_VERSION}`,
    head: CHANGELOG_BRANCH_NAME,
    base: "main",
    body: "**Note:** Please do not take any action on this pull request unless it has been reviewed and approved by a member of the Mobile team.",
    draft: true,
  });
}

// async function createMiNRelease(unreleasedContent) {
//   await octokit.git.createRef({
//     owner: process.env.GITHUB_REPOSITORY_OWNER,
//     repo: process.env.GITHUB_REPOSITORY.split("/")[1],
//     ref: `refs/tags/v${MIN_VERSION}`,
//     sha: process.env.GITHUB_SHA,
//   });

//   await octokit.repos.createRelease({
//     owner: process.env.GITHUB_REPOSITORY_OWNER,
//     repo: process.env.GITHUB_REPOSITORY.split("/")[1],
//     tag_name: `v${MIN_VERSION}`,
//     name: `v${MIN_VERSION}`,
//     body: unreleasedContent,
//     draft: true,
//     prerelease: false,
//   });
// }

// Update MiN Changelog in MiN repo
async function updateMiNChangelog() {
  try {
    const { changelog, unreleasedContent, changelogPath } =
      extractUnreleasedChangelog();

    updateChangelog({ changelog, unreleasedContent, changelogPath });
    await createPRUpdateChangelog();
  } catch (err) {
    console.error("❌ Updating MiN Changelog failed:", err);
    process.exit(1);
  }
}

// Docs

// Update MiN Release Notes in Docs repo
async function updateMiNReleaseNotes() {}

(async () => {
  await updateMiNChangelog();
  process.chdir("..");
  await updateMiNReleaseNotes();
})();

/*
const VERSION = process.env.VERSION;
const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const PAT = process.env.PAT || GITHUB_TOKEN;

const CHANGELOG_BRANCH_NAME = `update-changelog-v${VERSION}`;

const DOCS_REPO_OWNER = "MendixMobile";
const DOCS_REPO_NAME = "docs";
const DOCS_UPSTREAM_OWNER = "MendixMobile";
// const DOCS_UPSTREAM_OWNER = "mendix";
const DOCS_BRANCH_NAME = `update-mobile-release-notes-v${VERSION}`;
const TARGET_FILE =
  "content/en/docs/releasenotes/mobile/make-it-native-parent/make-it-native-10.md";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const required = ["VERSION", "GITHUB_TOKEN", "GITHUB_SHA", "GITHUB_REPOSITORY"];
const missing = required.filter((k) => !process.env[k]);
if (missing.length) {
  console.error("Missing env vars:", missing.join(", "));
  process.exit(1);
}

const octokit = new Octokit({ auth: GITHUB_TOKEN });

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
  const newSection = `## [${VERSION}] Make it Native - ${today}\n\n${unreleasedContent}\n\n`;
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

async function createChangelogUpdatePR() {
  const git = simpleGit();

  await git.addConfig(
    "user.name",
    process.env.GIT_AUTHOR_NAME || "Release Bot",
    ["--global"]
  );
  await git.addConfig(
    "user.email",
    process.env.GIT_AUTHOR_EMAIL || "release-bot@example.com",
    ["--global"]
  );

  await git.checkoutLocalBranch(CHANGELOG_BRANCH_NAME);

  await git.add("CHANGELOG.md");
  await git.commit(`chore: update CHANGELOG for v${VERSION}`);
  await git.push("origin", CHANGELOG_BRANCH_NAME, ["--force-with-lease"]);

  await octokit.pulls.create({
    owner: process.env.GITHUB_REPOSITORY_OWNER,
    repo: process.env.GITHUB_REPOSITORY.split("/")[1],
    title: `Update CHANGELOG for v${VERSION}`,
    head: CHANGELOG_BRANCH_NAME,
    base: "main",
    body: "**Note:** Please do not take any action on this pull request unless it has been reviewed and approved by a member of the Mobile team.",
    draft: true,
  });

  process.chdir("..");
}

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

  const date = new Date();
  const formattedDate = date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
  const title = `## ${VERSION}\n\n**Release date: ${formattedDate}**`;

  return `${frontmatter}\n\n${firstParagraph}\n${title}\n\n${unreleasedContent}\n\n${afterFirstParagraph}`;
}

async function updateDocs(unreleasedContent) {
  const git = simpleGit();
  await git.clone(
    `https://x-access-token:${PAT}@github.com/${DOCS_REPO_OWNER}/${DOCS_REPO_NAME}.git`
  );
  process.chdir(DOCS_REPO_NAME);

  await git.addConfig(
    "user.name",
    process.env.GIT_AUTHOR_NAME || "Release Bot",
    ["--global"]
  );
  await git.addConfig(
    "user.email",
    process.env.GIT_AUTHOR_EMAIL || "release-bot@example.com",
    ["--global"]
  );

  await git.checkoutLocalBranch(DOCS_BRANCH_NAME);

  const newDocContent = injectUnreleasedToDoc(TARGET_FILE, unreleasedContent);
  fs.writeFileSync(TARGET_FILE, newDocContent, "utf-8");

  await git.add(TARGET_FILE);
  await git.commit(`docs: update mobile release notes for v${VERSION}`);
  await git.push("origin", DOCS_BRANCH_NAME, ["--force"]);

  const prBody = `
Automated sync of the latest release notes for v${VERSION} from [make-it-native](https://github.com/mendix/make-it-native).

---

**Note:**  
This pull request was automatically generated by an automation process managed by the Mobile team.
**Please do not take any action on this pull request unless it has been reviewed and approved by a member of the Mobile team.**
`;

  await octokit.pulls.create({
    owner: DOCS_UPSTREAM_OWNER,
    repo: DOCS_REPO_NAME,
    title: `Update mobile app release notes for v${VERSION}`,
    head: `${DOCS_REPO_OWNER}:${DOCS_BRANCH_NAME}`,
    base: "development",
    body: prBody,
    draft: true,
  });

  process.chdir("..");
}

(async () => {
  try {
    const { changelog, unreleasedContent, changelogPath } = extractUnreleased();

    await createRelease(unreleasedContent);
    updateChangelog({ changelog, unreleasedContent, changelogPath });
    await createChangelogUpdatePR();
    await updateDocs(unreleasedContent);

    console.log("Release, changelog update, and docs PR completed!");
    process.exit(0);
  } catch (err) {
    console.error("❌ Release script failed:", err);
    process.exit(1);
  }
})();

*/
