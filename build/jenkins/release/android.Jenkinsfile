#!groovy

stage("Promote") {
    docker.image("developerapp-build:11").inside {
        dir("developerapp/android") {
            sh "fastlane promote_to_production"
        }
    }
}

stage("Bump version") {
    docker.image("mx-client:28").inside {
        withEnv(["GIT_SSH_COMMAND=ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"]) {
            dir("developerapp/scripts") {
                sshagent(['gitlab-ssh']){
                    sh """
                       if [ "$BUMP_VERSION" = "true" ]; then
                        npm ci
                        npm run bump-version:android
                       fi
                   """
               }
            }
        }
    }
}
