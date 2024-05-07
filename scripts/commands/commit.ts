import {execSync} from "child_process";

export function commitChanges(message: string) {
    console.log("Commit changes");
    execSync(`git config --global user.name "jenkins"`)
    execSync(`git config --global user.email "jenkins@rnd.mendix.com"`)
    execSync(`git fetch origin`)
    execSync(`git checkout "${process.env.BRANCH}"`)
    execSync(`git pull --rebase origin "${process.env.BRANCH}" --autostash`)

    execSync(`git add ../package.json`)
    execSync(`git add ../CHANGELOG.android.txt`)
    execSync(`git add ../CHANGELOG.ios.txt`)
    execSync(`git commit -m "${message}"`)
    execSync(`git push origin "${process.env.BRANCH}"`)

    console.log("Version update completed")
}