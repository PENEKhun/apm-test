# apm test

- [ ] Spring Boot 앱
- [x] Docker Compose
  - [x] MySQL 8.0
  - [x] Oracle XE
- [x] Sentry
  - [ ] 개인적으로 메소드 인자 추적만 되면 충분히 사용할 수 있을 것 같음.
    - AOP로 어떻게 할 지 고민 중...
- [ ] SigNoz
- [ ] Datadog

## 실행 방법

### 1. 사전 요구사항

- Sentry Up

> apple 실리콘 모델 기준 (처음에 오래걸리고 리소스 많이 잡아먹음 참고바람)

```bash
git clone https://github.com/getsentry/self-hosted.git
cd self-hosted
./install.sh

# 기다렸다가 관리자 이메일 비밀번호 입력하라는 란 나오면 입력
```

  - JAVA 프로젝트 생성


### 2. 프로젝트 클론
```bash
git clone https://github.com/PENEKhun/apm-test.git
```

### 3. 데이터베이스 실행
```bash
docker compose up -d
```

### 4. Env 수정

- application.yml

```yml
sentry:
  dsn: 앞에서 만든 dsn
```

- build.gradle

```groovy
sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = false

    org = 'sentry'
    projectName = 'apm-test'
    authToken = '앞에서 만든 토큰'
}
```

5. Application 실행

```bash
./gradlew build -xTest
export OTEL_LOGS_EXPORTER=none;OTEL_METRICS_EXPORTER=none;OTEL_TRACES_EXPORTER=none;SENTRY_AUTO_INIT=false;java -javaagent:sentry-opentelemetry-agent-8.13.3.jar -jar build/libs/apm-test-0.0.1-SNAPSHOT.jar
```


## API 엔드포인트

1. API 테스트

`apm-test.http`에 API 실행이 담겨있음.
이것저것 실행하고 결과 확인하면 됨.

2. 웹 인터페이스 접속해서 확인
- URL: http://localhost:9000
- 생성한 관리자 계정으로 로그인

