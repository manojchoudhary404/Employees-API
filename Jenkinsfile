pipeline {
    agent any

    tools {
        maven 'Maven-3.9.9'
        jdk 'JDK-17'
    }

    environment {
        DOCKER_IMAGE = "manojchoudhary67/employee-api:${BUILD_NUMBER}"
        CONTAINER_NAME = "employee-container"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    credentialsId: 'github-credentails',
                    url: 'https://github.com/manojchoudhary404/Employees-API.git'
            }
        }

        stage('Build Application') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %DOCKER_IMAGE% .'
            }
        }

        stage('Verify Docker Image') {
            steps {
                bat 'docker images'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',   // ✅ fixed
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat """
                    echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                    docker push %DOCKER_IMAGE%
                    docker logout
                    """
                }
            }
        }

        stage('Deploy Container') {
            steps {
                bat """
                docker stop %CONTAINER_NAME% || echo Not running
                docker rm %CONTAINER_NAME% || echo Not exists
                docker run -d -p 8184:8184 --name %CONTAINER_NAME% %DOCKER_IMAGE%
                docker ps -a
                """
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline executed successfully!'
        }
        failure {
            echo '❌ Pipeline failed. Check logs above.'
        }
        always {
            cleanWs()
        }
    }
}