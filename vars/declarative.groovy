def call() {
pipeline {
  agent any
    stages {
        stage('Buld') { 
            steps {
                script{
                    def p = pipelineConfig()
                    dir("${p.build.projectFolder}") {
                        sh "${p.build.buildCommand}"
                    }
                }
            }
        }
        stage('Database') { 
            steps {
                script{
                   def p = pipelineConfig()
                    dir("${p.database.databaseFolder}") {
                        sh "${p.database.databaseCommand}"
                    }
                }      
            }
        }
        stage('Deploy') { 
            steps {
                script{
                    def p = pipelineConfig()
                    dir("${p.build.projectFolder}") {
                        sh "${p.deploy.deployCommand}"
                    }
                }
            }
        }
        
        stage('test') { 
            steps {
                script{
                    def p = pipelineConfig()                    
                    parallel( "${p.test.name[0]}": {
                                           timestamps {
                                                dir("${p.test.testFolder[0]}") {
                                                sh "${p.test.testCommand[0]}"
                                               }
                                           } 
                                        },
                            "${p.test.name[1]}": {
                                    timestamps {
                                        dir("${p.test.testFolder[1]}") {
                                        sh "${p.test.testCommand[1]}"
                                        }      
                                    }
                                }, 
                                "${p.test.name[2]}": { 
                                    timestamps {
                                        dir("${p.test.testFolder[2]}") {
                                        sh "${p.test.testCommand[2]}"
                                        }
                                    }
                                }
                             )
                        }
                    }
                }
            }
        post {
            always {
                deleteDir()
            }
            success {
                echo 'JOB SUCCESS'
                mail bcc: '', 
                body: """Please go to ${env.BUILD_URL}/consoleText for more details.,
                additional info: 
                NODE_NAME - ${env.NODE_NAME},
                GIT_COMMITTER_NAME ${env.GIT_COMMITTER_NAME}, 
                GIT_COMMITTER_EMAIL = ${env.GIT_COMMITTER_EMAIL} """, 
                cc: '', from: '', replyTo: '', subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'", 
                to: "my@box.com"
            }
            failure {
                echo 'Stage FAIL'
                mail bcc: '', 
                body: """Please go to ${env.BUILD_URL}/consoleText for more details.,
                additional info: 
                NODE_NAME - ${env.NODE_NAME},
                GIT_COMMITTER_NAME ${env.GIT_COMMITTER_NAME}, 
                GIT_COMMITTER_EMAIL = ${env.GIT_COMMITTER_EMAIL} """, 
                cc: '', from: '', replyTo: '', subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'", 
                to: "my@box.com"
            }
        }
    }
}