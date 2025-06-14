# apm test

- Java 17
- Spring Boot 3.2.3
- MySQL 8.0
- Oracle XE
- Docker
- Sentry (ARM64 지원 버전)

## 실행 방법

### 1. 사전 요구사항
- Java 17 이상
- Docker 및 Docker Compose
- Git

### 2. 프로젝트 클론
```bash
git clone https://github.com/PENEKhun/apm-test.git
```

### 3. 데이터베이스 실행
```bash
# Docker Compose로 모든 서비스 실행 (MySQL, Oracle, Sentry, Spring Boot 애플리케이션)
docker compose up -d
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
- [] SigNoz
- [] Sentry
- [] Datadog

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
