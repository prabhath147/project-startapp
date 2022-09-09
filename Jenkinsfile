pipeline {
	agent any
	environment {
		LINK="https://tools.publicis.sapient.com/bitbucket/scm/psijun/order-service.git"
		SONAR_LINK="PSIJUN_order-service_AYLBKEfv1h2U62IkEzoF"
	}
	tools{
		//in global tool configuration name - Maven
		maven 'Maven'
	}

	stages {

		stage("Clone Code from Repository"){
			// need to be genarated from secrets, here git_token is the id, put the password in secret
			steps{
				bbs_checkout branches: [[name: '*/development']], credentialsId: 'bitbucket_admin', id: 'd42b7d93-dbb1-4228-976e-3a86cfa0da33', projectName: 'PSI-2022 June ASDE Batch 3', repositoryName: 'order-service', serverId: '9d049c85-48b6-452e-8478-7519760f9383'
			}
		}

		stage("Code coverage") {
			steps {
				sh 'mvn clean verify'
				jacoco buildOverBuild: true, runAlways: true, exclusionPattern: '*/OrderServiceApplication.class, */dto/*, */configuration/*, */model/*, */utils/*, */repository/*, */service/*'
			}
		}

		stage("Create JAR File"){
			steps{
				sh "mvn clean package"
			}
		}

		stage ("archive") {
			steps{
				archiveArtifacts "**/*.jar"
			}
		}

		stage("SonarQube Analysis"){
			steps{
				withSonarQubeEnv('sonarqube-server'){
					sh "mvn sonar:sonar -Dsonar.projectKey=${SONAR_LINK}"
				}
			}
		}

		stage("SonarQube Quality Check"){
			steps{
				timeout(time:3, unit:'MINUTES'){
					waitForQualityGate abortPipeline: true, credentialsId: 'sonarqube_token'
				}
			}
		}

	}
	post {
		always {
			echo 'One way or another, I have finished'
			deleteDir() /* clean up our workspace */
			sh "docker stop \$(docker ps -a -q) || true"
			sh "docker rm \$(docker ps -a -q) || true"
			sh "docker rmi \$(docker images -a -q) || true"
		}
		success {
			echo 'I succeeded!'
		}
		unstable {
			echo 'I am unstable :/'
		}
		failure {
			echo 'I failed :('
		}
		changed {
			echo 'Things were different before...'
		}
	}
}
