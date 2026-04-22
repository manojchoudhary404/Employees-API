pipeline {
    agent any
    
    tools {
        maven 'Maven'  
    }
    
    environment {
        DOCKER_IMAGE = 'manojchoudhary67/employee-api'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/manojchoudhary404/Employees-API.git',
                    credentialsId: 'github-credentails'
            }
        }

        stage('Check Docker') {
            steps {
                bat '''
                    echo Checking Docker installation...
                    docker --version
                    if %errorlevel% neq 0 (
                        echo Docker not installed!
                        exit /b 1
                    )
                    
                    echo Checking Docker daemon...
                    docker ps
                    if %errorlevel% neq 0 (
                        echo ========================================
                        echo ERROR: Docker daemon is not running!
                        echo ========================================
                        echo Please start Docker Desktop first.
                        echo Then restart Jenkins service.
                        echo ========================================
                        exit /b 1
                    )
                    echo Docker is ready!
                '''
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat "docker build -t %DOCKER_IMAGE%:%DOCKER_TAG% ."
                bat "docker tag %DOCKER_IMAGE%:%DOCKER_TAG% %DOCKER_IMAGE%:latest"
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([string(credentialsId: 'github-credentials', variable: 'DOCKER_TOKEN')]) {
                    bat '''
                        echo %DOCKER_TOKEN% | docker login -u manojchoudhary67 --password-stdin
                        docker push %DOCKER_IMAGE%:%DOCKER_TAG%
                        docker push %DOCKER_IMAGE%:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                bat """
                    docker stop springboot-app || exit 0
                    docker rm springboot-app || exit 0
                    docker run -d -p 8084:8084 --name springboot-app %DOCKER_IMAGE%:latest
                """
            }
        }
    }

    post {
        success { 
            echo '========================================'
            echo '✅ Pipeline completed successfully!'
            echo '🌐 Application: http://localhost:8084'
            echo '========================================'
        }
        failure { 
            echo '========================================'
            echo '❌ Pipeline failed!'
            echo '========================================'
        }
    }
}
