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
        DOCKER_REGISTRY = "trantrongdai"
        BE_DOCKER_IMAGE = "${DOCKER_REGISTRY}/shorted-be"
        FE_DOCKER_IMAGE = "${DOCKER_REGISTRY}/shorted-fe"
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

        stage('Retrieve Commit Hash') {
            steps {
                // Retrieve Git commit hash and store it globally
                script {
                    COMMIT_HASH = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    echo "Git Commit Hash: ${COMMIT_HASH}"
                }
            }
        }

        stage('Build docker image') {
            steps {
                script{
                    BE_DOCKER_IMAGE = "${DOCKER_REGISTRY}/shorted-be:${COMMIT_HASH}"
                    dir('limits-service') {
                        sh 'pwd'
                        echo "docker TAG:  ${BE_DOCKER_IMAGE} "
                        docker.build("${BE_DOCKER_IMAGE}")
                    }
                    FE_DOCKER_IMAGE = "${DOCKER_REGISTRY}/shorted-fe:${COMMIT_HASH}"
                    dir('shorted-fe') {
                       sh 'pwd'
                       docker.build("${FE_DOCKER_IMAGE}")
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
                    echo "BE_DOCKER_IMAGE Push image to hub : ${BE_DOCKER_IMAGE}"
                    sh 'docker push trantrongdai/shorted-be'
                    sh 'docker push trantrongdai/shorted-fe'
                }
            }
        }
        stage ('Deploy') {
            steps{
                sh 'pwd'
                withVault([configuration:configuration, vaultSecrets: secrets]) {
                    sh "echo ${env.username}"
                    sh "echo server-domain = ${env.url}"
                    echo "BE_DOCKER_IMAGE Deploy: ${BE_DOCKER_IMAGE}"
                    sshagent(credentials : ['app-ssh']) {
                        sh 'scp -o StrictHostKeyChecking=no docker-compose-sql.yml root@34.87.4.192:/home/tony'
                        sh 'scp -o StrictHostKeyChecking=no db_root_password root@34.87.4.192:/home/tony'
                        sh 'scp -o StrictHostKeyChecking=no db_password root@34.87.4.192:/home/tony'
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
                        && docker pull trantrongdai/shorted-be \
                        && docker run --net=shorted-network -it -d -p 8080:8080 --name=shorted-be trantrongdai/shorted-be"'
                    }
                    sshagent(credentials : ['app-ssh']) {
                        sh 'ssh -o StrictHostKeyChecking=no root@$url uptime \
                        " docker stop shorted-fe || true \
                        && docker rm --force shorted-fe || true \
                        && docker pull trantrongdai/shorted-fe \
                        && docker run --net=shorted-network -it -d -p 3000:3000 --name=shorted-fe trantrongdai/shorted-fe"'
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
