def project_token = 'abcdefghijklmnopqrstuvwxyz0123456789ABCDEF'
def teams_webhooks_url = "https://atos365.webhook.office.com/webhookb2/8e77e7f9-7832-4097-be1b-6bd9c184b984@33440fc6-b7c7-412c-bb73-0e70b0198d5a/IncomingWebhook/e357e52acfbc43b38118c84d79e18929/0167722f-7496-46d8-b167-5784eb3afd18"
properties([
    gitLabConnection('si_connection'),
    pipelineTriggers([
        [
            $class: 'GitLabPushTrigger',
            branchFilterType: 'All',
            triggerOnPush: true,
            triggerOnMergeRequest: true,
            triggerOpenMergeRequestOnPush: "never",
            triggerOnNoteRequest: true,
            noteRegex: "Jenkins please retry a build",
            skipWorkInProgressMergeRequest: true,
            secretToken: project_token,
            ciSkip: false,
            setBuildDescription: true,
            addNoteOnMergeRequest: true,
            addCiMessage: true,
            addVoteOnMergeRequest: true,
            acceptMergeRequestOnSuccess: true,
            branchFilterType: "NameBasedFilter",
            includeBranchesSpec: "",
            excludeBranchesSpec: "",
        ]
    ])
])

pipeline{
    agent any
    options {
		office365ConnectorWebhooks([[
			name: "JenkinsTeamsConnector", 
            url: "${teams_webhooks_url}",
            startNotification: true, 
            notifyBackToNormal: true, 
            notifyFailure: true, 
            notifyRepeatedFailure: true, 
            notifySuccess: true, 
            notifyAborted: true
		]])
	}
    environment {
        USER_NAME = 'ibrahima.diop'
         imageName = "demo-demo"
         dockerImageVersion = 'SNAPSHOT-1.0.0'
         repo = "${JOB_NAME}"
         DOCKER_REGISTRY_USER= 'karimux'
         DOCKER_REGISTRY_USER_PASSWORD= 'Adama7812@%!'
         TOKEN_SONAR= '49b46dcd7429cb158d6c9ac968365ace9ab62ef9'
        }
    tools {
        maven 'maven'
        dockerTool 'DOCKER'
    }
    stages{

            stage('SCM Checkout') {
              steps {
                 git(branch: 'main', credentialsId: 'gitlab',  url:'${GITLAB_URL}/demo/demo')                   
              }
         }
           stage('Check Packages'){
                steps{
                    sh script: 'mvn clean install -Dmaven.test.skip=true'
                }
            }

              stage('SonarQube analysis') {
                  steps{
                  withSonarQubeEnv(credentialsId: 'sonar', installationName:"SonarForgeAtos") {
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
                groupId: 'net-atos',
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
       			 mail to: ''+"${env.USER_NAME}"+'@atos.net,ibrahima.diop@atos.net,abdou.mbengue@atos.net',
             		subject: "**Failed Pipeline**: ${currentBuild.fullDisplayName}",
             		body: "Something is wrong with ${env.BUILD_URL}"
    }
             success{
                mail to: ''+"${env.USER_NAME}"+'@atos.net,ibrahima.diop@atos.net,abdou.mbengue@atos.net',
                 subject: "**Success Pipeline**:${currentBuild.fullDisplayName}",
           		    body: "Success of your build, here is the link of the build ${env.BUILD_URL}"
                        }
}*/
              }
