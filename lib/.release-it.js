module.exports = ({
  "npm": {
    "publish": true
  },
   "git": {
    "push": false,
    "requireUpstream": false,
    "tag": false,
    "requireCleanWorkingDir": false,
    "commitMessage": "[RELEASE] Bump @mendix/native library version to v${version}"
  },
  "publishConfig": {
    "registry": "https://nexus.staging.not-rnd.mendix.com/repository/npm-hosted"
  },
  "plugins": {
    "@release-it/keep-a-changelog": {
      "filename": "../CHANGELOG.mendixnative.md",
      "strictLatest": false,
      "keepUnreleased": process.env.VERSION === "prerelease",
      "addUnreleased": true
    }
  },
  "hooks":{
    "before:init": [
        "git fetch origin",
        "git checkout " + process.env.BRANCH,
        "git pull --rebase origin " + process.env.BRANCH + " --autostash"
    ],
    "before:git:release":[
        "git add ../CHANGELOG.mendixnative.md"
    ],
    "after:release": [
        "git push origin " + process.env.BRANCH
    ]
  }
})
