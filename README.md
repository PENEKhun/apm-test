# APM 테스트 프로젝트

## 프로젝트 목적
이 프로젝트는 다양한 APM(Application Performance Monitoring) 도구들을 테스트하고 비교하기 위한 Spring Boot 기반의 테스트 애플리케이션입니다.

### 주요 목표
- 다양한 APM 도구(SigNoz, Sentry, Datadog)의 성능과 기능 비교
- 사내 APM 도입 전 적합한 도구 선정을 위한 테스트
- 각 APM 도구의 장단점 분석 및 사용성 평가

## 기술 스택
- Java 17
- Spring Boot 3.2.3
- MySQL 8.0
- Oracle XE
- Docker & Docker Compose

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
- Docker & Docker Compose
- Gradle

### 2. 데이터베이스 실행
```bash
# Docker Compose로 MySQL과 Oracle 실행
docker-compose up -d
```

### 3. 애플리케이션 실행
```bash
# Gradle로 애플리케이션 실행
./gradlew bootRun
```

## API 엔드포인트

### 기본 테스트
- GET `/api/test`
  - 기본 연결 테스트용 엔드포인트

### MySQL 테스트
- POST `/api/users`
  - 파라미터:
    - name: 사용자 이름
    - email: 이메일 주소
  - 예시: `/api/users?name=홍길동&email=hong@example.com`

### Oracle 테스트
- POST `/api/products`
  - 파라미터:
    - name: 상품명
    - price: 가격
  - 예시: `/api/products?name=노트북&price=1000000`

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