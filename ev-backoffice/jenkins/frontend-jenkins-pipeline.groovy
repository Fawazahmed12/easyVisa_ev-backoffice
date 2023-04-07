String getCloudFrontBucketName(String env){
    String value;
    switch(env){
        case 'qa': value='easyvisa-cloudfront-qa'
            break;
        case 'dev': value='easyvisa-cloudfront'
            break;
        case 'prod': value='easyvisa-cloudfront-prod'
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

String getCloudfrontDistIdKey(String env){
    "${env}-cloudfront-distribution-id"
}


pipeline {
    agent any
    tools {nodejs 'node-default'}

    environment{
        CLOUDFRONT_DIST_ID =  credentials("${getCloudfrontDistIdKey(app_env)}")
    }
    stages {
        stage('Pull latest code') {
            steps{
                git url: 'git@bitbucket.org:Michael_Andrew/easyvisa.git',
                        credentialsId: 'ev-git-ssh',
                        branch: params.BRANCH_TO_BUILD
            }
        }
        stage("Run tests"){
            steps{
                wrap([$class: 'Xvfb']) {
                    sh 'node --version'
                    sh 'npm --version'
                    sh 'rm client/package-lock.json'
                    sh 'rm -rf client/node_modules'
                    sh './gradlew client:test --rerun-tasks --no-daemon --max-workers 2'
                }
            }
        }
        stage("Build Package"){
            steps{
                sh 'rm client/package-lock.json'
                sh 'rm -rf client/node_modules'
                sh "./gradlew --no-daemon --max-workers 2 client:build_${app_env} --rerun-tasks"
                s3Upload(bucket:"${getCloudFrontBucketName(app_env)}", includePathPattern:'**/*', workingDir:'client/dist')
            }
        }
        stage("Refresh Cloudfront cache"){
            steps{
                sh "/usr/local/bin/aws cloudfront create-invalidation --distribution-id ${CLOUDFRONT_DIST_ID} --paths '/*'"
            }
        }
    }
    post {
        failure {
            script {
                script {
                    emailext to: 'philip.yurchuk@madeupname.com, ashrafthahir@gmail.com, abhayweb@gmail.com, marzooka@provility.com, rizwanspeaks@gmail.com ',
                            subject: "Development pipeline deploy: #${BUILD_NUMBER} ${currentBuild.currentResult == "SUCCESS" ? "succeeded" : "failed"}",
                            body: "See build log here: ${BUILD_URL}"
                }
            }
        }
    }
}
