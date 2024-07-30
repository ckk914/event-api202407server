도커(Docker)
- 컨테이너(Container) 기술을 사용하여 애플리케이션을 신속하게 구축, 테스트 및 배포할 수 있게 하는 플랫폼입니다. 
- 도커를 사용하면 애플리케이션과 그 종속성들을 컨테이너라는 단위로 패키징할 수 있으며, 이를 통해 일관된 실행 환경을 제공합니다.
- 도커는 리눅스 컨테이너(Linux Container, LXC) 기술을 기반으로 개발



- 도커 이미지(Docker Image): 애플리케이션과 그 종속성을 포함하는 읽기 전용 템플릿입니다.
- 도커 컨테이너(Docker Container): 도커 이미지를 실행한 것으로, 애플리케이션이 실행되는 독립적인 환경입니다.
- 도커허브(Docker Hub): 도커 이미지의 저장소로, 이미지를 공유하고 배포할 수 있는 서비스입니다.


환경 일관성: 도커를 사용하면 개발, 테스트, 프로덕션 환경에서 동일한 환경을 제공
효율성: 컨테이너는 VM(가상 머신)에 비해 가볍고 빠르며, 자원을 효율적으로 사용

도커 이미지는 여러 계층(Layer)으로 구성
- **베이스 이미지(Base Image)**: 모든 도커 이미지는 베이스 이미지에서 시작합니다. 예를 들어, `ubuntu`, `node`, `alpine` 등이 있습니다.
- **명령어(Command)**: Dockerfile에 정의된 명령어들은 각각의 계층을 형성합니다. 예를 들어, `RUN`, `COPY`, `ADD` 등이 있습니다.
- **메타데이터(Metadata)**: 이미지에 대한 정보, 작성자, 버전 등이 포함됩니다.


```dtd
 kon@konui-MacBookAir  ~  cd konFolder/src
 kon@konui-MacBookAir  ~/konFolder/src  mkdir my-node-app
 kon@konui-MacBookAir  ~/konFolder/src  cd my-node-app
 kon@konui-MacBookAir  ~/konFolder/src/my-node-app  code app.js
zsh: command not found: code
 ✘ kon@konui-MacBookAir  ~/konFolder/src/my-node-app  touch app.js
 kon@konui-MacBookAir  ~/konFolder/src/my-node-app  vi app.js
 kon@konui-MacBookAir  ~/konFolder/src/my-node-app  touch package.json
 kon@konui-MacBookAir  ~/konFolder/src/my-node-app  vi package.json
 kon@konui-MacBookAir  ~/konFolder/src/my-node-app  open .
 kon@konui-MacBookAir  ~/konFolder/src/my-node-app  sudo npm install
Password:

added 64 packages, and audited 65 packages in 3s

12 packages are looking for funding
  run `npm fund` for details

found 0 vulnerabilities
 kon@konui-MacBookAir  ~/konFolder/src/my-node-app  node app.js
예시 어플리케이션이 실행되었습니다.
```
app.js
```dtd
const express = require('express');
const app = express();
const port = 8585;

app.get('/', (req, res) => {
  res.send('Hello World from Docker!');
});

app.listen(port, () => {
  console.log(`예시 어플리케이션이 실행되었습니다.`);
});

```

패키지 제이슨 내용
```dtd
{
        "name": "docker-example",
        "version": "1.0.0",
        "description": "A simple Node.js app running in Docker",
        "main": "app.js",
        "scripts": {
        "start": "node app.js"
        },
        "dependencies": {
        "express": "^4.17.1"
        }
        }

```
1. 도커 이미지 빌드를 위한 설정파일인 Dockerfile을 작성합니다.
- 반드시 파일명을 대소문자 정확하게 ‘Dockerfile’로 만드세요! 확장자도 지정하지 마세요!

내용
```dtd
# 베이스 이미지로 Node.js를 사용 (버전 확인 후 입력)
FROM node: 22

# 애플리케이션 디렉토리 생성
WORKDIR /usr/src/app

# 애플리케이션 종속성을 설치
COPY package*.json ./

RUN npm install

# 애플리케이션 소스 복사 (점 두개를 띄어쓰기를 통해 작성해야 합니다 ..이 아니고 . . )
COPY . .

# 애플리케이션이 바인드할 포트 정의
EXPOSE 8585

# 애플리케이션 실행 명령어
CMD ["node", "app.js"]

```
이미지 생성 도커 
✘ kon@konui-MacBookAir  ~/konFolder/src/my-node-app  docker build -t my-node-
app .


docker images
입력해보면 확인할 수 있다

node-container로 이름 지어서 실행   my-node-app(이미지명)
도커 컨테이너 실행
docker run -d -p 8585:8585 --name node-container my-node-app


현재 상태
docker ps  , docker ps -a

도커 중지 
docker stop 컨테이너명

컨테이너 삭제
docker rm 컨테이너명

이미지 삭제
docker rmi my-node-app


Dockerfile은 도커 이미지를 빌드하기 위한 설정 파일로
이미지 생성 과정에서 필요한 명령어를 순차적으로 기술합니다. 

이 파일은 베이스 이미지 선택부터 필요한 소프트웨어 설치, 환경 설정까지 
이미지를 구성하는 모든 단계를 정의합니다.

## 도커 파일 기본 구조

Dockerfile은 간단한 구문으로 구성되어 있으며, 주요 구성 요소는 다음과 같습니다:

- FROM: 이미지의 기반이 되는 베이스 이미지 지정
- WORKDIR: 컨테이너 내에서 명령어가 실행될 작업 디렉토리 설정
- COPY: 호스트의 파일이나 디렉토리를 이미지에 복사
- RUN: 이미지 빌드 과정 중 명령어를 실행
- CMD: 컨테이너가 시작될 때 실행할 명령어 설정
//==========================================
<자바>
application.properties 에 맞춘 포트번호와 같게 해야 열린다~!🌟
server.port=8686

도커로 올리기 전에 빌드된게 있어야함~! jar 파일!@
gradle 기준- > 클린-> 빌드 하면 폴더 생김 build/lib/ooooooo.jar

[Dockerfile]
```dtd
FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

<터미널>
//빌드   -t : 태그명   (빌드명)
docker build -t spring-test .

docker run -d -p 8686:8686 --name node-container my-node-app

localhost:8686 확인
