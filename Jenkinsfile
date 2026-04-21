pipeline {
    agent any

    // ── Environment Variables ────────────────────────────────────────────────
    environment {
        DOCKER_CREDENTIALS_ID = 'dockerhub-credentials'   // ID set in Jenkins Credentials
        DOCKER_HUB_USERNAME   = 'your-dockerhub-username' // ← Change this to your Docker Hub username
        IMAGE_NAME            = "${DOCKER_HUB_USERNAME}/employee-api"
        IMAGE_TAG             = "${BUILD_NUMBER}"          // e.g. "42" from Jenkins build number
    }

    // ── Tool Versions ────────────────────────────────────────────────────────
    tools {
        maven 'Maven-3.9'   // Must match name in: Jenkins > Manage > Global Tool Configuration
        jdk   'JDK-17'      // Must match name in: Jenkins > Manage > Global Tool Configuration
    }

    stages {

        // ── Stage 1: Checkout ────────────────────────────────────────────────
        stage('Checkout Code') {
            steps {
                echo '📥 Checking out source from GitHub...'
                checkout scm
                echo "Branch: ${env.GIT_BRANCH}"
                echo "Commit: ${env.GIT_COMMIT}"
            }
        }

        // ── Stage 2: Build ───────────────────────────────────────────────────
        stage('Build Application') {
            steps {
                echo '🔨 Building with Maven (skipping tests here)...'
                sh 'mvn clean package -DskipTests -B'
            }
            post {
                success {
                    echo '✅ Build successful — JAR created.'
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
                failure {
                    echo '❌ Build failed. Check the Maven output above.'
                }
            }
        }

        // ── Stage 3: Run Tests ───────────────────────────────────────────────
        stage('Run Tests') {
            steps {
                echo '🧪 Running unit and integration tests...'
                sh 'mvn test -B'
            }
            post {
                always {
                    // Publish JUnit test results in Jenkins UI
                    junit 'target/surefire-reports/*.xml'
                }
                success {
                    echo '✅ All tests passed!'
                }
                failure {
                    echo '❌ Tests failed — pipeline stopped.'
                }
            }
        }

        // ── Stage 4: Build Docker Image ──────────────────────────────────────
        stage('Build Docker Image') {
            steps {
                echo "🐳 Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}"
                sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} ."
                sh "docker tag  ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_NAME}:latest"
                sh "docker images ${IMAGE_NAME}"
            }
        }

        // ── Stage 5: Push to Docker Hub ──────────────────────────────────────
        stage('Push Image to Docker Hub') {
            steps {
                echo "🚀 Pushing ${IMAGE_NAME}:${IMAGE_TAG} to Docker Hub..."
                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKER_CREDENTIALS_ID}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    // Login (password via stdin — never echoed in logs)
                    sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'
                    // Push both tags
                    sh "docker push ${IMAGE_NAME}:${IMAGE_TAG}"
                    sh "docker push ${IMAGE_NAME}:latest"
                    echo "✅ Image pushed: ${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "✅ Image pushed: ${IMAGE_NAME}:latest"
                }
            }
            post {
                always {
                    // Always logout — even on failure
                    sh 'docker logout'
                }
            }
        }
    }

    // ── Post-pipeline Actions ────────────────────────────────────────────────
    post {
        success {
            echo """
            ╔══════════════════════════════════════╗
            ║  ✅  Pipeline completed successfully  ║
            ║  Image: ${IMAGE_NAME}:${IMAGE_TAG}
            ╚══════════════════════════════════════╝
            """
        }
        failure {
            echo '❌ Pipeline FAILED. Review the stage logs above.'
        }
        always {
            // Remove local Docker images to free disk space
            sh "docker rmi ${IMAGE_NAME}:${IMAGE_TAG} || true"
            sh "docker rmi ${IMAGE_NAME}:latest       || true"
            cleanWs()
        }
    }
}
