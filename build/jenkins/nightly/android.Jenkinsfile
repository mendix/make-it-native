#!groovy

docker.image("developerapp-build:11").inside {
    dir("developerapp") {
        stage("Install dependencies") {
            sh "npm ci"
        }
        stage("MiN snyk scan") {
            withCredentials([string(credentialsId: "snyk-auth-key-plugin", variable: "SNYK_AUTH")]) {
                def snykSubproject = env.SNYK_BASELINE && env.SNYK_BASELINE != "master" ? "-${env.SNYK_BASELINE}" : "";
                withEnv(["SNYK_SUBPROJECT=${snykSubproject}"]) {
                    sh "npx snyk auth ${SNYK_AUTH} && npm run snyk:android" // nightly always reports
                }
            }
        }
        stage("MiN build") {
            dir("android") {
                keystore_path = "${pwd()}/keystores/upload.keystore"
                withEnv(["ANDROID_KEYSTORE_PATH=${keystore_path}"]) {
                    sh "fastlane internal submit:$SUBMIT_TO_GOOGLE"
                }
            }
        }
        stage("Archive artifacts") {
            archiveArtifacts artifacts: "artifacts/*.apk, artifacts/*.aab"
        }
    }
}
