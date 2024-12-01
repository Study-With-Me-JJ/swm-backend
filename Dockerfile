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

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot 프로파일을 dev로 설정
ENV SPRING_PROFILES_ACTIVE=dev

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
