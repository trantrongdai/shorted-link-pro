def configuration = [vaultUrl: params.VAULT_HOST,
						 vaultCredentialId: params.VAULT_CREDENTIAL_ID,
						 engineVersion: 2]

def secrets = [
  [  path: 'jenkin-kv/database', engineVersion: 2,
       secretValues: [[envVar: 'MYSQL_DATABASE', vaultKey: 'MYSQL_DATABASE'], [envVar: 'MYSQL_USER', vaultKey: 'MYSQL_USER'],[envVar: 'MYSQL_PASSWORD', vaultKey: 'MYSQL_PASSWORD'], [envVar: 'MYSQL_ROOT_PASSWORD', vaultKey: 'MYSQL_ROOT_PASSWORD']]
  ],
  [  path: 'jenkin-kv/server', engineVersion: 2,
     secretValues: [[envVar: 'SERVER_URL', vaultKey: 'url']]
  ]
]
pipeline {
    agent any
    environment {
        GIT_REPO_URL = 'https://github.com/trantrongdai/shorted-link-pro.git'  // Replace with your repo
        DEPLOY_SERVICES = ''  // List of services to deploy
        DOCKERHUB_CREDENTIALS = credentials('docker_cred')
        DOMAIN = "localhost:8080"
        DOCKER_REGISTRY = "trantrongdai"
        IMAGE_NAME_BE = "shorted-be"
        IMAGE_NAME_FE = "shorted-fe"
        CONTAINER_NAME_BE = "shorted-be"
        CONTAINER_NAME_FE = "shorted-fe"
        SERVER_USER = "app"
    }

    stages{
        stage('Vault') {
            steps {
              withVault([configuration:configuration, vaultSecrets: secrets]) {
                sh "echo ${env.SERVER_URL}"
              }
            }
        }
        stage("Get source") {
            steps{
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[url: env.GIT_REPO_URL]])
            }
        }
        stage('Check Branch') {
            steps {
                script {
                    // Check if the current branch is not 'main'
                    echo "env.BRANCH_NAME : ${env.BRANCH_NAME}"
                    if (env.BRANCH_NAME != 'main') {
                        error('This pipeline can only run on the main branch')
                    }
                }
            }
        }
        stage('Detect Changed Services') {
            steps {
                script {
                    // Compare current branch/commit with main branch to detect changes
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD origin/main", returnStdout: true).trim()

                    // Log the changed files
                    echo "Changed Files: ${changedFiles}"

                    // Detect which services are impacted based on folder changes
                    if (changedFiles.contains('limits-service/')) {
                        env.DEPLOY_SERVICES += 'BE'
                        echo "vao day roi"
                        echo "Services to Deploy: ${env.DEPLOY_SERVICES}"
                    }
                    if (changedFiles.contains('shorted-fe/')) {
                        env.DEPLOY_SERVICES += 'FE'
                        echo "Services to Deploy: ${env.DEPLOY_SERVICES}"
                    }
                    echo "Services to Deploy: ${DEPLOY_SERVICES}"
                }
            }
        }

        stage('Test') {
            steps {
                dir('limits-service') {
                    sh 'mvn test'
                }
            }
        }
        /* stage('Code Quality') {
            steps {
                dir('limits-service') {
                   sh 'mvn sonar:sonar'
                }
            }
        } */

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
                    DOCKER_IMAGE_BE = "${DOCKER_REGISTRY}/${IMAGE_NAME_BE}:${BUILD_ID}-${env.BRANCH_NAME}-${COMMIT_HASH}"
                    dir('limits-service') {
                        sh 'pwd'
                        echo "docker TAG:  ${DOCKER_IMAGE_BE} "
                        docker.build("${DOCKER_IMAGE_BE}")
                    }
                    DOCKER_IMAGE_FE = "${DOCKER_REGISTRY}/${IMAGE_NAME_FE}:${BUILD_ID}-${env.BRANCH_NAME}-${COMMIT_HASH}"
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
                        sh "echo ${env.MYSQL_DATABASE}"
                        sh "echo server-domain = ${env.SERVER_URL}"
                        // Remove existed container
                        sshagent(credentials : ['app-ssh']) {
                            sh """
                                ssh -o StrictHostKeyChecking=no ${SERVER_USER}@$SERVER_URL uptime \
                                " docker rm \$(docker ps -a -f status=exited -q) "
                            """
                        }

                        echo "DOCKER_IMAGE_BE Deploy: ${DOCKER_IMAGE_BE}"
                        sshagent(credentials : ['app-ssh']) {
                            sh """
                            echo "Deploying BE with commit hash: ${COMMIT_HASH} via SSH"
                            ssh -o StrictHostKeyChecking=no ${SERVER_USER}@$SERVER_URL uptime \
                                " docker stop ${CONTAINER_NAME_BE} || true \
                                && docker rm --force ${CONTAINER_NAME_BE} || true \
                                && docker pull ${DOCKER_IMAGE_BE} \
                                && docker run --net=shorted-network -it -d -p 8080:8080 --name=${CONTAINER_NAME_BE} ${DOCKER_IMAGE_BE}"
                            """
                        }

                        echo "DOCKER_IMAGE_FE Deploy: ${DOCKER_IMAGE_FE}"
                        sshagent(credentials : ['app-ssh']) {
                            sh """
                            echo "Deploying FE with commit hash: ${COMMIT_HASH} via SSH"
                            ssh -o StrictHostKeyChecking=no ${SERVER_USER}@$SERVER_URL uptime \
                                " docker stop ${CONTAINER_NAME_FE} || true \
                                && docker rm --force ${CONTAINER_NAME_FE} || true \
                                && docker pull ${DOCKER_IMAGE_FE} \
                                && docker run --net=shorted-network -it -d -p 3000:3000 --name=${CONTAINER_NAME_FE} ${DOCKER_IMAGE_FE}"
                            """
                        }
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed!'
        }
        always {
            echo 'Cleaning up resources...'
            sh 'docker logout'
        }
  }
}
