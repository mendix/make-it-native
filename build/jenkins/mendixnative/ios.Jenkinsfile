#!groovy

stage("Build ios library") {
     dir("developerapp/ios") {
        sh '''
            source ~/.bashrc
            export PATH=$PATH:/usr/local/bin
            export LANG=en_US.UTF-8

            rvm osx-ssl-certs update all
            fastlane build_mendixnative
        '''
    }
    archiveArtifacts artifacts: "developerapp/ios/build/mendixnative/mendixnative.xcframework/**/*"
}

