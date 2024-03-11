pipeline {
agent {label 'node1' }

tools {nodejs 'node'}

parameters {
        string(name: 'USERNAME_HOST', description: 'Username Host')
        string(name: 'IP_HOST', description: 'Host IP')
        string(name: 'TOKEN', description: 'Token telegram bot')
        string(name: 'CHAT_ID', description: 'Chat id telegram bot')
        string(name: 'AWS_ACCOUNT_ID', description: 'Account id AWS')
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
                sh "sudo docker build -t backend_dos_15_kepets ./apps/backend"
                sh "sudo docker build -t frontend_dos_15_kepets ./apps/frontend"
            }
        }
        
        stage("Push to ECR AWS") {
           steps {
                echo "Push build image to ECR AWS"
                script{
                        sh """
                        curl -s -X POST https://api.telegram.org/bot${params.TOKEN}/sendMessage -d chat_id=${params.CHAT_ID} -d parse_mode="HTML" -d text="<b>Project</b> : POC \
                        <b>Build image</b> : OK "
                        """
                    }
                input "Pushing Docker images to ECR AWS?" 
                echo 'Pushing Docker images to ECR AWS...'
                sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com"
                
                sh "docker tag backend_dos_15_kepets:latest ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/backend_dos_15_kepets:latest"
                sh "docker tag frontend_dos_15_kepets:latest ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/frontend_dos_15_kepets:latest"
                
                sh "docker push ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/backend_dos_15_kepets:latest"
                sh "docker push ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/frontend_dos_15_kepets:latest"
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
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker pull ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/backend_dos_15_kepets:latest"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker pull ${params.AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/frontend_dos_15_kepets:latest"
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
            sh """
            curl -s -X POST https://api.telegram.org/bot${params.TOKEN}/sendMessage -d chat_id=${params.CHAT_ID} -d parse_mode="HTML" -d text="<b>Project</b> : POC "\"
            CI/CD Pipeline has completed "\"
            CI/CD Pipeline encountered errors. Check the logs for details."
            """    
        }
    }
}

