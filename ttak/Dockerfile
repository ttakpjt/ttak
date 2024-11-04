# 빌드 스테이지
FROM gradle:8.6-jdk17 AS build-stage

# 환경 변수 설정
ENV ANDROID_COMPILE_SDK=34
ENV ANDROID_BUILD_TOOLS=34.0.0
ENV KOTLIN_VERSION=1.9.0
ENV AGP_VERSION=8.6.0
ENV ANDROID_HOME=/usr/local/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐싱을 위한 gradle.properties 설정
RUN echo "org.gradle.jvmargs=-Xmx6g -XX:MaxPermSize=1024m -XX:+HeapDumpOnOutOfMemoryError" > /app/gradle.properties \
    && echo "org.gradle.parallel=true" >> /app/gradle.properties \
    && echo "org.gradle.daemon=false" >> /app/gradle.properties \
    && echo "org.gradle.caching=true" >> /app/gradle.properties

# Gradle 캐싱을 위한 레이어 최적화
COPY gradle/ gradle/
COPY gradlew ./gradlew
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY app/build.gradle.kts app/
COPY gradle.properties .

# Gradle Wrapper 및 의존성 설치
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY app/src src/

# APK 빌드
RUN ./gradlew :app:assembleRelease --no-daemon

# 결과물 스테이지
FROM alpine:latest

# 빌드된 APK와 프로가드 매핑 파일 복사
COPY --from=build-stage /app/app/build/outputs/apk/release/app-release.apk /app/
# 매핑 파일이 존재할 경우에만 복사되도록 설정
RUN if [ -f "/app/app/build/outputs/mapping/release/mapping.txt" ]; then \
      cp /app/app/build/outputs/mapping/release/mapping.txt /app/; \
    fi

# 결과물 저장을 위한 볼륨
VOLUME ["/output"]