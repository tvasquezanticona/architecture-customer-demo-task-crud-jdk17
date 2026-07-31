pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Delegar al Dispatcher') {
            steps {
                script {
                    build job: '/dispatcher/build-and-test',
                            parameters: [
                                    string(
                                            name: 'REPO_URL',
                                            value: env.GIT_URL
                                    ),
                                    string(
                                            name: 'COMMIT',
                                            value: env.GIT_COMMIT
                                    ),
                                    string(
                                            name: 'SERVICE_NAME',
                                            value: 'task-crud'
                                    )
                            ],
                            wait: true,
                            propagate: true
                }
            }
        }
    }

    post {
        success {
            echo 'El Dispatcher terminó correctamente'
        }

        failure {
            echo 'El Dispatcher o alguno de sus pasos falló'
        }

        always {
            echo 'Pipeline principal finalizado'
        }
    }
}