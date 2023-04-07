import java.text.SimpleDateFormat

String evDir = "/opt/easyvisa"
String baseDir = "${evDir}/builds"
def latestCommit
def cqlLoadDate
def config

config = [
        "dev": [
                "db": [
                        "credentialsId": [
                                "password": "ev-test-db-password"
                        ]
                ],
                "neo4j": [
                        url: "neo4j-4-url",
                        "credentialsId": [
                                "password": "neo4j-4-user-password"
                        ]
                ],
                "git": [
                        "credentialsId": "ev-git-ssh"
                ]
        ]
]
// Get the specific deployment environment
config = config.dev
Date now = new Date()
String artifactVersion = new SimpleDateFormat("yyyyMMdd'T'HHmmss").format(now)

pipeline {
    agent any
    options {
        disableConcurrentBuilds()
    }
    environment{
        DEPLOY_ENV = "dev"
        DB_PASSWORD = credentials("${config.db.credentialsId.password}")
        NEO4J_URL = credentials("${config.neo4j.url}")
        NEO4J_CRED =  credentials("${config.neo4j.credentialsId.password}")
    }
    stages {
        stage('Pull latest code') {
            steps {
                // Note here for new pipelines, this takes a while but once it succeeds, all subsequent builds will be
                // faster.
                script {
                    git url: 'git@bitbucket.org:Michael_Andrew/easyvisa.git',
                            credentialsId: config.git.credentialsId,
                            branch: params.BRANCH_TO_BUILD
                }
            }
        }

        stage("Run API tests") {
            when {
                expression { !(params.fast_build) }
            }
            steps {
                withEnv([
                        'JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64',
                        "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}",
                        "TEST_DB_NAME=easyvisa_test_grails4",
                        "DB_USER=easyvisa",
                        "ROOT_DIR='./server'",
                ]) {
                    // This setup script could be extracted out into a common method, but I'm going to leave it for now.
                    sh "server/bin/setup-test-db.sh"
                    sh """
                       cat data-import/output/questionnaire-shell-data.cql | \
                       /opt/cypher-shell-4/cypher-shell -u ${NEO4J_CRED_USR} -p ${NEO4J_CRED_PSW} --address ${NEO4J_URL} --format plain
                       """.stripIndent()
                    sh """
                       ./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/grails4_test-config.yml -Dgrails.env=test -Xms512m -Xmx4g' \
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
                expression { !(params.fast_build) }
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
                       ./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/grails4_test-config.yml -Dgrails.env=test -Xms512m -Xmx4g' \
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

        stage("Build backend") {
            steps {
                withEnv([
                        'JAVA_HOME=/opt/amazon-corretto-11.0.13.8.1-linux-x64',
                        "PATH=/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}"
                ]) {
                    sh "mkdir /opt/easyvisa/builds/${BUILD_NUMBER}"
                    sh '[ -d /opt/easyvisa/builds/artifacts ] || mkdir -p /opt/easyvisa/builds/artifacts'
                    sh """
                       ./gradlew --no-daemon  --rerun-tasks --max-workers 2 -Dgrails.env=aws_development -Dorg.gradle.project.envName=dev \
                       server:clean \
                       server:war
                       """.stripIndent()
                    sh "cp server/build/libs/easyvisa.war /opt/easyvisa/builds/artifacts/${artifactVersion}-easyvisa.war"
                    s3Upload(file: 'server/build/libs/easyvisa.war', bucket: 'elasticbeanstalk-easyvisa-test', path: "${artifactVersion}-easyvisa.war")
                    sh "cp data-import/output/questionnaire-shell-data.cql /opt/easyvisa/builds/${BUILD_NUMBER}/graph.cql"
                    script {
                        latestCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
                        cqlLoadDate = sh(returnStdout: true, script: "cat server/src/main/resources/db/neo4j_update.txt").trim()
                    }
                    sh "echo '${latestCommit}' > $baseDir/${BUILD_NUMBER}/commitId.txt"
                    sh "echo '${cqlLoadDate}' > $baseDir/${BUILD_NUMBER}/neo4j.txt"
                }
            }
        }
        stage("Trigger frontend build") {
            steps {
                script {
                    build job: 'dev-Easyvisa-frontend', wait: true, propagate: true, parameters: [[$class: 'StringParameterValue', name: 'app_env', value: 'dev']]
                }
            }
        }
        stage("Trigger dev deployment") {
            steps {
                script {
                    build job: 'deploy-development', wait: true, propagate: true, parameters: [
                            [$class: 'StringParameterValue', name: 'deploy_environment', value: 'dev'],
                            [$class: 'StringParameterValue', name: 'build_no', value: "${BUILD_NUMBER}"],
                            [$class: 'StringParameterValue', name: 'artifact_id', value: "${artifactVersion}"]
                    ]
                }
            }
        }
    }
    post {
        failure {
            script {
                if (params.send_notification) {
                    emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com ',
                            subject: "Development pipeline deploy: #${BUILD_NUMBER} ${currentBuild.currentResult == "SUCCESS" ? "succeeded" : "failed"}",
                            body: "See build log here: ${BUILD_URL}"
                }
            }
        }
    }
}
