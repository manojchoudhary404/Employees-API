pipeline {

    agent any

    tools {
        maven 'Maven-3.8.1'
        jdk 'JDK-17'
    }

    environment {
        DOCKER_HUB_USER = "manojchoudhary67"
        IMAGE_NAME = "manojchoudhary67/employee-api"
        BUILD_IMAGE = "manojchoudhary67/employee-api:${BUILD_NUMBER}"
        LATEST_IMAGE = "manojchoudhary67/employee-api:latest"
        CONTAINER_NAME = "employee-container"
        APP_PORT = "8084"
    }

    stages {

        // ─────────────────────────────────────────
        // STAGE 1 : Checkout Code from GitHub
        // ─────────────────────────────────────────
        stage('Checkout Code') {
            steps {
                echo '📥 Pulling code from GitHub...'
                git branch: 'main',
                    credentialsId: 'github-credentails',
                    url: 'https://github.com/manojchoudhary404/Employees-API.git'
                echo '✅ Code checkout successful!'
            }
        }

        // ─────────────────────────────────────────
        // STAGE 2 : Build JAR with Maven
        // ─────────────────────────────────────────
        stage('Build Application') {
            steps {
                echo '🔨 Building Spring Boot application...'
                bat 'mvn clean package -DskipTests'
                echo '✅ JAR file created successfully!'
            }
        }

        // ─────────────────────────────────────────
        // STAGE 3 : Run Tests
        // ─────────────────────────────────────────
        stage('Run Tests') {
            steps {
                echo '🧪 Running unit tests...'
                bat 'mvn test'
                echo '✅ All tests passed!'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        // ─────────────────────────────────────────
        // STAGE 4 : Build Docker Image
        // ─────────────────────────────────────────
        stage('Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                bat "docker build -t %BUILD_IMAGE% ."
                bat "docker tag %BUILD_IMAGE% %LATEST_IMAGE%"
                echo '✅ Docker image built successfully!'
            }
        }

        // ─────────────────────────────────────────
        // STAGE 5 : Push Image to Docker Hub
        // ─────────────────────────────────────────
        stage('Push to Docker Hub') {
            steps {
                echo '☁️ Pushing image to Docker Hub...'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    // Windows bat uses -p flag (password-stdin not supported on Windows)
                    bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"
                    bat "docker push %BUILD_IMAGE%"
                    bat "docker push %LATEST_IMAGE%"
                    bat "docker logout"
                }
                echo '✅ Image pushed to Docker Hub!'
            }
        }

        // ─────────────────────────────────────────
        // STAGE 6 : Deploy Container Locally
        // ─────────────────────────────────────────
        stage('Deploy Container') {
            steps {
                echo '🚀 Deploying container...'

                // Stop and remove old container if it exists
                bat "docker stop %CONTAINER_NAME% || echo Container was not running"
                bat "docker rm %CONTAINER_NAME% || echo Container did not exist"

                // Run new container
                bat "docker run -d -p %APP_PORT%:%APP_PORT% --name %CONTAINER_NAME% %LATEST_IMAGE%"

                // Show running containers
                bat "docker ps"

                echo '✅ Container deployed on port 8080!'
            }
        }

    }

    // ─────────────────────────────────────────
    // POST ACTIONS
    // ─────────────────────────────────────────
    post {
        success {
            echo '🎉 ✅ Pipeline SUCCESSFUL!'
            echo '👉 App running at: http://localhost:8084/employees'
            echo '👉 Docker Hub : https://hub.docker.com/r/manojchoudhary67/employee-api'
        }
        failure {
            echo '❌ Pipeline FAILED — check the stage logs above'
        }
        always {
            // Clean workspace after every build
            cleanWs()
        }
    }
}