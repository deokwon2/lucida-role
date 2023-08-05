/* pipeline 변수 설정 */
def DOCKER_IMAGE_NAME = "role"           // 생성하는 Docker image 이름
def DOCKER_IMAGE_TAGS = "0.1.${env.BUILD_NUMBER}"  // 생성하는 Docker image 태그
// def DOCKER_REPOSITORY = "192.168.200.79:32000"
// def SERVICE_NAME = "role"
// def SLACK_CHANNEL = "jenkins"
// def SLACK_URL = "https://nkia-hq.slack.com/services/hooks/jenkins-ci/"
// def SLACK_CREDENTIAL_ID = "nkia-hq-slack-token"
def SONAR_PROJECT = env.JOB_NAME
def sonarAnalysis

// /* Slack 시작 알람 함수 */
//
// def notifyStarted(credential, slack_url, slack_channel) {
//     slackSend(teamDomain: "nkia-hq", tokenCredentialId: "${credential}", baseUrl: "${slack_url}", channel: "${slack_channel}", color: '#FFFF00', message: "STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
// }
// /* Slack 성공 알람 함수 */
//
// def notifySuccessful(credential, slack_url, slack_channel) {
//     slackSend(teamDomain: "nkia-hq", tokenCredentialId: "${credential}", baseUrl: "${slack_url}", channel: "${slack_channel}", color: '#00FF00', message: "SUCCESSFUL: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
// }
// /* Slack 실패 알람 함수 */
//
// def notifyFailed(credential, slack_url, slack_channel) {
//     slackSend(teamDomain: "nkia-hq", tokenCredentialId: "${credential}", baseUrl: "${slack_url}", channel: "${slack_channel}", color: '#FF0000', message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL})")
// }

node { 
    //jdk = tool name: 'openJDK 17'
    //env.JAVA_HOME = "${jdk}"
    try {
        stage('Checkout') {
            checkout scm
        }
        stage('Build') {
            // gradle wrapper를 프로젝트에 포함하여 repository에 등록했을 시 gradlew 사용이 가능합니다.
            // gradlew : 빌드 서버에 gradle을 설치하지 않아도 개발 환경과 같은 버전으로 빌드가 가능하게 해 줍니다. (Gradle에서 권장하는 방법)
            sh '''
                chmod +x ./gradlew
                ./gradlew clean bootJar
            '''
        }
        try {
            stage('Test') {
                // gradlew check를 하면 test 코드가 실행되고 결과 파일이 생성됩니다.
                sh '''
                    chmod +x ./gradlew
                    ./gradlew check
                '''
            }
        } finally {
            // test reports가 있을 경우 junit 실행
            junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
        }

        stage('test(SonarQube Analysis)') { //SonarQube 분석

            def scannerHome = tool 'SonarQube Scanner' // Jenkins에서 SonarQubeScanner를 설치하고 등록한 도구 이름
            def PROJECTDIR = pwd() //dir 확인

            withSonarQubeEnv() { // 분석 시작
                sonarAnalysis = sh(returnStatus: true, script:
                    """
                    ${scannerHome}/bin/sonar-scanner \
                    -Dsonar.host.url=http://localhost:9000 \
                    -Dsonar.projectKey=${SONAR_PROJECT} \
                    -Dsonar.projectName=${SONAR_PROJECT} \
                    -Dsonar.report.export.path=${scannerHome}/.scannerwork/sonar-report.json \
                    -Ddetekt.sonar.kotlin.config.path=default-detekt-config.yml \
                    -Dsonar.sources=src/main/java,src/main/resources \
                    -Dsonar.exclusions='**/util/**,**/support/**,**/dto/**,**/entity/**' \
                    -Dsonar.java.sourcesion=1.8 \
                    -Dsonar.sourceEncoding=UTF-8 \
                    -Dsonar.java.binaries=build/classes \
                    -Dsonar.coverage.jacoco.xmlReportPaths=${PROJECTDIR}/build/reports/jacoco/test/jacocoTestReport.xml \
                    -Dsonar.projectBaseDir=${PROJECTDIR}
                    """)
            }

            //분석 후 결과 확인
            while (true){
                def sonarScannStatus = sh(returnStdout: true, script: // 소나큐브 분석 상태 API 확인
                    """
                    curl -u admin:'admin1234' -X GET http://localhost:9000/api/ce/component?component=${SONAR_PROJECT}
                    """)
                //queue에 아무것도 없으면 수행
                if (sonarScannStatus.contains('"queue":[]')){

                    if (sonarAnalysis == 0) {
                        def qualityGateStatus = sh(returnStdout: true, script: // 소나큐브 결과 API 확인
                            """
                            curl -u admin:'admin1234' -X GET http://localhost:9000/api/qualitygates/project_status?projectKey=${SONAR_PROJECT}
                            """)

                            if (qualityGateStatus.contains('"projectStatus":{"status":"ERROR"')) {
                                // 소나큐브 품질 게이트 실패 시 빌드 종료
                                echo 'SonarQube Quality Gate failed'
                                error('SonarQube Quality Gate Failed')
                            } else if (qualityGateStatus.contains('"projectStatus":{"status":"OK"')){

                                // 소나큐브 품질 게이트 성공
                                echo 'SonarQube Quality Gate Passed'
                                }
                        } else {
                                // SonarQube 분석 단계 자체가 실패한 경우 빌드 종료
                                echo 'SonarQube Analysis Failed'
                                error('SonarQube Analysis Failed')
                            }
                    break
                }else {
                    //queue에 내용이 있을 경우 1초 대기
                    sleep time: 1
                }
            }
         }

    } catch (e) {
        echo 'Jenkins Failed'
    }
}

