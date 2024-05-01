#!groovy

stage("Build android library") {
    docker.image("developerapp-build:11").inside {
        dir("developerapp/android") {
            sh 'fastlane build_mendix_native'
            sh 'cp ../artifacts/aar/mendixnative-release.aar ../lib/android/'
        }
        archiveArtifacts artifacts: "developerapp/artifacts/aar/*.aar"
    }
}

