pipeline {
    agent any
    environment {     
        DOCKERHUB_CREDENTIALS = credentials('docker_cred')
    } 

    stages{
        stage("Build Maven") {
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/trantrongdai/shorted-link-pro.git']])
            }
        }
        stage('Build docker image') {
            steps {
                script{
                    dir('limits-service') {
                        sh 'pwd'
                        docker.build("trantrongdai/shorted-be")
                    }
                    dir('shorted-fe') {
                       sh 'pwd'
                       docker.build("trantrongdai/shorted-fe")
                   }
                }
            }
        }
        stage('Login to Docker Hub') {         
            steps{
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
            	echo 'Login Completed'                
             }           
        } 
        stage('Push image to hub'){
            steps {
                script{
                    sh 'docker push trantrongdai/shorted-be'
                    sh 'docker push trantrongdai/shorted-fe'
                }
            }
        }
        stage ('Deploy') {
            steps{
                sshagent(credentials : ['app-ssh']) {
                    sh 'ssh -o StrictHostKeyChecking=no tony@34.87.97.87 uptime \
                    " docker stop trantrongdai.shorted-be || true \
                    && docker rm --force trantrongdai.shorted-be || true \
                    && docker pull trantrongdai/shorted-be \
                    && docker run -it -d -p 8080:8080 --name=trantrongdai.shorted-be trantrongdai/shorted-be"'
                }
                sshagent(credentials : ['app-ssh']) {
                    sh 'ssh -o StrictHostKeyChecking=no tony@34.87.97.87 uptime \
                    " docker stop trantrongdai.shorted-fe || true \
                    && docker rm --force trantrongdai.shorted-fe || true \
                    && docker pull trantrongdai/shorted-fe \
                    && docker run -it -d -p 3000:3000 --name=trantrongdai.shorted-fe trantrongdai/shorted-fe"'
                }
            }
        }
    }
    post {
        always {
          sh 'docker logout'
        }
  }
}
