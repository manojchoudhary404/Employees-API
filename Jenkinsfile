pipeline {
    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    environment {
        DOCKER_IMAGE = "manojchoudhary67/employee-api:${BUILD_NUMBER}"
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
                    credentialsId: 'docker-credentails',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat """
                    docker login -u %DOCKER_USER% -p %DOCKER_PASS%
                    docker push %DOCKER_IMAGE%
                    """
                }
            }
        }

       stage('Run Container') {
        steps {
            bat '''
            docker stop employee-container
            docker rm employee-container
            docker run -d -p 8184:8184 --name employee-container %DOCKER_IMAGE%
            docker ps -a
            '''
                }
        }
   }

    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed. Check logs above.'
        }
    }