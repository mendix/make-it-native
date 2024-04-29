#!groovy

stage("Promote") {
    docker.image("developerapp-build:8").inside {
        dir("developerapp/ios") {
            sh """
                if [ "$PUBLISH_STRATEGY" = "BUMP_AND_PUBLISH" ] || [ "$PUBLISH_STRATEGY" = "ONLY_PUBLISH" ]; then
                    fastlane promote_to_production build_number:${BUILD_NUMBER}
                fi
            """
        }
    }
}

stage("Bump version") {
    docker.image("mx-client:28").inside {
        withEnv(["GIT_SSH_COMMAND=ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"]) {
            dir("developerapp/scripts") {
                sshagent(['gitlab-ssh']){
                    sh """
                       if [ "$PUBLISH_STRATEGY" = "BUMP_AND_PUBLISH" ] || [ "$PUBLISH_STRATEGY" = "ONLY_BUMP" ]; then
                        npm ci
                        npm run bump-version:ios
                       fi
                   """
               }
            }
        }
    }
}
