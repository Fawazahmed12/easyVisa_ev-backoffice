pipeline {
    agent any
    options { disableConcurrentBuilds() }
    stages {
        stage("Deploy build") {
            steps {
                build(job: 'deploy-app', parameters: [
                        [$class: 'StringParameterValue', name: 'app_env', value: 'prod'],
                        [$class: 'StringParameterValue', name: 'build_no', value: "${params.build_no}"],
                        [$class: 'StringParameterValue', name: 'build_no', value: "${params.artifact_id}"]
                ], propagate: true)
            }
        }
    }
}