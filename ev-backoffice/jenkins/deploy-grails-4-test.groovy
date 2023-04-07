//file:noinspection GroovyAssignabilityCheck

import java.text.ParseException
import java.text.SimpleDateFormat

String versionNumber = ""

List<String> deployedIssueKeys = []

def sendEmail(Integer build, String url, String status) {
    String subject = "Development pipeline build: #${build} ${status == "SUCCESS" ? "succeeded" : "failed"}"
    emailext to: 'anthonyjsmith1993@gmail.com',
             subject: subject,
             body: "See build log here: ${url}"
}

Date parseDate(String d) {
    try {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(d)
    } catch (ParseException | NullPointerException ignored) {
        return null
    }
}

Map<String, Object> cfg = [
        'qa'  : [
                'cfBucketName' : 'easyvisa-cloudfront-qa',
                'ebBucketName' : 'elasticbeanstalk-easyvisa-qa',
                'neo4jCredName': 'qa-neo4j-user-password',
                'neo4jUrl'     : 'qa-neo4j-url',
                'jiraEnv'      : 'staging',
                'appName'      : 'EasyVisa-EB',
                'ebEnv'        : 'qualityassurance',
                'region'       : 'us-west-2'
        ],
        'dev' : [
                'cfBucketName' : 'easyvisa-cloudfront',
                'ebBucketName' : 'elasticbeanstalk-easyvisa-test',
                'neo4jCredName': 'dev-neo4j-user-password',
                'neo4jUrl'     : 'dev-neo4j-url',
                'jiraEnv'      : 'development',
                'appName'      : 'Development',
                'ebEnv'        : 'Development',
                'region'       : 'us-west-2'
        ],
        'prod': [
                'cfBucketName' : 'easyvisa-cloudfront-prod',
                'ebBucketName' : 'elasticbeanstalk-easyvisa-prod',
                'neo4jCredName': 'prod-neo4j-user-password',
                'neo4jUrl'     : 'prod-neo4j-url',
                'jiraEnv'      : 'production',
                'appName'      : 'EasyVisa-EB',
                'ebEnv'        : 'Production',
                'region'       : 'us-west-2'
        ]
]

cfg = cfg."${params.deploy_environment.toLowerCase()}"

pipeline {
    agent any
    options { disableConcurrentBuilds() }
    environment {
        CLOUDFRONT_DIST_ID = credentials("${params.deploy_environment.toLowerCase()}-cloudfront-distribution-id")
        DB_PASSWORD = credentials('ev-test-db-password')
        NEO4J_URL = credentials("${cfg.neo4jUrl}")
        NEO4J_CRED = credentials("${cfg.neo4jCredName}")
        AWS_DEFAULT_REGION = "${cfg.region}"
        JAVA_HOME = '/opt/amazon-corretto-11.0.13.8.1-linux-x64'
        PATH = "/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}"
    }
    stages {
        stage("Update Neo4j") {
            steps {
                script {
                    currentBuildDateString = sh(returnStdout: true, script: "cat /opt/easyvisa/builds/${params.build_no}/neo4j.txt").trim()
                    try {
                        lastUpdatedDateString = sh(returnStdout: true, script: "cat /opt/easyvisa/${params.deploy_environment}/neo4j-last.txt").trim()
                    } catch (ignored) {
                        sh "touch /opt/easyvisa/${params.deploy_environment}/neo4j-last.txt"
                        lastUpdatedDateString = null
                    }
                    currentBuildDate = parseDate(currentBuildDateString)
                    lastUpdatedDate = parseDate(lastUpdatedDateString)
                    shouldUpdate = currentBuildDate && lastUpdatedDate && (currentBuildDate > lastUpdatedDate) || !lastUpdatedDate

                    if (shouldUpdate) {
                        sh "cat /opt/easyvisa/builds/${params.build_no}/graph.cql | /opt/cypher-shell-4/cypher-shell -u ${NEO4J_CRED_USR} -p ${NEO4J_CRED_PSW} --address ${NEO4J_URL} --format plain >> /dev/null"
                        sh "echo '${currentBuildDateString}' >  /opt/easyvisa/${params.deploy_environment}/neo4j-last.txt"
                    } else {
                        sh "echo 'Neo4j is latest version!'"
                    }
                }
            }
        }
        stage("Create EB Application Version") {
            steps {
                script {
                    sh """
                        /usr/local/bin/aws elasticbeanstalk create-application-version --application-name \"${cfg.appName}\" \
                        --version-label \"${params.artifact_id}\" \
                        --source-bundle S3Bucket=${cfg.ebBucketName},S3Key=${params.artifact_id}-easyvisa.war \
                        --process
                        """.stripIndent()
                    boolean describeVersion = true
                    while (describeVersion) {
                        String status
                        status = sh(
                                script: """
                                /usr/local/bin/aws elasticbeanstalk describe-application-versions \
                                --application-name \"${cfg.appName}\" \
                                --version-label \"${params.artifact_id}\" \
                                --query \"ApplicationVersions[*].Status\" \
                                --output text
                                """.stripIndent(),
                                returnStdout: true
                        )
                        status = status.trim()
                        echo "Status: ${status}"
                        if ("PROCESSED".equalsIgnoreCase(status)) {
                            describeVersion = false
                        }
                        if (status == "FAILED") {
                            error "Creating application version ${params.artifact_id} in ${cfg.appName} failed"
                            break
                        }
                        echo "Continue Loop: ${describeVersion}"
                        sleep(time: 5000, unit: 'MILLISECONDS')
                    }
                }
            }
        }
        stage("Update EB Application Version") {
            steps {
                script {
                    sh """
                       /usr/local/bin/aws elasticbeanstalk update-application-version \
                       --version-label \"${params.artifact_id}\" \
                       --application-name \"${cfg.appName}\" \
                       --description \"Application version ${params.artifact_id}, deployed on \$(date)\"
                       """.stripIndent()
                }
            }
        }
        stage("Update EB Environment") {
            steps {
                script {
                    sh "/usr/local/bin/aws elasticbeanstalk update-environment --environment-name \"${cfg.ebEnv}\" --version-label \"${params.artifact_id}\""
                }
            }
        }
    }
    post {
        always {
            script {
                emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com ',
                        subject: "Development pipeline deploy: #${BUILD_NUMBER} ${currentBuild.currentResult == "SUCCESS" ? "succeeded" : "failed"}",
                        body: "See build log here: ${BUILD_URL}"
            }
        }
        success {
            script {
                latestCommit = sh(returnStdout: true, script: "cat /opt/easyvisa/builds/${params.build_no}/commitId.txt").trim()
                lastBuild = sh(returnStdout: true, script: "cat /opt/easyvisa/${params.deploy_environment}/last-successful-commit.txt").trim()
                String issueKeysText = sh(script: "git log ${lastBuild}..HEAD --pretty=format:%B | egrep -iwo 'ev-[0-9]+' || exit 0", returnStdout: true)
                deployedIssueKeys = issueKeysText ? issueKeysText
                        .split("\n")
                        .collect { it.toUpperCase() }
                        .unique()
                        : []
                sh "echo ${latestCommit} > /opt/easyvisa/${params.deploy_environment}/last-successful-commit.txt"
                println "Issues to be deployed -> ${deployedIssueKeys}"
            }
            jiraSendDeploymentInfo site: 'easyvisa.atlassian.net',
                    environmentId: cfg.jiraEnv,
                    environmentName: cfg.jiraEnv,
                    environmentType: cfg.jiraEnv,
                    issueKeys: deployedIssueKeys
        }
    }
}
