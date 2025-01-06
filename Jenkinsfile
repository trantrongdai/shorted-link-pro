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
        DOCKER_IMAGE_BE = "${DOCKER_REGISTRY}/shorted-be"
        DOCKER_IMAGE_FE = "${DOCKER_REGISTRY}/shorted-fe"
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
                    DOCKER_IMAGE_BE = "${DOCKER_REGISTRY}/shorted-be:${COMMIT_HASH}"
                    dir('limits-service') {
                        sh 'pwd'
                        echo "docker TAG:  ${DOCKER_IMAGE_BE} "
                        docker.build("${DOCKER_IMAGE_BE}")
                    }
                    DOCKER_IMAGE_FE = "${DOCKER_REGISTRY}/shorted-fe:${COMMIT_HASH}"
                    dir('shorted-fe') {
                       sh 'pwd'
                       docker.build("${DOCKER_IMAGE_FE}")
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
                    echo "DOCKER_IMAGE_BE Push image to hub : ${DOCKER_IMAGE_BE}"
                    sh "docker push ${DOCKER_IMAGE_BE}"

                    echo "DOCKER_IMAGE_FE Push image to hub : ${DOCKER_IMAGE_FE}"
                    sh "docker push ${DOCKER_IMAGE_FE}"
                }
            }
        }
        stage ('Deploy') {
            steps{
                script {
                    sh 'pwd'
                    withVault([configuration:configuration, vaultSecrets: secrets]) {
                        sh "echo ${env.username}"
                        sh "echo server-domain = ${env.url}"
                        echo "DOCKER_IMAGE_BE Deploy 1: ${DOCKER_IMAGE_BE}"
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

                        echo "DOCKER_IMAGE_BE Deploy: ${DOCKER_IMAGE_BE}"
                        sshagent(credentials : ['app-ssh']) {
                            sh """
                            echo "Deploying BE with commit hash: ${COMMIT_HASH} via SSH"
                            ssh -o StrictHostKeyChecking=no root@$url uptime \
                                " docker stop shorted-be || true \
                                && docker rm --force shorted-be || true \
                                && docker pull ${DOCKER_IMAGE_BE} \
                                && docker run --net=shorted-network -it -d -p 8080:8080 --name=shorted-be ${DOCKER_IMAGE_BE}"
                            """
                        }

                        echo "DOCKER_IMAGE_FE Deploy: ${DOCKER_IMAGE_FE}"
                        sshagent(credentials : ['app-ssh']) {
                            sh """
                            echo "Deploying FE with commit hash: ${COMMIT_HASH} via SSH"
                            ssh -o StrictHostKeyChecking=no root@$url uptime \
                                " docker stop shorted-fe || true \
                                && docker rm --force shorted-fe || true \
                                && docker pull ${DOCKER_IMAGE_FE} \
                                && docker run --net=shorted-network -it -d -p 3000:3000 --name=shorted-fe ${DOCKER_IMAGE_FE}"
                            """
                        }
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
