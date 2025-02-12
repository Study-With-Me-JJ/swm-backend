FROM amazoncorretto:21

WORKDIR /app

RUN yum update -y && \
    yum install -y wget unzip && \
    # Google Chrome 설치
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm && \
    yum install -y ./google-chrome-stable_current_x86_64.rpm && \
    rm -rf google-chrome-stable_current_x86_64.rpm && \
    yum clean all

ENV GOOGLE_CHROME_BIN=/usr/bin/google-chrome

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
