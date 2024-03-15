pipeline {
    agent any
    stages{
        stage('Build') {
            steps {
                sh 'mvn -B -DskipTests clean package'
            }
        }
        stage('Run'){
            steps {
                sh 'ps | grep KuronekoBotto | awk '{print $1}' | xargs kill -9 || true'
                sh 'java -jar ./target/KuronekoBotto-1.0-SNAPSHOT.jar '
            }
        }
    }
}