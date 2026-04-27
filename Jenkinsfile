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
                bat """
                docker build -t %DOCKER_IMAGE% .
                docker tag %DOCKER_IMAGE% manojchoudhary67/employee-api:latest
                """
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat """
                    echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                    docker push %DOCKER_IMAGE%
                    docker push manojchoudhary67/employee-api:latest
                    docker logout
                    """
                }
            }
        }

        stage('Deploy Container') {
            steps {
                bat """
                docker rm -f %CONTAINER_NAME% || echo Not running
                docker run -d -p 8084:8084 --name %CONTAINER_NAME% %DOCKER_IMAGE%
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