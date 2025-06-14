# apm test

- [ ] Spring Boot 앱
- [x] Docker Compose
  - [x] MySQL 8.0
  - [x] Oracle XE
- [x] Sentry
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

## API 엔드포인트

### 기본 테스트
- GET `/api/test`
  - 기본 연결 테스트용 엔드포인트

### MySQL 테스트
- GET `/api/mysql/test`
- POST `/api/mysql/test`

### Oracle 테스트
- GET `/api/oracle/test`
- POST `/api/oracle/test`

### Sentry 테스트
- GET `/api/sentry-test`

## TODO


#### Sentry 초기 설정
1. 데이터베이스 마이그레이션 및 초기 설정
```bash
# Sentry 컨테이너에 접속하여 초기화 명령어 실행
docker compose exec sentry sentry upgrade

# 프롬프트가 나타나면 'Y'를 입력하여 관리자 계정 생성
# 이메일과 비밀번호를 입력하여 계정 생성
```

3. 웹 인터페이스 접속
- URL: http://localhost:9000
- 생성한 관리자 계정으로 로그인
