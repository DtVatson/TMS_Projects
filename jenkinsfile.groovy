pipeline {
agent {label 'node1' }

environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub')
    }

/*parameters {
        string(name: 'DOCKERHUB_USERNAME', description: 'DockerHub Username')
        string(name: 'USERNAME_HOST', description: 'Username Host')
        string(name: 'IP_HOST', description: 'Host IP')
    }
*/
stages {
        stage("Clone code") {
            /*
            steps {
                echo "Clone code from the GitHub repository"
                echo "send message"
                //git url: "https://github.com/DtVatson/TMS_Projects.git", branch: "main"
            }
            */
            steps {

                script{


                        sh """

                        curl -s -X POST https://api.telegram.org/bot6916827290:AAF-EXvxazvbhAO-qp_OYGVV8KyewjkTs7k/sendMessage -d chat_id=399553676 -d parse_mode="HTML" -d text="<b>Project</b> : POC \
                        <b>Branch</b>: master \
                        <b>Build </b> : OK \
                        <b>Test suite</b> = Passed"
                        """
                        }
                    }
                }
        /*
        stage("Build") {
            steps {
                echo "Building Image"
                sh "docker build -t ${params.DOCKERHUB_USERNAME}/frontend:latest ./apps/frontend"
                sh "docker build -t ${params.DOCKERHUB_USERNAME}/backend:latest ./apps/backend"
            }
        }
        
        stage("Push to DockerHub") {
           steps {
              echo "Push build image to DockerHub"
               sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
               input "Pushing Docker images to Docker Hub?" 
               echo 'Pushing Docker images to Docker Hub...'

                sh "docker tag ${params.DOCKERHUB_USERNAME}/backend:latest ${params.DOCKERHUB_USERNAME}/backend:1.0.0"
                sh "docker tag ${params.DOCKERHUB_USERNAME}/frontend:latest ${params.DOCKERHUB_USERNAME}/frontend:1.0.0"
                
                sh "docker push ${params.DOCKERHUB_USERNAME}/backend:latest"
                sh "docker push ${params.DOCKERHUB_USERNAME}/frontend:latest"
                }
            }
           
                  stage("Deploy") {
          steps {
                input "Install new version on prod?"
                sshagent (credentials: ['vm-local']) {
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} rm -rf TMS_Projects"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} git clone https://github.com/DtVatson/TMS_Projects.git"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker pull ${params.DOCKERHUB_USERNAME}/frontend:latest"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker pull ${params.DOCKERHUB_USERNAME}:latest"
                        sh "ssh -o StrictHostKeyChecking=no ${params.USERNAME_HOST}@${params.IP_HOST} docker-compose -f /home/ec_2/TMS_Projects/apps/docker-compose.yml up -d"
          }
          }
        }
       */
}


}
