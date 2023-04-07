String baseDir = "/opt/easyvisa/dev"
String buildCommitFile = "$baseDir/last-successful-commit.txt"

List<String> deployedIssueKeys = []
String latestCommit;

void sendBuildFailedEmail(send_notification){
if(send_notification){
    String emailSubject  = "Easyvisa-${app_env} backend build #${BUILD_NUMBER} Failed"
    emailext to: 'pcbalodi@gmail.com, philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, mr.engineer256@gmail.com, rizwanspeaks@gmail.com',
    subject: emailSubject,
    body: "See build log here - ${BUILD_URL}"
    }
}

void sendBuildStatusChangedEmail(send_notification){
    if(send_notification && currentBuild.currentResult=="SUCCESS"){
        String emailSubject  = "Easyvisa-${app_env} backend build #${BUILD_NUMBER} is now passing"
        emailext to: 'pcbalodi@gmail.com, philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, mr.engineer256@gmail.com, rizwanspeaks@gmail.com',
        subject: emailSubject,
        body: "See build log here - ${BUILD_URL}"
    }
}

Boolean envHasAPIDoc(String env){
    env=='dev'
}

String getNeo4jUrlPropertyName(String env){
    String neo4jUrl;
    switch(env){
            case 'qa': neo4jUrl='qa-neo4j-url'
            break;
            case 'dev': neo4jUrl='dev-neo4j-url'
            break;
            case 'prod': neo4jUrl='prod-neo4j-url'
            break;
    }
    neo4jUrl
}

String getGrailsEnvName(String env){
    String value;
    switch(env){
            case 'qa': value='qa'
            break;
            case 'dev': value='aws_development'
            break;
            case 'prod': value='production'
            break;
    }

    value
}

String getBranchNameForEnvironment(String env){
    String value;
    switch(env){
            case 'qa': value='master'
            break;
            case 'dev': value='development'
            break;
            case 'prod': value='master'
            break;
    }

    value
}

String getNeo4jCredPropertyName(String env){
    String value;
    switch(env){
            case 'qa': value='qa-neo4j-user-password'
            break;
            case 'dev': value='dev-neo4j-user-password'
            break;
            case 'prod': value='prod-neo4j-user-password'
            break;
    }
    value
}

String getS3BucketName(String env){
    String value;
    switch(env){
            case 'qa': value='elasticbeanstalk-easyvisa-qa'
            break;
            case 'dev': value='elasticbeanstalk-easyvisa-test'
            break;
            case 'prod': value='elasticbeanstalk-easyvisa-prod'
            break;
    }
    value
}


pipeline {
    agent any
    options { disableConcurrentBuilds() }
    environment{
        DB_PASSWORD=credentials('ev-test-db-password')
        NEO4J_URL = credentials("${getNeo4jUrlPropertyName(app_env)}")
        NEO4J_CRED =  credentials("${getNeo4jCredPropertyName(app_env)}")

    }
    stages {
        stage('Pull latest code') {
            steps{
                git url: 'git@bitbucket.org:Michael_Andrew/easyvisa.git',
                credentialsId: 'ev-git-ssh',
                 branch: getBranchNameForEnvironment(app_env)
            }
        }

        stage("Run API tests"){
             when {
                     expression { return !(params.fast_build) }
                  }
                  steps{
                sh """export DB_PASSWORD=${DB_PASSWORD};
                export DB_USER=easyvisa;
                export ROOT_DIR="./server";
                server/bin/setup-test-db.sh;"""
                sh "cat data-import/output/questionnaire-shell-data.cql | cypher-shell -u ${NEO4J_CRED_USR} -p ${NEO4J_CRED_PSW} --address ${NEO4J_URL} --format plain >> /dev/null"
                 sh "./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/${app_env}_test-config.yml -Dgrails.env=test -Xms512m -Xmx1024m' server:clean server:test server:integrationTest --rerun-tasks --no-daemon --tests 'com.easyvisa.test.api.*' --tests 'com.easyvisa.test.jobs.*'"
               }
              post {
                  always {
                   junit 'server/build/test*/**/*.xml'

                  }
              }
        }

          stage("Run Questionnaire tests"){
            when {
                     expression { return !(params.fast_build) }
                 }
            steps{
              sh """export DB_PASSWORD=${DB_PASSWORD};
                export DB_USER=easyvisa;
                export ROOT_DIR="./server";
                server/bin/setup-test-db.sh;"""

                sh "./gradlew -PjvmArgs='-Dlocal.config.location=/var/lib/jenkins/${app_env}_test-config.yml -Dgrails.env=test -Xms512m -Xmx1024m' server:test server:integrationTest --rerun-tasks --no-daemon --tests 'com.easyvisa.test.questionnaire.*'"

            }
            post {
                always {
                  junit 'server/build/test*/**/*.xml'

                }
            }
        }

         stage("Generate API doc"){
          when {
                              expression { return envHasAPIDoc(app_env) }
            }
            steps{
                sh './gradlew server:asciidoc --rerun-tasks --no-daemon --max-workers 2'
            }
        }


         stage("Build war"){
                    steps{
                      script {
                               latestCommit =  sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
                               lastBuild = sh(returnStdout: true, script: "cat ${buildCommitFile}").trim()
                               String issueKeysText = sh(script: "git log ${lastBuild}..HEAD --pretty=format:%B | egrep -iwo 'ev-[0-9]+' || exit 0", returnStdout:true)
                               deployedIssueKeys = issueKeysText? issueKeysText.split("\n"):[]
                               deployedIssueKeys = deployedIssueKeys.unique()
                        }
                        sh "./gradlew --no-daemon  --rerun-tasks --max-workers 2 -Dgrails.env=${getGrailsEnvName(app_env)} -Dorg.gradle.project.envName=${app_env} server:clean server:assemble"
                        s3Upload(file:'server/build/libs/easyvisa.war', bucket:"${getS3BucketName(app_env)}", path:'easyvisa.war')
                    }
                }

          stage("Deploy to EBS"){
                    steps{
                        build (job: 'ebsdeploy', parameters:[
                            [$class: 'StringParameterValue', name: 'NEWVER', value: "${env.BUILD_NUMBER}"],
                            [$class: 'StringParameterValue', name: 'FILENAME', value: "easyvisa.war"],
                            [$class: 'StringParameterValue', name: 'S3BKT', value: "${getS3BucketName(app_env)}"],
                            [$class: 'StringParameterValue', name: 'DEPLOYENV', value: "${app_env}"]
                            ],propagate:true)
                    }
                }
    }
    post{
        failure{
            sendBuildFailedEmail(params.send_notification)
        }
        changed{
            sendBuildStatusChangedEmail(params.send_notification)
        }
        success {
        sh "echo ${latestCommit} > ${baseDir}/last-successful-commit.txt"
         println "Sending Issues to JIRA for update - "
         println deployedIssueKeys
            jiraSendDeploymentInfo site: 'easyvisa.atlassian.net',
            environmentId: 'development',
             environmentName: 'development',
              environmentType: 'development',
              issueKeys: deployedIssueKeys
        }
    }
}
