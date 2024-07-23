def configuration = [vaultUrl: params.VAULT_HOST,
						 vaultCredentialId: params.VAULT_CREDENTIAL_ID,
						 engineVersion: 2]

def secrets = [
  [  path: 'jenkin-kv/database', engineVersion: 2,
     secretValues: [[envVar: 'username', vaultKey: 'username'], [envVar: 'password', vaultKey: 'password']]
  ],
  [  path: 'jenkin-kv/server', engineVersion: 2,
     secretValues: [[envVar: 'server-domain', vaultKey: 'server-domain']]
  ]
]
pipeline {
    agent any
    environment {     
        DOCKERHUB_CREDENTIALS = credentials('docker_cred')
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
                sh 'pwd'
                withVault([configuration:configuration, vaultSecrets: secrets]) {
                    environment {
                        DOMAIN = ${env.server-domain}
                    }
                }
                sshagent(credentials : ['app-ssh']) {
                    sh 'scp docker-compose-sql.yml tony@$DOMAIN:/home/tony'
                    sh 'scp db_root_password tony@34.87.97.87:/home/tony'
                    sh 'scp db_password tony@34.87.97.87:/home/tony'
                }
                sshagent(credentials : ['app-ssh']) {
                    sh 'ssh -o StrictHostKeyChecking=no tony@34.87.97.87 uptime \
                    " pwd \
                    && docker compose -f /home/tony/docker-compose-sql.yml up -d || true "'
                }
                sshagent(credentials : ['app-ssh']) {
                    sh 'ssh -o StrictHostKeyChecking=no tony@34.87.97.87 uptime \
                    " docker stop shorted-be || true \
                    && docker rm --force shorted-be || true \
                    && docker pull trantrongdai/shorted-be \
                    && docker run --net=shorted-network -it -d -p 8080:8080 --name=shorted-be trantrongdai/shorted-be"'
                }
                sshagent(credentials : ['app-ssh']) {
                    sh 'ssh -o StrictHostKeyChecking=no tony@34.87.97.87 uptime \
                    " docker stop shorted-fe || true \
                    && docker rm --force shorted-fe || true \
                    && docker pull trantrongdai/shorted-fe \
                    && docker run --net=shorted-network -it -d -p 3000:3000 --name=shorted-fe trantrongdai/shorted-fe"'
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
