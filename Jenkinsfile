pipeline {
    agent none
    
    stages {
        stage('Delegar al Dispatcher') {
            steps {
                script {
                // Ejemplo de dispara un job remoto en el orquestador
                    build job: 'dispatcher/build-and-test',
                          parameters: [
                                  string(name: 'REPO_URL', value: env.GIT_URL),
                                  string(name: 'COMMIT', value: env.GIT_COMMIT),
                                  string(name: 'SERVICE_NAME', value: 'task-crud')
                          ],
                          wait: true
                }
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building...'
                sh 'mvn clean package -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Testing...'
                sh 'mvn test'
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline finished!'
        }
    }
}
