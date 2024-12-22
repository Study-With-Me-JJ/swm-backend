# 1단계: 빌드 이미지
FROM amazoncorretto:21 as builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle wrapper와 소스 코드 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src ./src
COPY swm-backend-secret ./swm-backend-secret

# Gradle 캐시를 활용한 종속성 미리 다운로드
RUN ./gradlew dependencies --no-daemon --stacktrace

# 애플리케이션 빌드
RUN ./gradlew bootJar --no-daemon --stacktrace

# 2단계: 런타임 이미지
FROM amazoncorretto:21 as runtime

# 작업 디렉토리 설정
WORKDIR /app

# Chrome 및 ChromeDriver 설치를 위한 패키지 업데이트
RUN yum update -y && \
    yum install -y wget unzip && \
    # Google Chrome 설치
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum install -y ./google-chrome-stable_current_x86_64.rpm && \
    rm google-chrome-stable_current_x86_64.rpm && \
    yum clean all

# Chrome 환경변수 설정
ENV GOOGLE_CHROME_BIN=/usr/bin/google-chrome
ENV CHROMEDRIVER=/usr/local/bin/chromedriver

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