//         stage('Image Build') {
//             // jenkins가 설치된 서버의 docker와 같은 docker를 사용하여 빌드 합니다.
//             sh "docker build -t ${DOCKER_REPOSITORY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS} ."
//         }
//         stage('Image Push') {
//             sh "docker push ${DOCKER_REPOSITORY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS}"
//         }

//         stage('Deploy for development') {
//             sh 'rm subdir -rf; mkdir subdir'
//             sh "mkdir -p /home/on-premise/${SERVICE_NAME}"
//             dir('subdir') {
//                 git branch: 'master',
//                         credentialsId: 'lucida_pm_account',
//                         url: 'https://cims2.nkia.net:8443/gitlab/lucida-deployment.git'
//                 if (gitlabSourceBranch.equals("develop") || gitlabSourceBranch.equals("feature/migration")) {
//                     sh "git checkout dev --"
//                     sh "cd ${SERVICE_NAME}/env/dev && pwd && kustomize edit set image localhost:32000/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS}"
//                     GIT_BRANCH = "dev"
//                 } else if (gitlabSourceBranch.equals("master")) {
//                     sh "git checkout prod --"
//                     sh "cd ${SERVICE_NAME}/env/prod && pwd && kustomize edit set image ${DOCKER_REPOSITORY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS} &&" +
//                             "kustomize build . > /home/on-premise/${SERVICE_NAME}/${SERVICE_NAME}.yml"
//                     sh "docker save -o /home/on-premise/${SERVICE_NAME}/${SERVICE_NAME}.tar ${DOCKER_REPOSITORY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS}"
//                     GIT_BRANCH = "prod"
//                 } else {
//                     echo 'Unsupported branch name'
//                     echo gitlabSourceBranch
//                     throw new Exception("unsupported branch")
//                 }
//                 withCredentials([usernamePassword(
//                         credentialsId: 'lucida_pm_account',
//                         usernameVariable: 'GIT_USERNAME',
//                         passwordVariable: 'GIT_PASSWORD')]) {
//                     echo GIT_PASSWORD
//                     sh """
//                     git config --global user.email "lucida_pm@nkia.co.kr"
//                     git config --global user.name "lucida_pm"
//
//                     git commit -a -m "updated the image tag ${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS}"
//                     git remote set-url origin "https://${GIT_USERNAME}:${GIT_PASSWORD}@cims2.nkia.net:8443/gitlab/lucida-deployment.git"
//                     git push origin ${GIT_BRANCH}
//                 """
//                 }
//             }
//         }
//         stage('Cleaning up') {
//             sh "docker rmi ${DOCKER_REPOSITORY}/${DOCKER_IMAGE_NAME}:${DOCKER_IMAGE_TAGS}"
//         }
//         notifySuccessful(SLACK_CREDENTIAL_ID, SLACK_URL, SLACK_CHANNEL)
//     }
//     catch (e) {
//         /* 배포 실패 */
//         currentBuild.result = "FAILED"
//         notifyFailed(SLACK_CREDENTIAL_ID, SLACK_URL, SLACK_CHANNEL)
//     }
// }
