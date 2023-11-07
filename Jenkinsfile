#!groovy
pipeline {
    agent none
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
            agent {
                docker {
                    args '-v /home/jenkins-slave/.m2:/home/jenkins-slave/.m2:z --cpus=4 -u root'
                    image 'maven:3.8.4-openjdk-17'
                }
            }
            steps {
               
                    // sh "mvn clean install -DskipTests -T1C --batch-mode --errors -Pbuild-documentation,ditto -Drevision=${theVersion}"
                    sh "sudo build-images.sh"
                    
                }
            
        }
        stage('Deploy') {
            steps {
                // Start Docker Compose in /deployment/docker directory
                dir('../deployment/docker/') {
                    sh 'docker-compose up -d'
                }
            }
        }
    
        stage('SonarQube Scan') {
            agent {
                docker {
                    args '-v /home/jenkins-slave/.m2:/home/jenkins-slave/.m2:z -u root'
                    image 'maven:3.8.4-openjdk-17'
                }
            }
            steps {
                configFileProvider([configFile(fileId: 'mvn-bdc-settings', variable: 'MVN_SETTINGS')]) {
                    withSonarQubeEnv("${env.SONAR_QUBE_ENV}") {
                        sh "mvn -s $MVN_SETTINGS --batch-mode --errors sonar:sonar -Dsonar.branch.name=${theBranch} -Drevision=${theVersion}"
                    }
                }
            }
        }
    }
}
