pipeline {
    agent any
    options { disableConcurrentBuilds() }
    stages {
        stage("Deploy build") {
            steps {
                build(job: 'deploy-app', parameters: [
                        [$class: 'StringParameterValue', name: 'app_env', value: 'qa'],
                        [$class: 'StringParameterValue', name: 'build_no', value: "${params.build_no}"],
                ], propagate: true)
            }
        }
    }
}