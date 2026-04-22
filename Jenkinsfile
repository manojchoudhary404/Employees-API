pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        GIT_REPO = 'https://github.com/manojchoudhary404/Employees-API.git'
        BRANCH = 'main'

        IMAGE_NAME = "manojchoudhary67/employee-api"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo "📥 Cloning GitHub repo..."
                git branch: "${BRANCH}", url: "${GIT_REPO}"
            }
        }

        stage('Verify Tools') {
            steps {
                bat '''
                java -version
                mvn -v
                docker version
                '''
            }
        }

        stage('Build Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat """
                docker build -t %IMAGE_NAME%:%IMAGE_TAG% .
                docker tag %IMAGE_NAME%:%IMAGE_TAG% %IMAGE_NAME%:latest
                """
            }
        }

        stage('Login Docker Hub') {
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
                docker push %IMAGE_NAME%:%IMAGE_TAG%
                docker push %IMAGE_NAME%:latest
                """
            }
        }

        stage('Deploy Container') {
            steps {
                bat """
                docker stop springboot-app || exit 0
                docker rm springboot-app || exit 0
                docker run -d -p 8084:8084 --name springboot-app %IMAGE_NAME%:latest
                """
            }
        }
    }

    post {
        success {
            echo '✅ SUCCESS: App running on http://localhost:8084'
        }
        failure {
            echo '❌ Pipeline failed — check logs'
        }
    }
}