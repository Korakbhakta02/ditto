#!groovy
pipeline {
    agent any
    environment {
        // Need to replace the '%2F' used by Jenkins to deal with / in the path (e.g. story/...)
        theBranch = "${env.BRANCH_NAME}".replace("%2F", "-").replace("/", "-")
        theVersion = "0-${theBranch}-SNAPSHOT"
        theMvnRepo = "$WORKSPACE/../ditto-repo-${theBranch}"
        JAVA_TOOL_OPTIONS = '-Duser.home=/home/jenkins-slave'
    }
    stages {
        stage('Checkout scm') {
            agent any
            steps {
                echo 'Checkout scm'
                checkout scm
            }
        }
        stage('Build') {
            // agent any
            agent {
                docker {
                    args '-v /home/jenkins-slave/.m2:/home/jenkins-slave/.m2:z --cpus=4 -u root'
                    image 'maven:3.8.4-openjdk-17'
                }
            }
            steps {
               
                    sh "mvn clean install -DskipTests"
                   
                }
            
        }
        stage('clean build') {
            steps {
                sh "chmod u+x build-images.sh"
                sh "./build-images.sh"
                }
            }
        
        stage('Deploy') {
            steps {
                // Start Docker Compose in /deployment/docker directory
                dir('deployment/docker/') {
                    sh "cp dev.env .env"
                    sh 'docker-compose up -d'
                }
            }
        }
    
        
    }
}
