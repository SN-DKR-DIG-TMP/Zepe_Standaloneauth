pipeline{
    agent any
    environment {
        USER_NAME = 'ibrahima.diop'
         imageName = "zepe-microauth"
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
                git(branch: 'develop-v2', credentialsId: 'gitlab',  url:'${GITLAB_URL}/zepe/standaloneauth')
              }
         }
           stage('Check Packages'){
                steps{
                    sh script: 'mvn clean install -DskipTests'
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
                
                //nexusArtifactUploader artifacts: [
                //    [
                //        artifactId: 'cabd-springboot-jwt',
                //        classifier: '',
                //        file: 'target/cabd-springboot-jwt-0.0.1-SNAPSHOT.jar',
                //        type: 'jar'
                //    ]
                //],
                //credentialsId: 'nexus',
                //groupId: 'com.zepe-standaloneauth',
                //nexusUrl: '${NEXUS_URL}',
                //nexusVersion: 'nexus3',
                //protocol: 'http',
                //repository: 'zepe-standaloneauth',
                //version: '0.0.1-SNAPSHOT'
                sh "echo 'Skip Nexus uploading beacause credentialsId not good.'"
           }
        }
           
                    stage('Build Image'){
                          steps{
                            script {
                             sh "docker build -t ${DOCKER_REGISTRY_URL}/${env.imageName}:${env.dockerImageVersion} ."
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
                      
                          sh "docker push ${DOCKER_REGISTRY_URL}/${env.imageName}:${env.dockerImageVersion}"
                        }
                        }
                    }


                    stage ('Delete Tempory Image'){
                        steps{
                        sh "docker rmi ${DOCKER_REGISTRY_URL}/${env.imageName}:${env.dockerImageVersion}"
                    }
                    }

                        }
                            		post {
   			 failure {
                mail to: ''+"${env.USER_NAME}"+'@atos.net,eric.djikydjazik@atos.net,jospin.diffouofoufouo@atos.net,mohammad-abdoul-aziz.ciss@atos.net,abdou-karim.diop@atos.net',
             		subject: "**Failed Pipeline**: ${currentBuild.fullDisplayName}",
             		body: "Something is wrong with ${env.BUILD_URL}"
    }
             success{
                mail to: ''+"${env.USER_NAME}"+'@atos.net,eric.djikydjazik@atos.net,jospin.diffouofoufouo@atos.net,mohammad-abdoul-aziz.ciss@atos.net,abdou-karim.diop@atos.net',
                 subject: "**Success Pipeline**:${currentBuild.fullDisplayName}",
           		    body: "Success of your build, here is the link of the build ${env.BUILD_URL}"
                        }
}
              }
