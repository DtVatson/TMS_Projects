pipeline {
agent {label 'node1' }

tools {nodejs 'node'}

environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
    }

parameters {
        string(name: 'DOCKERHUB_USERNAME', description: 'DockerHub Username')
        string(name: 'USERNAME_HOST', description: 'Username Host')
        string(name: 'IP_HOST', description: 'Host IP')
        string(name: 'TOKEN', description: 'Host IP')
        string(name: 'CHAT_ID', description: 'Host IP')
    }

stages {
        stage("Clone code") {
            
            steps {
                echo "Clone code from the GitHub repository"
                git url: "https://github.com/DtVatson/TMS_Projects.git", branch: "main"
            }
        }
        
        stage('Install dependencies') {
            steps {
                sh 'cd /home/ec2-user/workspace/Test_Nodejs/apps/backend && npm install'
            }
            }
     
        stage('Test') {
            steps {
                sh 'cd /home/ec2-user/workspace/Test_Nodejs/apps/backend && npm test'
            }
        }
                
        stage("Build") {
            steps {
                echo "Building Image"
                sh "sudo docker build -t ${params.DOCKERHUB_USERNAME}/frontend:latest ./apps/frontend"
                sh "sudo docker build -t ${params.DOCKERHUB_USERNAME}/backend:latest ./apps/backend"
            }
        }
        
        stage("Push to DockerHub") {
           steps {
                echo "Push build image to DockerHub"
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | sudo docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                script{
                        sh """
                        curl -s -X POST https://api.telegram.org/bot${params.TOKEN}/sendMessage -d chat_id=${params.CHAT_ID} -d parse_mode="HTML" -d text="<b>Project</b> : POC \
                        <b>Build image</b> : OK "
                        """
                    }
                input "Pushing Docker images to Docker Hub?" 
                echo 'Pushing Docker images to Docker Hub...'

                sh "sudo docker tag ${params.DOCKERHUB_USERNAME}/backend:latest ${params.DOCKERHUB_USERNAME}/backend:1.0.0"
                sh "sudo docker tag ${params.DOCKERHUB_USERNAME}/frontend:latest ${params.DOCKERHUB_USERNAME}/frontend:1.0.0"
                
                sh "sudo docker push ${params.DOCKERHUB_USERNAME}/backend:latest"
                sh "sudo docker push ${params.DOCKERHUB_USERNAME}/frontend:latest"
                }
            }
           
                  stage("Deploy") {
          steps {
                script{
                        sh """
                        curl -s -X POST https://api.telegram.org/bot${params.TOKEN}/sendMessage -d chat_id=${params.CHAT_ID} -d parse_mode="HTML" -d text="<b>Project</b> : POC \
                        <b>Ready to deploy</b> : OK "
                        """
                    }
                input "Install new version on prod?"
                sshagent (credentials: ['prod_stage']) {
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} rm -rf TMS_Projects"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} git clone https://github.com/DtVatson/TMS_Projects.git"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker pull ${params.DOCKERHUB_USERNAME}/backend:latest"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker pull ${params.DOCKERHUB_USERNAME}/frontend:latest"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker-compose -f /home/ec2-user/TMS_Projects/apps/docker-compose.yml stop"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker-compose -f /home/ec2-user/TMS_Projects/apps/docker-compose.yml up -d"
          }
          }
        }
       
}
post {
        success {
            sh """
                curl -s -X POST https://api.telegram.org/bot${params.TOKEN}/sendMessage -d chat_id=${params.CHAT_ID} -d parse_mode="HTML" -d text="<b>Project</b> : POC "\"
                CI/CD Pipeline has completed "\"
                CI/CD Pipeline successfully executed. Great job!"
                """
        }
        failure {
            echo 'CI/CD Pipeline encountered errors. Check the logs for details.'
            sh """
            curl -s -X POST https://api.telegram.org/bot${params.TOKEN}/sendMessage -d chat_id= -d parse_mode="HTML" -d text="<b>Project</b> : POC "\"
            CI/CD Pipeline has completed "\"
            CI/CD Pipeline encountered errors. Check the logs for details."
            """    
        }
    }
}

