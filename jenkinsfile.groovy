pipeline {
    
    agent {
        label 'node1'
    }
    
    stages {
        stage("Clone code") {
            steps {
                echo "Clone code from the GitHub repository"
                git url: "https://github.com/DtVatson/TMS_Projects.git", branch: "main"
            }
        }
        
        stage("Build") {
            steps {
                echo "Building Image"
                sh "sudo docker build -t ${params.DOCKERHUB_USERNAME}/frontend:latest ./frontend"
                sh "sudo docker build -t ${params.DOCKERHUB_USERNAME}/backend:latest ./backend"
            }
        }
        
        stage("Scan") {
            steps {
                echo "Scan Image with Trivy"
                sh "trivy image ${params.DOCKERHUB_USERNAME}/frontend:latest --scanners vuln"
                sh "trivy image ${params.DOCKERHUB_USERNAME}/backend:latest --scanners vuln"
            }
        }
        
        stage("Push to DockerHub") {
            steps {
                echo "Push build image to DockerHub"
                 sh 'echo $DOCKERHUB_CREDENTIALS_PSW | sudo docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                
                echo 'Pushing Docker images to Docker Hub...'

                sh "sudo docker tag ${params.DOCKERHUB_USERNAME}/backend:latest ${params.DOCKERHUB_USERNAME}/backend:1.0.0"
                sh "sudo docker tag ${params.DOCKERHUB_USERNAME}/frontend:latest ${params.DOCKERHUB_USERNAME}/frontend:1.0.0"
                
                sh "sudo docker push ${params.DOCKERHUB_USERNAME}/backend:latest"
                sh "sudo docker push ${params.DOCKERHUB_USERNAME}/frontend:latest"
                }
            }
                
       stage("Deploy") {
          steps {
                sshagent (credentials: ['vm-id']) {
                        sh "ssh -o StrictHostKeyChecking=no git clone https://github.com/DtVatson/TMS_Projects.git"
                        sh "ssh -o StrictHostKeyChecking=no -p docker pull ${dockerhubUsername}/frontend:latest"
                        sh "ssh -o StrictHostKeyChecking=no -p docker pull ${dockerhubUsername}/backend:latest"
                        sh "ssh -o StrictHostKeyChecking=no cd /apps && docker-compose up -b"
          }
          }
        }
    }
}