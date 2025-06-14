# APM 테스트 프로젝트

## 프로젝트 목적
이 프로젝트는 다양한 APM(Application Performance Monitoring) 도구들을 테스트하고 비교하기 위한 Spring Boot 기반의 테스트 애플리케이션입니다.

### 주요 목표
- 다양한 APM 도구들의 기능과 성능을 비교
- 각 APM 도구의 장단점 파악
- 실제 운영 환경에서의 적용 가능성 평가
- APM 도구의 데이터 수집 방식과 정확도 비교

## 기술 스택
- Java 17
- Spring Boot 3.2.3
- MySQL 8.0
- Oracle XE
- Docker
- Sentry (ARM64 지원 버전)

## 프로젝트 구조
```
src/main/java/com/example/apmtest/
├── config/          # 설정 파일
├── controller/      # API 컨트롤러
├── entity/         # JPA 엔티티
│   ├── mysql/      # MySQL 엔티티
│   └── oracle/     # Oracle 엔티티
├── repository/     # JPA 레포지토리
│   ├── mysql/      # MySQL 레포지토리
│   └── oracle/     # Oracle 레포지토리
└── service/        # 비즈니스 로직
```

## 실행 방법

### 1. 사전 요구사항
- Java 17 이상
- Docker 및 Docker Compose
- Git

### 2. 프로젝트 클론
```bash
# 서브모듈을 포함하여 클론
git clone --recursive https://github.com/PENEKhun/apm-test.git

# 또는 이미 클론한 경우 서브모듈 업데이트
git submodule update --init --recursive
```

### 3. 데이터베이스 실행
```bash
# Docker Compose로 모든 서비스 실행 (MySQL, Oracle, Sentry, Spring Boot 애플리케이션)
docker compose up -d

# 또는 특정 서비스만 실행
docker compose up -d mysql oracle  # 데이터베이스만 실행
docker compose up -d sentry        # Sentry만 실행
docker compose up -d app           # Spring Boot 애플리케이션만 실행
```

### 4. 애플리케이션 실행
```bash
# 방법 1: Docker Compose로 실행 (권장)
docker compose up -d app

# 방법 2: Gradle로 직접 실행
./gradlew bootRun
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

## 데이터베이스 접속 정보

### MySQL
- Host: localhost
- Port: 3306
- Database: apmtest
- Username: user
- Password: password

### Oracle
- Host: localhost
- Port: 1521
- Service Name: XEPDB1
- Username: system
- Password: oracle

## APM 연동 예정
1. SigNoz
2. Sentry
3. Datadog

각 APM 도구별 연동 방법과 설정은 추후 업데이트될 예정입니다. 

## APM 도구 통합 및 설치 안내

### Sentry 설치 및 실행
- 공식 Sentry 이미지는 ARM(M1/M2) 및 x86 환경 모두 지원합니다.
- Docker Compose로 Sentry 및 의존성 서비스(Redis, Postgres, ClickHouse)를 실행합니다.

```bash
# 프로젝트 루트에서 실행
# 최초 실행 시
# 필요한 환경변수(SENTRY_SECRET_KEY 등)는 docker-compose.yml에서 설정

docker compose up -d
```

#### Sentry 초기 설정
1. 데이터베이스 마이그레이션 및 초기 설정
```bash
# Sentry 컨테이너에 접속하여 초기화 명령어 실행
docker compose exec sentry sentry upgrade

# 프롬프트가 나타나면 'Y'를 입력하여 관리자 계정 생성
# 이메일과 비밀번호를 입력하여 계정 생성
```

2. Sentry 서비스 재시작
```bash
docker compose restart sentry
```

3. 웹 인터페이스 접속
- URL: http://localhost:9000/auth/login/
- 생성한 관리자 계정으로 로그인

#### 주의사항
- SENTRY_SECRET_KEY는 최소 32자 이상의 안전한 문자열을 사용해야 합니다.
- 초기 설정이 완료되면 Sentry 대시보드에서 프로젝트를 생성하고 DSN을 발급받을 수 있습니다.

### SigNoz (예정)
- 오픈소스 APM 도구
- 분산 추적 및 메트릭 수집

### Datadog (예정)
- 클라우드 기반 APM 솔루션
- 종합적인 모니터링 기능