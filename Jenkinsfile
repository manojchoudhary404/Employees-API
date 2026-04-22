pipeline {
  agent any

  tools {
    maven 'Maven-3.9'
    jdk 'JDK-17'
  }

  environment {
    DOCKER_IMAGE = 'manojchoudhary67/employee-api'
    DOCKER_TAG   = "${BUILD_NUMBER}"
  }

  stages {

    stage('Checkout') {
      steps {
        git branch: 'main',
            url: 'https://github.com/manojchoudhary404/Employees-API.git'
      }
    }

    stage('Build & Test') {
      steps {
        echo '🔨 Building & Testing...'
        bat 'mvn clean test -B'
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }

    stage('Docker Build') {
      steps {
        echo '🐳 Building Docker Image...'
        bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
        bat "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
      }
    }

    stage('Docker Login & Push') {
      steps {
        echo '🔐 Logging in & pushing image...'
        withCredentials([usernamePassword(
            credentialsId: 'dockerhub-credentials',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            bat '''
            echo %DOCKER_PASS%> docker-pass.txt
            docker login -u %DOCKER_USER% --password-stdin < docker-pass.txt
            del docker-pass.txt

            docker push %DOCKER_IMAGE%:%DOCKER_TAG%
            docker push %DOCKER_IMAGE%:latest
            '''
        }
      }
    }

    stage('Deploy') {
      steps {
        echo '🚀 Deploying container...'

        bat "docker stop springboot-app || exit 0"
        bat "docker rm springboot-app || exit 0"

        bat "docker run -d -p 8084:8084 --name springboot-app ${DOCKER_IMAGE}:latest"
      }
    }
  }

  post {
    success { 
      echo '✅ Pipeline succeeded! App running on http://localhost:8484'
    }
    failure { 
      echo '❌ Pipeline failed — check logs!' 
    }
  }
}