/**
 * This script deploys all artifacts to QA or Production.
 * It takes a build number as a parameter: params.build_no
 * This corresponds to a "master-test-and-build" job, which uses test-and-build.groovy.
 * By convention, the build number determines the artifacts to deploy.
 *
 * Note: Jenkins seems to dislike variables not interpolated via GStrings.
 */
import java.text.ParseException
import java.text.SimpleDateFormat

String evDir = "/opt/easyvisa"
String baseDir = "${evDir}/builds"
String artifactVersion = new File("/opt/easyvisa/builds/${params.build_no}/artifact-version").text.trim()

List<String> deployedIssueKeys = []

static Date parseDate(String d) {
    try {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(d)
    } catch (ParseException | NullPointerException ignored) {
        return null
    }
}

Map<String, Object> envConfig = [
        'qa'  : [
                'cfBucketName'    : 'qa-cloudfront-easyvisa',
                'cfDistributionId': 'EOVR4FKK7IO98',
                'ebBucketName'    : 'elasticbeanstalk-easyvisa-qa',
                'neo4jCredName'   : 'qa-neo4j-user-password',
                'neo4jUrl'        : 'qa-neo4j-url',
                'jiraEnv'         : 'staging',
                'appName'         : 'qualityassurance',
                'ebEnv'           : 'qualityassurance',
                'region'          : 'us-west-2'
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
                'cfBucketName' : 'prod-cloudfront-easyvisa',
                'ebBucketName' : 'elasticbeanstalk-easyvisa-prod',
                'neo4jCredName': 'prod-neo4j-user-password',
                'neo4jUrl'     : 'prod-neo4j-url',
                'jiraEnv'      : 'production',
                'appName'      : 'EasyVisa-EB',
                'ebEnv'        : 'Production',
                'region'       : 'us-west-2'
        ]
]

Map<String, String> cfg = envConfig[params.app_env.toLowerCase()]
if (!cfg) {
    error "Environment ${params.app_env} is invalid."
}

