pipeline {
    agent any

    tools {
        maven 'Maven-3.8.1'
        jdk 'JDK-17'
    }

    environment {
        IMAGE_NAME = "manojchoudhary67/employee-api"
        BUILD_IMAGE = "${IMAGE_NAME}:${BUILD_NUMBER}"
        LATEST_IMAGE = "${IMAGE_NAME}:latest"
        CONTAINER_NAME = "employee-container"
        PORT = "8084"
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
                docker build -t %BUILD_IMAGE% .
                docker tag %BUILD_IMAGE% %LATEST_IMAGE%
                """
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat """
                    echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                    """
                }
            }
        }

        stage('Push Image') {
            steps {
                bat """
                docker push %BUILD_IMAGE%
                docker push %LATEST_IMAGE%
                """
            }
        }

        stage('Deploy Container') {
            steps {
                bat """
                docker rm -f %CONTAINER_NAME% 2>nul || echo Container not running

                docker run -d ^
                -p %PORT%:%PORT% ^
                --name %CONTAINER_NAME% ^
                %BUILD_IMAGE%

                docker ps -a
                """
            }
        }
    }

    post {
        success {
            echo '✅ Build, Push, and Deployment SUCCESSFUL'
        }
        failure {
            echo '❌ Pipeline FAILED - check logs above'
        }
        always {
            cleanWs()
        }
    }
}