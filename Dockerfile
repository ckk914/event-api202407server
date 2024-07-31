# 기본 이미지로 OpenJDK가 설치된 Amazon Corretto 11 사용
#openJDK 라고 쳐도 알아서 가져올 수 있음
FROM amazoncorretto:11

# 작업 디렉토리 설정
WORKDIR /app

# 호스트의 Gradle 래퍼와 소스 코드를 이미지로 복사
COPY . /app

# 애플리케이션 빌드
RUN ./gradlew clean build

# 빌드된 JAR 파일 복사
RUN cp build/libs/*.jar app.jar

# 애플리케이션 실행 명령
CMD ["java", "-jar", "app.jar"]