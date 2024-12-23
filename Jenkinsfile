def configuration = [vaultUrl: params.VAULT_HOST,
						 vaultCredentialId: params.VAULT_CREDENTIAL_ID,
						 engineVersion: 2]

def secrets = [
  [  path: 'jenkin-kv/database', engineVersion: 2,
     secretValues: [[envVar: 'username', vaultKey: 'username'], [envVar: 'password', vaultKey: 'password']]
  ],
  [  path: 'jenkin-kv/server', engineVersion: 2,
     secretValues: [[envVar: 'url', vaultKey: 'url']]
  ]
]
pipeline {
    agent any
    environment {     
        DOCKERHUB_CREDENTIALS = credentials('docker_cred')
        DOMAIN = "localhost:8080"
    } 

    stages{
        stage('Vault') {
            steps {
              withVault([configuration:configuration, vaultSecrets: secrets]) {
                sh "echo ${env.username}"
              }
            }
        }
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
                        docker.build("trongdai306/shorted-be")
                    }
                    dir('shorted-fe') {
                       sh 'pwd'
                       docker.build("trongdai306/shorted-fe")
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
                    sh 'docker push trongdai306/shorted-be'
                    sh 'docker push trongdai306/shorted-fe'
                }
            }
        }
        stage ('Deploy') {
            steps{
                sh 'pwd'
                withVault([configuration:configuration, vaultSecrets: secrets]) {
                    sh "echo ${env.username}"
                    sh "echo server-domain = ${env.url}"
                    sshagent(credentials : ['app-ssh']) {
                        sh 'scp -o StrictHostKeyChecking=no docker-compose-sql.yml root@$url:/home/tony'
                        sh 'scp -o StrictHostKeyChecking=no db_root_password root@34.124.150.106:/home/tony'
                        sh 'scp -o StrictHostKeyChecking=no db_password root@34.124.150.106:/home/tony'
                    }
                    sshagent(credentials : ['app-ssh']) {
                        sh 'ssh -o StrictHostKeyChecking=no root@$url uptime \
                        " pwd \
                        && docker compose -f /home/tony/docker-compose-sql.yml up -d || true "'
                    }
                    sshagent(credentials : ['app-ssh']) {
                        sh 'ssh -o StrictHostKeyChecking=no root@$url uptime \
                        " docker stop shorted-be || true \
                        && docker rm --force shorted-be || true \
                        && docker pull trongdai306/shorted-be \
                        && docker run --net=shorted-network -it -d -p 8080:8080 --name=shorted-be trongdai306/shorted-be"'
                    }
                    sshagent(credentials : ['app-ssh']) {
                        sh 'ssh -o StrictHostKeyChecking=no root@$url uptime \
                        " docker stop shorted-fe || true \
                        && docker rm --force shorted-fe || true \
                        && docker pull trongdai306/shorted-fe \
                        && docker run --net=shorted-network -it -d -p 3000:3000 --name=shorted-fe trongdai306/shorted-fe"'
                    }
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
