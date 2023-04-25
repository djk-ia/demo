def project_token = '....'
def webhooks_url = "..."

pipeline{
    agent any
    options {
		
	}
    environment {
        USER_NAME = '...'
         imageName = "demo-dit"
         dockerImageVersion = 'SNAPSHOT-1.0.0'
         repo = "${JOB_NAME}"
         DOCKER_REGISTRY_USER= '...'
         DOCKER_REGISTRY_USER_PASSWORD= '...'
         TOKEN_SONAR= '...'
        }
    tools {
        maven 'maven'
        dockerTool 'DOCKER'
    }
    stages{

            stage('SCM Checkout') {
              steps {
                 git(branch: 'main', credentialsId: 'gitlab',  url:'localhost')                   
              }
         }
           stage('Check Packages'){
                steps{
                    sh script: 'mvn clean install -Dmaven.test.skip=true'
                }
            }

              stage('SonarQube analysis') {
                  steps{
                  withSonarQubeEnv(credentialsId: 'sonar', installationName:"Sonar") {
                      sh 'mvn sonar:sonar -Dsonar.host.url=${SONARQUBE_URL} -Dsonar.login=${TOKEN_SONAR}'
                    }
                }
              }
        stage('Upload Jar to nexus'){
            steps{
                
                nexusArtifactUploader artifacts: [
                    [
                        artifactId: 'demo',
                        classifier: '',
                        file: 'target/demo-0.0.1-SNAPSHOT.jar',
                        type: 'jar'
                    ]
                ],
                credentialsId: 'nexus',
                groupId: 'demo-dit',
                nexusUrl: '${NEXUS_URL}',
                nexusVersion: 'nexus3',
                protocol: 'http',
                repository: 'demo',
                version: '0.0.1-SNAPSHOT'
            }
        }
                    stage('Build Image'){
                          steps{
                            script {
                             sh "docker build -t ${DOCKER_REGISTRY_URL}/${env.imageName} ."
                            }
                          }
                        }
                    stage('Connect To Registry'){
                        steps{
                            sh "docker logout"
                            sh "docker login ${DOCKER_REGISTRY_URL} --username ${env.DOCKER_REGISTRY_USER} --password ${env.DOCKER_REGISTRY_USER_PASSWORD}"
                        }
                    }
                    stage ('Push Docker Image'){
                        steps{
                            script{
                          sh "docker push ${DOCKER_REGISTRY_URL}/${env.imageName}:latest" 
                         
                        }
                        }
                    }
                                        stage ('Delete Tempory Image'){
                        steps{
                        sh "docker rmi ${DOCKER_REGISTRY_URL}/${env.imageName}:latest"
                    }
                    }


             stage('Deploy to cluster') {
                  steps {
                    script {
                       sshagent(['demodeploy']) {
                           sh'ssh -oStrictHostKeyChecking=no root@10.0.0.20 kubectl delete deploy --ignore-not-found=true demo-app'
                                                      sh'ssh -oStrictHostKeyChecking=no root@10.0.0.20 kubectl delete svc --ignore-not-found=true demo-svc'
                                     sh 'ssh -oStrictHostKeyChecking=no root@10.0.0.20 kubectl apply -f /var/lib/jenkins/workspace/demo_main@2/deploymentservice.yml'
                                     sh 'sleep 30'
                                }
                        
                    }
                  }
                }

                        }
                            /*		post {
   			 failure {
       			 mail to: ' edjiky@gmail.com ',
             		subject: "**Failed Pipeline**: ${currentBuild.fullDisplayName}",
             		body: "Something is wrong with ${env.BUILD_URL}"
    }
             success{
                mail to: ' edjiky@gmail.com ',
                 subject: "**Success Pipeline**:${currentBuild.fullDisplayName}",
           		    body: "Success of your build, here is the link of the build ${env.BUILD_URL}"
                        }
}*/
              }