pipeline {
    agent any
    options { disableConcurrentBuilds() }
    environment {
        DB_PASSWORD = credentials('ev-test-db-password')
        NEO4J_URL = credentials("${cfg.neo4jUrl}")
        NEO4J_CRED = credentials("${cfg.neo4jCredName}")
        AWS_DEFAULT_REGION = "${cfg.region}"
        JAVA_HOME = '/opt/amazon-corretto-11.0.13.8.1-linux-x64'
        PATH = "/opt/amazon-corretto-11.0.13.8.1-linux-x64/bin:${env.PATH}"
    }
    stages {
        stage('Push War to S3') {
            steps {
                s3Upload(file: "/opt/easyvisa/builds/artifacts/$artifactVersion-${params.app_env}-easyvisa.war", bucket: "${cfg.ebBucketName}", path: "$artifactVersion-${params.app_env}-easyvisa.war")
            }
        }
        stage("Update Neo4j") {
            steps {
                script {
                    currentBuildDateString = sh(returnStdout: true, script: "cat $baseDir/${params.build_no}/neo4j.txt").trim()
                    try {
                        lastUpdatedDateString = sh(returnStdout: true, script: "cat /opt/easyvisa/${params.app_env}/neo4j-last.txt").trim()
                    } catch (ignored) {
                        sh "touch /opt/easyvisa/${params.app_env}/neo4j-last.txt"
                        lastUpdatedDateString = null
                    }
                    currentBuildDate = parseDate(currentBuildDateString)
                    lastUpdatedDate = parseDate(lastUpdatedDateString)
                    shouldUpdate = currentBuildDate && lastUpdatedDate && (currentBuildDate > lastUpdatedDate) || !lastUpdatedDate

                    if (shouldUpdate) {
                        sh "cat /opt/easyvisa/builds/${params.build_no}/graph.cql | /opt/cypher-shell-4/cypher-shell -u ${NEO4J_CRED_USR} -p ${NEO4J_CRED_PSW} --address ${NEO4J_URL} --format plain >> /dev/null"
                        sh "echo '${currentBuildDateString}' >  ${evDir}/${params.app_env}/neo4j-last.txt"
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
                        --version-label \"$artifactVersion\" \
                        --source-bundle S3Bucket=${cfg.ebBucketName},S3Key="${artifactVersion}-${params.app_env}-easyvisa.war" \
                        --process
                        """.stripIndent()
                    boolean describeVersion = true
                    int retries = 0
                    while (describeVersion && retries < 6) {
                        String status
                        status = sh(
                                script: """
                                /usr/local/bin/aws elasticbeanstalk describe-application-versions \
                                --application-name \"${cfg.appName}\" \
                                --version-label \"$artifactVersion\" \
                                --query \"ApplicationVersions[*].Status\" \
                                --output text
                                """.stripIndent(),
                                returnStdout: true
                        )
                        status = status.trim()
                        echo "Status: ${status}"
                        if ("PROCESSED".equalsIgnoreCase(status)) {
                            describeVersion = false
                            break
                        }
                        if (status == "FAILED") {
                            error "Creating application version $artifactVersion in ${cfg.appName} failed"
                            break
                        }
                        echo "Continue Loop: ${describeVersion}"
                        retries++
                        sleep(time: 30, unit: 'SECONDS')
                    }
                    if (describeVersion) {
                        error "Version status did not transition to PROCESSED within 5 minutes."
                    }
                }
            }
        }
        stage("Update EB Application Version") {
            steps {
                script {
                    sh """
                       /usr/local/bin/aws elasticbeanstalk update-application-version \
                       --version-label \"$artifactVersion\" \
                       --application-name \"${cfg.appName}\" \
                       --description \"Application version $artifactVersion, deployed on \$(date)\"
                       """.stripIndent()
                }
            }
        }
        stage("Update EB Environment") {
            steps {
                script {
                    sh "/usr/local/bin/aws elasticbeanstalk update-environment --environment-name \"${cfg.ebEnv}\" --version-label \"$artifactVersion\""
                }
            }
        }
        stage("Push Frontend build to Cloudfront") {
            steps {
                s3Upload(bucket: "${cfg.cfBucketName}", includePathPattern: '**/*', workingDir: "/opt/easyvisa/builds/${params.build_no}/frontend-${params.app_env}")
            }
        }

        stage("Refresh Cloudfront cache") {
            steps {
                sh "/usr/local/bin/aws cloudfront create-invalidation --distribution-id ${cfg.cfDistributionId} --paths '/*'"
            }
        }
    }
    post {
        failure {
            emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com',
                     subject: "Deployment of ${cfg.ebEnv} environment: #${BUILD_NUMBER} ${currentBuild.currentResult == "SUCCESS" ? "succeeded" : "failed"}",
                     body: "See build log here: ${BUILD_URL}"
        }
        changed {
            emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com, toants.301@gmail.com ',
                     subject: "Deployment of ${cfg.ebEnv} environment: #${BUILD_NUMBER} ${currentBuild.currentResult == "SUCCESS" ? "succeeded" : "failed"}",
                     body: "See build log here: ${BUILD_URL}"
        }
        success {
            script {
                latestCommit = sh(returnStdout: true, script: "cat /opt/easyvisa/builds/${params.build_no}/commitId.txt").trim()
                lastBuild = sh(returnStdout: true, script: "cat /opt/easyvisa/${params.app_env}/last-successful-commit.txt").trim()
                String issueKeysText = sh(script: "git log ${lastBuild}..HEAD --pretty=format:%B | egrep -iwo 'ev-[0-9]+' || exit 0", returnStdout: true)
                deployedIssueKeys = issueKeysText ? issueKeysText
                        .split("\n")
                        .collect { it.toUpperCase() }
                        .unique()
                        : []
                sh "echo ${latestCommit} > /opt/easyvisa/${params.app_env}/last-successful-commit.txt"
                println "Issues to be deployed -> ${deployedIssueKeys}"
            }
            jiraSendDeploymentInfo site: 'easyvisa.atlassian.net',
                                   environmentId: "${cfg.jiraEnv}",
                                   environmentName: "${cfg.jiraEnv}",
                                   environmentType: "${cfg.jiraEnv}",
                                   issueKeys: deployedIssueKeys
        }
    }
}
