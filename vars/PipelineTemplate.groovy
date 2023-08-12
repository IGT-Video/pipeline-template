def testext(){
    sh 'pwd && ls -al'
    sh 'echo "external step"'
}

def call() {
    pipeline {
        options {
          disableConcurrentBuilds()
        }
        agent {
          label "jenkins-maven"
        }
        environment {
          JIRA_CREDS = credentials('jx-pipeline-issue-jira-jira')
          JX_HELM3 = "true"
        }

        stages {
          stage('set repo name') {
              steps {
                  script {
                    def gitUrl = env.GIT_URL
                    def repoName = gitUrl.substring(gitUrl.lastIndexOf('/') + 1, gitUrl.lastIndexOf('.git'))
                    echo "Repository Name: ${repoName}"
                    env.APP_NAME = "${repoName}"
                }
              }
          }
          stage('Update Environment') {
            when {
              branch 'master'
            }
            steps {
              container('igt-tools') {
                sh 'echo "Repository Name Again: $APP_NAME"'
              }
            }
          }

          stage('test ext') {
              steps{
                  script{
                      testext()
                  }
              }
          }
        }
    }
}
