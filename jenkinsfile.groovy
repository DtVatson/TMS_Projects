pipeline {
    agent {
        kubernetes {
        defaultContainer 'docker'
        yaml """
apiVersion: v1
kind: Pod
spec:
  serviceAccountName: jenkins
  containers:
    - name: docker
      image: twnsnd2team/docker:v2
      env:
        - name: NEXT_PUBLIC_API_URL
          value: 'https://dev.wowplaces.online/api/'
        - name: API_URL
          value: 'https://dev.wowplaces.online/api/'
        - name: NEXT_PUBLIC_GOOGLE_MAPS_API
          value: 'test'
      resources:
        limits:
          memory: "256Mi"
          cpu: "400m"
      command:
        - sleep
        - "1d"
      volumeMounts:
        - name: dockersock
          mountPath: /var/run/docker.sock
          readOnly: false
  volumes:
    - name: dockersock
      hostPath:
        path: /var/run/docker.sock
    - name: kubeconfig
      secret:
        secretName: jenkins-agent
                """
        }
    }     

    stages {
        stage('Delete workspace before build starts') {
            steps {
                echo 'Deleting workspace 1'
            }
        }
        
        stage ('Сhange variables') {
            steps {
                    sh "ls -la"
            }
        }

        stage ('Deploy'){
             when {
                branch "main"
            }
            steps {
            echo "deploy 4"
            }
        }
    }
}

        

    

