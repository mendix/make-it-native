#!groovy

stage('copy artifacts') {
    copyArtifacts (
        projectName: "current.frontend.mendixnative.android.latest",
        selector: specific("${env.ANDROID_BUILD_NUMBER}"),
        target: "developerapp/lib/artifacts"
    )

    copyArtifacts (
        projectName: "current.frontend.mendixnative.ios.latest",
        selector: specific("${env.IOS_BUILD_NUMBER}"),
        target: "developerapp/lib/artifacts"
    )
}

stage("release") {
    docker.image("node:14.17").inside("-u root") {
        dir("developerapp/lib") {
            withCredentials([string(credentialsId: "mendix-native-npm-token", variable: 'NPM_TOKEN')]) {
                withEnv(["GIT_SSH_COMMAND=ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"]) {
                    sshagent(['gitlab-ssh']){
                        sh """
                            mv ./artifacts/developerapp/artifacts/aar/* ./android
                            mv ./artifacts/developerapp/ios/build/mendixnative/* ./ios

                            curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | bash
                            apt-get install -y git-lfs
                            git config --global user.name "jenkins"
                            git config --global user.email "jenkins@rnd.mendix.com"
                            npm install
                            npm config set registry=https://registry.npmjs.org
                            npm config set //registry.npmjs.org/:_authToken="$NPM_TOKEN"
                            npm run release -- ${VERSION} --ci
                        """
                    }
                }
            }
        }
    }
}
