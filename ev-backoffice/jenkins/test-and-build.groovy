/**
 * This builds the artifacts for QA and Production from master.
 * It saves these artifacts by build number, which is a parameter in deploy.groovy.
 * This allows us to deploy a specific set artifacts that were tested on QA to production, confident knowing they are
 * identical except for the environment parameter.
 *
 * In addition, we create an artifact version that is a timestamp.
 * This is because we need unique file names for WAR files later uploaded to S3, otherwise they get overwritten
 * silently.
 */
import java.text.SimpleDateFormat

String buildDir = "/opt/easyvisa/builds/$BUILD_NUMBER"

Date now = new Date()
String artifactVersion = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(now)

pipeline {
    agent any
    tools { nodejs 'node-default' }
    options { disableConcurrentBuilds() }
    environment {
        DB_PASSWORD = credentials('ev-test-db-password')
        NEO4J_URL = credentials('neo4j-4-url')
        NEO4J_CRED = credentials('neo4j-4-user-password')
    }
    stages {
        stage('Pull latest code') {
            steps {
                git url: 'git@bitbucket.org:Michael_Andrew/easyvisa.git',
                    credentialsId: 'ev-git-ssh',
                    branch: 'master'
            }
        }

        stage("Run API tests") {
            when {
                expression { !(params.skip_tests) }
            }
            steps {
                withEnv([
                        'JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64',
                        "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}",
                        "TEST_DB_NAME=easyvisa_test_grails4",
                        "DB_USER=easyvisa",
                        "ROOT_DIR='./server'",
                ]) {
                    sh "server/bin/setup-test-db.sh"
                    sh """
                       cat data-import/output/questionnaire-shell-data.cql | \
                       /opt/cypher-shell-4/cypher-shell -u $NEO4J_CRED_USR -p $NEO4J_CRED_PSW --address $NEO4J_URL --format plain
                       """.stripIndent()
                    sh """
                        ./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/grails4_test-config.yml -Dgrails.env=test -Xms512m -Xmx1024m' \
                        server:clean \
                        server:test \
                        server:integrationTest --rerun-tasks --no-daemon --tests 'com.easyvisa.test.api.*' --tests 'com.easyvisa.test.jobs.*'
                        """.stripIndent()
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
                expression { !(params.skip_tests) }
            }
            steps {
                withEnv([
                        'JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64',
                        "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}",
                        "TEST_DB_NAME=easyvisa_test_grails4",
                        "DB_USER=easyvisa",
                        "ROOT_DIR='./server'",
                ]) {
                    sh "server/bin/setup-test-db.sh"
                    sh """
                        ./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/grails4_test-config.yml -Dgrails.env=test -Xms512m -Xmx1024m' \
                        server:test \
                        server:integrationTest --rerun-tasks --no-daemon --tests 'com.easyvisa.test.questionnaire.*'
                        """.stripIndent()
                }
            }
            post {
                always {
                    junit 'server/build/test*/**/*.xml'

                }
            }
        }

        stage("Run Frontend tests") {
            when {
                expression { !(params.skip_tests) }
            }
            steps {
                wrap([$class: 'Xvfb']) {
                    sh 'rm -f client/package-lock.json'
                    sh 'rm -rf client/node_modules'
                    sh './gradlew client:test --rerun-tasks --no-daemon --max-workers 2'
                }
            }
        }

        stage("Build war") {
            steps {
                withEnv([
                        'JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64',
                        "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}"
                ]) {
                    sh "mkdir -p $buildDir"

                    // Record artifact version for this build for future retrieval by deploy.groovy
                    sh "echo ${artifactVersion} > $buildDir/artifact-version"

                    sh "./gradlew --no-daemon  --rerun-tasks --max-workers 2 -Dgrails.env=qa -Dorg.gradle.project.envName=qa server:clean server:war"
                    sh "cp server/build/libs/easyvisa.war /opt/easyvisa/builds/artifacts/${artifactVersion}-qa-easyvisa.war"
                    sh "mv server/build/libs/easyvisa.war $buildDir/ev-qa.war" // duplicate for posterity

                    sh "./gradlew --no-daemon  --rerun-tasks --max-workers 2 -Dgrails.env=production -Dorg.gradle.project.envName=prod server:clean server:war"
                    sh "cp server/build/libs/easyvisa.war /opt/easyvisa/builds/artifacts/${artifactVersion}-prod-easyvisa.war"
                    sh "mv server/build/libs/easyvisa.war $buildDir/ev-prod.war" // duplicate for posterity

                    sh "cp data-import/output/questionnaire-shell-data.cql $buildDir/graph.cql"

                    script {
                        latestCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
                        cqlLoadDate = sh(returnStdout: true, script: "cat server/src/main/resources/db/neo4j_update.txt").trim()
                        sh "echo '${latestCommit}' > $buildDir/commitId.txt"
                        sh "echo '${cqlLoadDate}' > $buildDir/neo4j.txt"
                    }
                }
            }
        }

        stage("Build Front end build") {
            steps {
                sh 'rm -f client/package-lock.json'
                sh 'rm -rf client/node_modules'

                sh "./gradlew --no-daemon --max-workers 2 client:build_qa --rerun-tasks"
                sh "mv client/dist $buildDir/frontend-qa"

                sh 'rm -f client/package-lock.json'
                sh 'rm -rf client/node_modules'
                sh "./gradlew --no-daemon --max-workers 2 client:build_prod --rerun-tasks"
                sh "mv client/dist $buildDir/frontend-prod"
            }
        }
    }
    post {
        failure {
            script {
                if (params.send_notification) {
                    emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com ',
                             subject: "Master Branch failed to build: #${BUILD_NUMBER} ${currentBuild.currentResult == "SUCCESS" ? "succeeded" : "failed"}",
                             body: "See build log here: ${BUILD_URL}"
                }
            }
        }
        success {
            script {
                if (params.send_notification) {
                    emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com, toants.301@gmail.com ',
                             subject: "Master Branch is Ready for deployment: #${BUILD_NUMBER} ${currentBuild.currentResult == "SUCCESS" ? "succeeded" : "failed"}",
                             body: "See build log here: ${BUILD_URL}"
                }
            }
        }
    }
}
