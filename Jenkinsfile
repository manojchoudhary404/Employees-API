pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'
        DOCKER_HUB_USERNAME = 'manojchoudhary67'
        IMAGE_NAME = "${DOCKER_HUB_USERNAME}/employee-api"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo '📥 Cloning repository...'
                checkout scm
            }
        }

        stage('Build Application') {
            steps {
                echo '🔨 Building Spring Boot app...'
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Run Tests') {
            steps {
                echo '🧪 Running tests...'
                bat 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "🐳 Building Docker image ${IMAGE_NAME}:${IMAGE_TAG}"
                bat "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                bat "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
            }
        }

        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: "${DOCKER_CREDENTIALS_ID}",
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo '🚀 Pushing image to Docker Hub...'
                bat "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                bat "docker push ${IMAGE_NAME}:latest"
            }
        }
    }

    post {
        success {
            echo """
            ======================================
            ✅ PIPELINE SUCCESS
            Image: ${IMAGE_NAME}:${IMAGE_TAG}
            ======================================
            """
        }

        failure {
            echo "❌ PIPELINE FAILED"
        }

        always {
            echo '🧹 Cleaning workspace...'
            bat "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || exit 0"
            bat "docker rmi ${IMAGE_NAME}:latest || exit 0"
            cleanWs()
        }
    }
}