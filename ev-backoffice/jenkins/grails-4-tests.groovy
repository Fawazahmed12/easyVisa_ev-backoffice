void sendBuildFailedEmail(send_notification) {
    if (send_notification) {
        String emailSubject = "Grails 4 tests build no. #${BUILD_NUMBER} Failed"
        emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com',
                subject: emailSubject,
                body: "See build log here - ${BUILD_URL}"
    }
}

void sendBuildStatusChangedEmail(send_notification) {
    if (send_notification && currentBuild.currentResult == "SUCCESS") {
        String emailSubject = "Grails 4 tests build no. #${BUILD_NUMBER} is now passing"
        emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com',
                subject: emailSubject,
                body: "See build log here - ${BUILD_URL}"
    }
}

pipeline {
    agent any
    tools { jdk 'aws-jdk-11' }
    options { disableConcurrentBuilds() }
    environment{
        DB_PASSWORD=credentials('ev-test-db-password')
        NEO4J_URL = credentials('neo4j-4-url')
        NEO4J_CRED =  credentials('neo4j-4-user-password')
    }
    stages {
        stage('Pull latest code') {
            steps {
                git url: 'git@bitbucket.org:Michael_Andrew/easyvisa.git',
                        credentialsId: 'ev-git-ssh',
                        branch: 'development'
            }
        }

        stage("Run API tests") {
            when {
                expression { return !(params.fast_build) }
            }
            steps {
                sh """export DB_PASSWORD=${DB_PASSWORD};
                export TEST_DB_NAME=easyvisa_test_grails4;
                export DB_USER=easyvisa;
                export ROOT_DIR="./server";
                server/bin/setup-test-db.sh;"""
                withEnv([ 'JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64', "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}"]) {
                    sh "cat data-import/output/questionnaire-shell-data.cql | /opt/cypher-shell-4/cypher-shell -u ${NEO4J_CRED_USR} -p ${NEO4J_CRED_PSW} --address ${NEO4J_URL} --format plain"
                    sh "./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/grails4_test-config.yml -Dgrails.env=test' server:clean server:test server:integrationTest --rerun-tasks --no-daemon --tests 'com.easyvisa.test.api.*' --tests 'com.easyvisa.test.jobs.*'"

                }
            }
            post {
                always {
                    junit 'server/build/test*/**/*.xml'

                }
            }
        }

        stage("Run Questionnaire tests") {
            when {
                expression { return !(params.fast_build) }
            }
            steps {
                sh """export DB_PASSWORD=${DB_PASSWORD};
                export TEST_DB_NAME=easyvisa_test_grails4;
                export DB_USER=easyvisa;
                export ROOT_DIR="./server";
                server/bin/setup-test-db.sh;"""

                withEnv([ 'JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64', "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}"]) {
                    sh "./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/grails4_test-config.yml -Dgrails.env=test -Xms512m -Xmx1024m' server:test server:integrationTest --rerun-tasks --no-daemon --tests 'com.easyvisa.test.questionnaire.*'"
                }
            }
            post {
                always {
                    junit 'server/build/test*/**/*.xml'

                }
            }
        }

        stage("Build war"){
            steps {
                withEnv(['JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64', "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}"]) {
                    sh "./gradlew --no-daemon  --rerun-tasks --max-workers 2 -Dgrails.env=aws_development -Dorg.gradle.project.envName=dev server:clean server:assemble"
                    s3Upload(file: 'server/build/libs/server-0.1.war', bucket: "elasticbeanstalk-easyvisa-test", path: 'easyvisa-grails4.war')
                }
            }
        }
    }
    post {
        failure {
            sendBuildFailedEmail(params.send_notification)
        }
        changed {
            sendBuildStatusChangedEmail(params.send_notification)
        }
    }
}