dir("developerapp") {
    stage("MiN build") {
        sshagent(['gitlab-ssh']){
            dir("ios") {
                sh  '''
                    source ~/.bashrc
                    source ~/.rvm/scripts/rvm
                    export PATH=$PATH:/usr/local/bin
                    export LANG=en_US.UTF-8

                    # Install SSL certificates
                    mkdir /usr/local/etc/ca-certificates | true
                    curl -f -o /usr/local/etc/ca-certificates/cert.pem https://curl.se/ca/cacert.pem
                    rvm osx-ssl-certs update all

                    # Download Apple certificates
                    curl -f -o /tmp/AppleWWDRCA.cer https://developer.apple.com/certificationauthority/AppleWWDRCA.cer
                    curl -f -o /tmp/AppleWWDRCAG3.cer https://www.apple.com/certificateauthority/AppleWWDRCAG3.cer
                    curl -f -o /tmp/AppleIncRootCertificate.cer https://www.apple.com/appleca/AppleIncRootCertificate.cer

                    # Install Apple certificates
                    sudo security import /tmp/AppleWWDRCA.cer -k /Library/Keychains/System.keychain -T /usr/bin/codesign -T /usr/bin/security -T /usr/bin/productbuild | true
                    sudo security import /tmp/AppleWWDRCAG3.cer -k /Library/Keychains/System.keychain -T /usr/bin/codesign -T /usr/bin/security -T /usr/bin/productbuild | true
                    sudo security import /tmp/AppleIncRootCertificate.cer -k /Library/Keychains/System.keychain | true

                    pod repo add-cdn trunk https://cdn.cocoapods.org/

                    #Clear all keychains
                    sudo rm -rf /Users/ec2-user/Library/Keychains/

                    ssh-keyscan -H ssh.gitlab.rnd.mendix.com >> ~/.ssh/known_hosts

                    fastlane beta submit:$SUBMIT_TO_APPSTORE
                    '''
            }
        }
    }

    stage("Archive artifacts") {
        archiveArtifacts artifacts: "artifacts/*"
    }
}
