# HANDOFF — 진행 상황 & 다음 할 일 (백엔드)

> 세션 시작 시 이 파일을 먼저 읽고 이어서 작업.
>
> **이 파일은 "상태 · 다음 할 일 · 함정"만 담는다.** 설계는 아래로 미루고 여기에 중복하지 않는다. **끝난 슬라이스는 결론만 남기고 자세한 과정은 git 기록**에 맡긴다(2026-11-03 정리 — 진행 표·측정 기록은 커밋 본문에 있다).
> - 시스템 설계(스키마·API·역할분담) → **프론트 레포 `docs/ARCHITECTURE.md`** (로컬 경로는 PC마다 다름 → [CLAUDE.md](CLAUDE.md))
> - 작업 규칙(역할·커밋·브랜치·스택 주의점) → [CLAUDE.md](CLAUDE.md)
> - 프론트 진행 상황 → 프론트 레포 `HANDOFF.md`

**마지막 업데이트: 2026-11-24 (데스크톱)** — **F6.5 Flyway 머지됨**(PR #9 · `4d10883` · 테스트 16건) · **두 PC 모두 로컬 전환 끝**(데스크톱 2026-11-23) → **D1 첫 배포 진행 중**(브랜치 `feat/deploy` · 로컬 예행까지 끝(jar · 이미지 · 환경변수 실행 · 빈 MySQL 8.4) · 4단계 준비 끝(비root 계정 · arm64 이미지) · 4단계 ① 계정 점검(보기) 끝 2026-11-24 · **운영 형태를 EC2 한 대(앱 + MySQL 컨테이너)로 바꿈 2026-11-24 — RDS 는 1주 실습** · **4단계 ① 계정 끝 2026-11-24**(점검 · 예산 알림 · IAM 사용자 `newbeez-admin` · 크레딧 $120) · **② EC2 활동을 연습용 인스턴스로 끝냄 2026-11-24**(만들고 종료 · 활동 2/6 · 획득 $40) · **② 진짜 서버 `newbeez-server` 켬 2026-11-24 14:46**(`t4g.small` · AL2023 arm64 · 20GB · 크레딧 사양 standard — ⚠️ 여기서부터 크레딧이 빠진다) · **다음 ② SSH 접속 → Docker 설치**(키 파일은 노트북 다운로드 폴더에 있다)) — 아래 "D1 진행" · 호스팅은 **AWS 무료 플랜**으로 결정 2026-11-14 · 맨 아래 "그 외"). 설계 검토 결정(2026-11-09)은 프론트 `main` 에 머지됨(프론트 PR #10 · `0e095d9` · 브랜치 `docs/review-decisions-2026-11-09` 는 원격에서 삭제됨 — 2026-11-23 확인) · D1 체크리스트는 맨 아래 "그 외". F7 팀 소개 위치는 재검토 중(아래 F7 절 ⏸)
🔀 **브랜치** — D1 은 `feat/deploy`. 슬라이스 끝에 PR 하나 · `main` 에 직접 커밋·push 하지 않는다 — PR 로만 ([CLAUDE.md](CLAUDE.md))

### ▶ 다른 PC에서 이어갈 때

대화 기록과 `.env`·DB 는 PC를 넘어가지 않는다. 넘어가는 것은 git 에 올린 것뿐이다. 떠나는 PC 에서 할 일은 [CLAUDE.md](CLAUDE.md) "PC 를 떠날 때".

1. **백엔드** — `git fetch --prune` 후 작업 브랜치로(`git switch <브랜치>` · `git pull`). PR 이 이미 머지됐으면 `git switch main` · `git pull` 하고 머지된 로컬 브랜치는 `git branch -d` 로 지운다(옛 `feat/participant-log` · `feat/participant-api` · `feat/cors` · `feat/flyway` 가 남아 있으면 같이) · **F6.5 로컬 전환(Hibernate 가 만든 옛 표 `DROP` → `bootRun`)은 두 PC 모두 끝났다**(노트북 2026-11-22 · 데스크톱 2026-11-23) — 새 PC 는 빈 DB 라 `bootRun` 만 하면 Flyway 가 V1 로 표와 `flyway_schema_history` 를 만든다. ⚠️ **장부가 없는데 스키마에 표가 하나라도 있으면** Flyway 는 V1 을 실행하기 **전에** ``Found non-empty schema(s) `newbeez` but no schema history table. Use baseline() or set baselineOnMigrate to true …`` 로 기동을 멈춘다 — 장부도 만들지 않고 DB 는 그대로다(2026-11-23 데스크톱 측정 · `Flyway.java:221` · `Successfully validated 1 migration` 은 그 전에 찍힌다. 옛 기록 "`CREATE TABLE` 이 '이미 있음'으로 실패하고 `success=0` 행이 남는다"는 틀렸다) → `SHOW TABLES;` 가 비어야 한다 · ⚠️ **커밋 안 한 변경부터 치운다** — 원격에서 지운 파일(예: 2026-10-29 에 지운 `ParticipantLogRepositoryTest.java`)을 그 PC 에서 고치고 있었다면 `switch` 가 거부되지 않고 **그 파일을 새 브랜치로 조용히 들고 간다**(2026-11-02 노트북 재현). 먼저 커밋하거나 `git stash`
2. **프론트** — `git switch main` 후 `git pull` (§6 계약: PR #3 `POST /participants` · #4 주소 버전 제거 · #5 참여자 수 · #6 캐시 10초)
3. **그 PC 의 준비** — 아래 F6-1 표에서 ⬜ 인 것부터(계정 · `.env` — DB 3줄 + `NEWBEEZ_CORS_ALLOWED_ORIGINS` · `NEWBEEZ_DDL_AUTO` 는 **없어야** 한다). `bootRun` 이 뜨면 Flyway 가 그 PC 의 DB 에 `participant_logs` 와 `flyway_schema_history` 를 만든 것이다 — Workbench `SHOW CREATE TABLE participant_logs;` 가 `V1__participant_logs.sql` 과 같은 모양인지(칸 순서 그대로 · `KEY idx_cat_created`) 본다
   - ⚠️ **Docker 이미지도 PC 를 넘어가지 않는다**(2026-11-23) — `newbeez-back:latest`(amd64) · `newbeez-back:arm64` 는 데스크톱(`jun98`)에만 있다. git 으로 넘어가는 것은 `Dockerfile` · `.dockerignore` 뿐. 그 PC 에서 이미지가 필요해지면(D1 4단계 ③ 이미지 옮기기부터) Docker Desktop 을 켜고 `./gradlew bootJar` → `docker build -t newbeez-back .` → `docker build --platform linux/arm64 -t newbeez-back:arm64 .` 로 다시 만든다(같은 `Dockerfile` + 같은 소스라 같은 이미지가 나온다). 노트북(`이현준`)에 Docker 가 깔려 있는지는 **미확인** — `docker version` 으로 먼저 본다. 4단계 ①·②(AWS 콘솔)는 브라우저 작업이라 Docker 없이 진행된다
4. **IntelliJ 를 쓰면 세 가지를 먼저 본다** — `.idea` 는 git 이 무시해서 PC마다 설정이 다르다
   - **Project SDK 가 21 인가** (File → Project Structure → Project → SDK). `jun98` PC 에는 실험판 `valhalla-ea-23` 이 잡혀 있었다 → 2026-10-16 `ms-21` (21.0.11) 로 바꿈
   - **실행은 상단 드롭다운의 `NewbeezBackApplication`(Spring Boot) ▶** — Gradle 실행 설정은 task 칸이 비어 있으면 `> Task :help` 만 돌고 앱이 안 켜진다
   - **`build.gradle` 이 바뀌었으면 Gradle 창의 Sync All Gradle Projects** — 안 하면 새 라이브러리의 `import` 가 빨갛다. Gradle 은 그 PC 에서 쓴 것만 받아 두고 30일 안 쓴 캐시는 지운다(2026-11-02 노트북 캐시엔 validation 이 없었다)
5. 아래 **로드맵에서 ⬜ 인 첫 슬라이스**를 이어간다 — 지금은 **D1 4단계 ② 서버 `newbeez-server` 에 SSH 로 접속 → Docker Engine 설치**(⚠️ 서버는 2026-11-24 14:46 부터 켜져 있다 — 크레딧이 빠진다 · 키 파일 `newbeez-server-key.pem` 은 노트북의 다운로드 폴더에 있다 — 접속은 키가 있는 PC 에서 한다 · 아래 "4단계 ② 진짜 서버 `newbeez-server` 에서 측정한 것"). EC2 활동은 연습용 인스턴스를 만들고 종료해 2026-11-24 에 끝냈다(보상 받음). ① 계정(점검 · 예산 알림 · IAM 사용자)은 2026-11-24 에 끝났다(아래 "D1 진행"의 "4단계 ① …에서 측정한 것" 세 묶음) · **AWS 에는 루트가 아니라 `newbeez-admin` 으로 로그인한다**(콘솔 로그인 URL · 암호 · MFA 는 사용자가 보관 — 문서에 적지 않는다)
6. ⚠️ **Claude 의 메모리는 PC마다 따로다** — 다른 PC가 알아야 할 결론은 이 파일과 프론트 문서에 옮겨 둔다. 학습 진행 방식과 작업 규칙은 [CLAUDE.md](CLAUDE.md) 에 적었다

---

## 🗺️ 슬라이스 로드맵

**MVP 경계는 프론트 ARCHITECTURE §6 (엔드포인트 4개).**

| | 슬라이스 | 상태 |
| --- | --- | --- |
| **F6-1** | **DB 설정** — `application.yml` + 환경변수 접속 → `bootRun` 성공 | ✅ `44e2283` (2026-10-06) |
| **F6-2** | `participant_logs` 엔티티 + Repository | ✅ PR #2 `5559dd6` (2026-10-27) |
| **F6-3** | `POST /api/participants` — 4계층 첫 관통 | ✅ PR #3·#4·#5 `8e640af` (2026-10-29) |
| **F6-4** | `GET /api/participants/count?categorySlug=` + **10초 캐시** | ✅ PR #7 `009796a` (2026-11-03) |
| 선택 | **Redis 캐시 실습** — 학습용 · 배포에 넣지 않는다. 아래 F6-4 절 "선택 과제" | ⬜ 아무 때나 (2026-11-03 추가) |
| **F6-5** | **CORS** — 브라우저가 직접 부르는 두 엔드포인트만 | ✅ PR #8 `a4185c9` (2026-11-20) |
| **F6.5** | **Flyway 전환** — `V1__participant_logs.sql` · `ddl-auto` 는 `validate` 고정 | ✅ PR #9 `4d10883` (2026-11-22) |
| **D1** | **첫 배포** — Dockerfile · 운영 MySQL 8.4 · 환경변수 · 유휴 회수 없는 호스팅(**AWS 무료 플랜** · 2026-11-14 결정) | 🚧 **진행 중** — 3/4 (아래 "D1 진행") |
| 연동 | 프론트가 완료 시 `POST` + `ParticipantCount` — 실제 참여자 수·답코드가 쌓이기 시작 | ⬜ (프론트 작업) |
| F7 | `team_results`(읽기 전용 · Flyway `V2__` + `R__` 시드 15행) + `GET /results` | ⬜ **배포·연동 뒤** — 설계 확정 2026-10-20 · 백엔드 유지 2026-10-27. 상세는 아래 "F7 설계 확정" |

**순서 결정 (2026-10-27)** — 참여 기록 하나를 **배포까지 먼저** 끝낸다. 실제로 쓰이는 기능이 일찍 생기고, 답코드가 일찍 쌓이며(§5.2 "기록하지 않은 답은 되살릴 수 없다"), S8 이 의존하는 백엔드 호스팅이 S8 전에 준비된다.
**팀 콘텐츠는 백엔드에 둔다 (2026-10-27 재검토)** — 프론트 정적 데이터로 옮기는 안을 검토했으나 유지. 팀 소개·인물은 코드와 따로 바뀌는 **편집 콘텐츠**라 DB 가 맞고, 3차 UGC·4차 카테고리도 DB 에 쌓인다. 대가: S8 이후 **백엔드가 죽으면 프론트 새 배포가 실패**한다(기존 결과 페이지는 마지막 성공본 유지) → D1 의 호스팅은 **유휴 회수가 없어야** 한다

이후: 댓글 (2차)

---

## 🚧 D1 첫 배포 — 진행 (브랜치 `feat/deploy`)

코드는 그대로 두고 **실행 환경만** PC 밖으로 옮긴다 — 실행 방법(Gradle → 이미지) · DB(로컬 8.0 → 운영 8.4) · 설정값(`.env` → 환경변수) · 주소(`localhost:8080` → `api.newbeez.kr`).

| | 조각 | 상태 |
| --- | --- | --- |
| 1 | `./gradlew bootJar` → `java -jar` 로 Gradle 없이 기동 | ✅ 2026-11-23 (데스크톱) |
| 2 | `Dockerfile` · `.dockerignore` → 이미지 빌드 | ✅ 2026-11-23 (데스크톱) |
| 3 | 컨테이너를 `.env` 없이 환경변수만으로 실행 | ✅ 2026-11-23 (데스크톱 · 로컬 MySQL 8.0 에 붙임) |
| 3+ | 예행 — **빈 MySQL 8.4 컨테이너**에 붙여 Flyway 첫 적용을 본다(운영과 같은 조건) | ✅ 2026-11-23 (데스크톱 · MySQL 8.4.13 · 경고 없음) |
| 4 준비 | ① 컨테이너를 비root 계정으로 ② arm64 이미지 | ✅ 2026-11-23 (데스크톱 · ① `1898e84` · ② `Dockerfile` 변경 없음) |
| 4 | AWS (EC2 한 대에 앱 + MySQL · HTTPS · RDS 는 1주 실습 — 2026-11-24 변경) | 🚧 ① 계정 ✅ 2026-11-24(점검 · 예산 알림 · IAM 사용자) · ② 연습용 인스턴스 ✅ 2026-11-24(EC2 활동 · 만들고 종료) · ② 진짜 서버 `newbeez-server` 켬 ✅ 2026-11-24 14:46 · **← 다음** ② SSH 접속 → Docker 설치 |

**1단계에서 측정한 것** (2026-11-23 · 데스크톱)
- **`bootJar` 는 실행용 jar 만 다시 만든다** — `build/libs` 의 `-plain.jar` 는 2026-11-03 것(7,371바이트 · `CorsConfig`·`db/migration` 없음)이 그대로 남았다 → Dockerfile 에서 `COPY build/libs/*.jar` ✗ · 파일 이름을 정확히 쓴다. 실행용 jar 는 56,155,756바이트 · `4 actionable tasks: 1 executed, 3 up-to-date`
- **jar 안** — `BOOT-INF/classes/`(우리 `.class` 8개 · `application.yml` · `db/migration/V1__participant_logs.sql` — 소스와 바이트까지 같다) · `BOOT-INF/lib/`(라이브러리 jar 77개) · `org/springframework/boot/loader/` · `MANIFEST.MF` 의 `Main-Class: org.springframework.boot.loader.launch.JarLauncher` → `Start-Class: com.newbeez.newbeezback.NewbeezBackApplication`. **`.env` 는 jar 에 없다** — `application.yml` 은 `${…}` 빈칸인 채로 들어간다
- **`.env` 는 "실행한 폴더"에서 찾는다**(`optional:file:.env` · 로그 첫 줄 끝의 `started by … in <폴더>`). 프로젝트 폴더에서 `java -jar build\libs\…jar` → 기동 · `{"count":0}` · `Access-Control-Allow-Origin` 확인. **`build\libs` 로 들어가 같은 jar 를 켜면 `'url' must start with "jdbc"` 로 죽는다**(Hikari 로그가 한 줄도 없다 · CORS 빈칸보다 DB URL 이 먼저 걸린다) — 컨테이너·서버에서 환경변수를 빠뜨리면 보게 될 오류다
- jar 로 켠 로그의 차이 — 첫 줄에 `v0.0.1-SNAPSHOT`(매니페스트의 `Implementation-Version`)과 jar 경로 · 스택의 `spring-beans-7.0.8.jar!/` 의 `!/` 는 jar 안의 jar · 맨 아래 프레임이 `JarLauncher.main` → `Launcher.launch` → `NewbeezBackApplication.main`
- **프론트 호스팅은 Vercel Hobby 유지**(2026-11-23 재확인) — 약관 §4(2026-10-19 개정) "personal or non-commercial use" · 월 한도(전송 100GB · CDN 요청 100만 · 함수 호출 100만)를 넘으면 과금이 아니라 대부분 30일 정지 · 정적 내보내기는 `redirects()`·`next/image` 기본 로더·ISR 이 미지원이라 ✗(Next 16.2.6 번들 문서 `node_modules/next/dist/docs`). 광고를 켜면 Pro — 프론트 FUTURE §6 그대로. "상업 사용"의 정의와 후원 예외 문구는 이날 공식 페이지 본문이 읽히지 않아 프론트 LEGAL §4 의 2026-09-14 기록에 기대고 있다

**2단계에서 측정한 것** (2026-11-23 · 데스크톱 · Docker 28.5.2)
- **방식** — jar 는 PC 에서 `./gradlew bootJar` 로 만들고 `Dockerfile`(2단계 때 4줄 · 비root 2줄을 더해 지금 6줄)은 그 jar 를 복사만 한다. ⚠️ **이미지를 빌드하기 전에 `bootJar` 를 다시 돌린다** — 안 돌리면 옛 jar 가 그대로 들어간다(jar 는 만든 시점의 스냅샷). 이미지 안에서 소스부터 빌드하는 멀티 스테이지는 자동 배포를 붙일 때로 미뤘다
- `docker build -t newbeez-back .` — 첫 빌드 20초(바탕 내려받기) · 두 번째 2.3초(`CACHED`). **빌드는 앱을 켜지 않는다**(Spring 로그 없음 · DB 불필요) — `ENTRYPOINT` 는 "켜질 때 실행할 명령"을 적어 두는 것뿐
- 이미지 안 — `linux/amd64` · `WorkingDir /app` · `/app/app.jar` 56,155,756바이트(PC 의 jar 와 같다) · **Temurin 21.0.13**(PC 는 Oracle 21.0.8 — 컨테이너는 PC 의 Java 를 쓰지 않는다) · Ubuntu 26.04 · **시계는 UTC**(jar 시각 04:41 = KST 13:41) · `NEWBEEZ_…` 환경변수 0개 · 실행 계정 root(→ 같은 날 `app` 으로 바꿨다 · 아래 "4단계 준비 ①")
- 크기 — 층 합 약 414MB(Ubuntu 117 + 74.8 · Java 166 · **우리 jar 56.2**) · `docker image inspect` 의 Size 172MB(압축본 = 네트워크로 오가는 양) · `docker images` 의 586MB 는 둘의 합(414 + 172). 다시 배포할 때 오가는 것은 바뀐 jar 층뿐 · 이미지 크기는 디스크 문제이지 메모리 문제가 아니다. 이미지는 프로젝트 폴더가 아니라 `%LOCALAPPDATA%\Docker\wsl\disk\docker_data.vhdx` 안(`/var/lib/docker`)에 있다
- `.dockerignore` 는 허용 목록(`*` + `!build/libs/newbeez-back-0.0.1-SNAPSHOT.jar`). 지금 Dockerfile 에서는 결과가 같다(BuildKit 이 넘긴 것은 처음부터 jar 뿐 — `transferring context: 56.17MB`) — **나중에 `COPY . .` 가 생겨도 `.env` 가 이미지에 못 들어가게 하는 안전장치**다. 확인: 임시 Dockerfile `COPY . /ctx` 로 빌드하면 보이는 파일이 jar 1개뿐 · `.env` 없음
- **3단계에서 측정한 것** (같은 날)
  - 설정값 없이 `docker run --rm newbeez-back` → `'url' must start with "jdbc"`. 로그: `PID 1` · `(/app/app.jar started by root in /app)` · 시각 끝이 `Z`(UTC) · **`No active profile set, falling back to 1 default profile: "default"`** — PC 의 `SPRING_PROFILES_ACTIVE=loc` 가 안 들어간다. 컨테이너 안에는 **명시적으로 넘긴 값만** 있다
  - **컨테이너 안의 `localhost` 는 컨테이너 자신** — 안에서 `localhost:3306` 은 안 닿고 `host.docker.internal:3306`(→ 192.168.65.254 · Docker Desktop 이 주는 이름 · AWS 에는 없다)은 열려 있다
  - `docker run --rm --env-file .env -e NEWBEEZ_DB_URL=jdbc:mysql://host.docker.internal:3306/newbeez -p 8080:8080 newbeez-back` → 6.1초에 기동 · `Database: jdbc:mysql://host.docker.internal:3306/newbeez (MySQL 8.0)` · `Current version of schema: 1` · **MySQL 계정 `'newbeez'@'localhost'` 가 그대로 통했다** · `{"count":0}` · 허용 출처엔 `Access-Control-Allow-Origin` · 다른 출처 403 · **메모리 358MiB**(`docker stats` — 서버 크기를 정할 때 쓰는 숫자)
  - `--env-file` 은 Docker 가 **PC 의** 파일을 읽어 환경변수로 넣는 것 — 컨테이너 안에 `.env` 파일은 없다 · 주석·빈 줄·CRLF 그대로 된다 · **`-e` 가 `--env-file` 을 이긴다**(가짜 변수로 확인) → "파일 값 전부 + 하나만 다르게"
  - `-p 8080:8080` 동안 PC 의 8080 을 쥔 것은 `wslrelay`·`com.docker.backend`(PC 에 java 프로세스 없음). `0.0.0.0` 으로 열린다 → 서버에서 nginx 뒤에 둘 때는 `-p 127.0.0.1:8080:8080` 을 검토
  - ⚠️ PowerShell 에 긴 한 줄을 붙여 넣다가 `-p` 에서 잘려 `flag needs an argument: 'p' in -p` — 백틱(`` ` ``)으로 줄을 나눠 주거나, 끝이 이미지 이름으로 끝났는지 본다
  - 종료 — `Ctrl+C` 뒤 `docker ps -a` 가 비었다(`--rm`). 정상 종료 로그(`Commencing graceful shutdown`)는 못 봤다 → 같은 날 `docker stop` 으로 종료 신호가 닿는 것까지 확인(아래 "4단계 준비 ①" · 로그 글자는 여전히 못 봤다)
- **예행(빈 MySQL 8.4)에서 측정한 것** (같은 날 · Docker Desktop 이 켜져 있어야 한다 — 꺼져 있으면 `docker` 명령이 `open //./pipe/dockerDesktopLinuxEngine` 오류)
  - 띄우기 — `docker network create newbeez-net` → `docker run -d --name newbeez-mysql --network newbeez-net -e MYSQL_DATABASE=newbeez -e MYSQL_USER=newbeez -e MYSQL_PASSWORD=<예행용> -e MYSQL_ROOT_PASSWORD=<예행용> mysql:8.4`(8.4.13 · **`-p` 없음** — PC 의 3306 과 충돌하지 않고 같은 네트워크의 컨테이너만 접속한다 · 계정은 `newbeez@%` · 서버 시각 UTC · 로그에 `ready for connections` 가 두 번 나오고 `port: 3306` 이 붙은 두 번째가 진짜다) → 앱은 **`.env` 없이 `-e` 4개로**: `docker run --rm --network newbeez-net -e NEWBEEZ_DB_URL=jdbc:mysql://newbeez-mysql:3306/newbeez -e NEWBEEZ_DB_USERNAME=… -e NEWBEEZ_DB_PASSWORD=… -e NEWBEEZ_CORS_ALLOWED_ORIGINS=http://localhost:3000 -p 8080:8080 newbeez-back`
  - **MySQL 8.4.13 + Flyway 12.4.0 — 경고 없이 통과.** `Database: jdbc:mysql://newbeez-mysql:3306/newbeez (MySQL 8.4)` → 첫 적용 로그(F6.5 절과 같은 순서) → `Started … in 6.667 seconds`. `SHOW CREATE TABLE` 은 로컬 8.0 과 글자까지 같다 · 장부 체크섬 **668339657**(세 번째 환경 — Linux 컨테이너가 jar 안의 파일을 읽어도 같다) · **`installed_on` 이 UTC**(06:42:08 — PC 에서는 KST 였다)
  - 쓰기 경로 — `POST /api/participants`(`.http` ① 모양 + `Origin`) → 201 + `Access-Control-Allow-Origin` · 행의 `created_at` 06:42:36 이 `UTC_TIMESTAMP()` 와 1초 차 · `{"count":1}`. **JDBC 시간대 옵션 없이도 UTC 로 들어갔다 — 컨테이너의 JVM 과 DB 서버가 둘 다 UTC 였기 때문**이다. 옵션(`connectionTimeZone=UTC…`)이 일하는 것은 DB 서버 시간대가 UTC 가 아닐 때 → RDS 에서 실측으로 정한다
  - **컨테이너끼리는 이름으로 찾는다**(같은 사용자 정의 네트워크 · `newbeez-mysql` → 172.19.0.2). DB 주소가 하루에 세 번 바뀌었고(`localhost` → `host.docker.internal` → `newbeez-mysql`) 이미지는 한 글자도 안 바뀌었다
  - ⚠️ MySQL 비밀번호를 `rehearsal-pw-`(끝에 `-`)로 잘못 쳤는데 그대로 계정이 만들어졌다 — **환경변수는 오타를 검사하지 않는다.** 앱에 다른 값을 주면 `Access denied for user 'newbeez'@'…'`. 서버에서 값을 넣을 때 양쪽을 같은 곳에서 복사한다
  - 메모리 — 앱 333~358MiB · MySQL 8.4 445MiB(`docker stats`)
  - 정리 — `docker rm -f -v newbeez-mysql`(`-v` 가 데이터 볼륨까지 지운다) · `docker network rm newbeez-net`. `mysql:8.4` 이미지는 남겨 둔다(다시 예행할 때 내려받지 않는다)
- **4단계 준비 ① 비root 계정에서 측정한 것** (같은 날 · 커밋 `1898e84`)
  - `Dockerfile` 에 2줄 — `RUN useradd --system --uid 10001 app` 은 **`COPY` 위**(jar 가 바뀌어도 이 층은 캐시) · `USER app` 은 **`COPY` 아래**(jar 의 주인이 root 로 남는다). 2번째 줄에 끼워 넣자 글자가 그대로인 `WORKDIR`·`COPY` 도 다시 실행됐다(`CACHED` 는 `[1/4]` 뿐 · 7.9초) — 층은 앞 층 위에 쌓인다
  - `docker run --rm --entrypoint id newbeez-back` → `uid=10001(app) gid=999(app)`(앱을 켜지 않고 계정만 본다) · 이미지 설정 `User=app` · `/app/app.jar` 는 `0:0 -rwxr-xr-x` → `app` 계정으로 `/app` 에 파일 만들기 · jar 덧쓰기 둘 다 `Permission denied` · `/tmp` 쓰기는 된다(내장 Tomcat 작업 폴더가 여기 생긴다)
  - 이 계정으로 기동 — `docker run --rm --name newbeez-app --env-file .env -e NEWBEEZ_DB_URL=jdbc:mysql://host.docker.internal:3306/newbeez -p 8080:8080 newbeez-back` → 9분 동안 떠 있었다 · `docker exec newbeez-app ps -eo user,pid,args` → `app 1 java -jar app.jar` — **PID 1 이 `sh` 가 아니라 `java`** 다(`ENTRYPOINT` 를 JSON 배열로 쓴 결과 · 신호를 `java` 가 직접 받는다)
  - **종료 신호가 앱에 닿는다** — `docker stop newbeez-app` → `docker events` 기록: `kill signal=15` → **0.34초 뒤** `die exitCode=143`(128 + 15 = SIGTERM 을 받고 스스로 끝남). 신호가 안 닿았다면 10초 유예 뒤 `signal=9` · `137` 이 찍힌다. ⚠️ 근거는 이벤트 기록이다 — 터미널의 `Commencing graceful shutdown` 줄은 아직 눈으로 확인하지 못했다
  - `--rm` 컨테이너는 끝나면 로그도 사라진다(`docker logs` ✗) — 지나간 실행은 `docker events --since 30m --until 0s --filter container=<이름>` 으로 본다
- **4단계 준비 ② arm64 이미지에서 측정한 것** (같은 날 · `Dockerfile` 변경 없음 · AWS `t4g` 가 ARM 이라서)
  - 사전 확인 — 바탕 `eclipse-temurin:21-jre` 가 `linux/arm64/v8` 을 제공한다(`docker buildx imagetools inspect`) · 이 PC 의 빌더가 `linux/arm64` 를 지원한다(`docker buildx ls`) · 이미지 저장소가 containerd(`docker info` 의 `driver-type io.containerd.snapshotter.v1`)라 다른 CPU 용 이미지도 로컬에 담긴다
  - `docker build --platform linux/arm64 -t newbeez-back:arm64 .` → `newbeez-back:arm64` 는 `linux/arm64`(583MB 표시) · `newbeez-back:latest` 는 `linux/amd64` 그대로(586MB · 로컬 실행용으로 남긴다). 두 이미지 모두 `User=app` · `/app/app.jar` 56,155,756바이트 — **jar 는 CPU 를 가리지 않아 `bootJar` 를 다시 안 돌렸다**(CPU 를 타는 줄은 `FROM` 과 `RUN` 뿐). 빌드 로그 자체는 못 봤다(결과만 확인)
  - `docker run --rm --platform linux/arm64 --entrypoint uname newbeez-back:arm64 -m` → `aarch64` · 같은 방식의 `java -version` → Temurin 21.0.13+9 (amd64 PC 에서 QEMU 에뮬레이션으로 돈다)
  - **arm64 이미지로 앱 기동(에뮬레이션)** — 3단계 실행 명령에 `--platform linux/arm64` · 태그 `:arm64` 만 붙임 → `Started NewbeezBackApplication in 96.16 seconds (process running for 108.216)`(amd64 는 6.1~6.7초 — **약 15배 · 이 PC 에서만 느리다**) · 첫 줄 `started by app in /app`(전엔 `started by root`) · Flyway `Schema … is up to date` · 오류 없음. `docker stop` → `kill signal=15` → 0.75초 뒤 `exitCode=143`
  - ⚠️ 이름 오타 `newebeez-back:arm64` → `Unable to find image … locally` → `pull access denied … repository does not exist` — Docker 는 로컬에 없으면 **Docker Hub 에서 받으려 한다(이미지 이름은 주소).** 서버에서 이름을 틀리면 보게 될 오류다 · `docker image`(하위 명령이 붙는 묶음)와 `docker images`(목록)는 다른 명령
  - 남은 것 — 이미지를 서버로 옮기는 방법(저장소) 미정 · 정상 종료 로그 글자는 서버에서 `docker logs` 로 본다(`--rm` 없이 켜면 끝난 뒤에도 남는다)
- **4단계 순서(예정)** — ① 계정 ✅ 2026-11-24(점검 · 예산 알림 · 일상용 IAM 사용자 — 돈 안 들었다) ② EC2 `t4g.small` 한 대 + Docker Engine(위젯의 EC2 활동은 연습용 인스턴스로 끝냄 ✅ 2026-11-24 — 활동의 마지막이 삭제라 진짜 서버는 따로 만든다 · 크레딧 사양 Standard · 디스크 gp3 20GB · 진짜 서버 `newbeez-server` 켬 ✅ 2026-11-24 14:46 · **← 다음 SSH 접속 → Docker Engine 설치**) — ⚠️ **여기서부터 크레딧이 켜 둔 시간만큼 빠진다**(월 약 $20.66 · 아래 "운영 형태") → 켜는 날짜는 사용자가 정한다 ③ 이미지 옮기기 ④ 앱 + MySQL 8.4 컨테이너를 환경변수로 실행(예행과 같은 구성 · DB 데이터는 이름 있는 볼륨 · 메모리 한도) ⑤ `api.newbeez.kr` + HTTPS(nginx) ⑥ 운영 출처로 CORS 확인 · 프론트 연결 ⑦ `mysqldump` 백업 ⑧ RDS 1주 실습(위젯의 RDS 활동으로 시작 · 덤프·복원으로 옮겨 보고 삭제 · 최종 스냅샷 없이). 옛 순서(② RDS 상시 → ③ EC2)는 2026-11-24 에 바꿨다. 서버에 친 명령은 여기에 기록한다(이전 때 그대로 다시 친다)
- **4단계 ① 계정 점검에서 측정한 것** (2026-11-24 · 데스크톱 · 아무것도 만들지 않음 · 콘솔 언어는 한국어)
  - **신원** — 루트로 로그인 중 · IAM 대시보드 "보안 권장 사항"에 초록 체크 2개(루트 MFA 있음 · 루트 활성 액세스 키 없음). IAM 사용자 수 · 예산 목록은 화면으로 안 봤다 — 새 계정 초기 상태(둘 다 0)로 가정하고, 만들러 들어갈 때 목록에서 확인한다
  - **크레딧** — 결제 화면 "크레딧": $100 중 사용 $0 · 활성 크레딧 1개 · 시작 2026-11-14 · **만료 2027-11-14**(12개월). 콘솔 홈 "비용 및 사용량" 위젯: 남은 크레딧 US$100.00 · 남은 일수 `173일 (May 14, 2027)`. → **날짜가 둘이다: 무료 플랜 종료 2027-05-14(계정 정지) · 크레딧 만료 2027-11-14**(유료 플랜으로 전환하면 남은 크레딧이 그날까지 청구서에 적용된다 — 대신 초과분은 카드로 청구). 일수 173 은 오늘부터 세면 171 이라 2일 어긋난다(이유 미확인) — 괄호 안 날짜를 믿는다
  - **리전** — 새 계정 기본값이 미국 버지니아 북부(`us-east-1`)였다 → 서울(`ap-northeast-2`)로 바꿈(콘솔 홈 Security 위젯에 `리전: Asia Pacific (Seoul)`). ⚠️ 무언가 만들기 직전마다 오른쪽 위가 "서울"인지 본다. IAM · 결제 화면에서는 선택기가 "글로벌"로 나온다(계정 전체에 하나인 서비스라 리전이 없다) — 리전은 콘솔 홈이나 EC2·RDS 화면에서 본다
  - **크레딧 활동** — 콘솔 홈 **"AWS 살펴보기"** 위젯(영문 Explore AWS · 필터 "AWS 크레딧 받기") · 완료 0/6 · US$0 / US$100 · 활동마다 US$20 · 전부 "시작되지 않음": Lambda 웹 앱 · Bedrock 플레이그라운드 · **Aurora 또는 RDS 데이터베이스 생성** · 첫 DynamoDB 테이블 · **AWS Budgets 비용 예산 설정** · 6번째(위젯 2쪽)는 **EC2를 사용하여 인스턴스 실행**. 활동 이름을 누르면 안내 창이 뜨고 **"활동 시작" 버튼**으로 시작한다(예산 활동: 상태 "시작되지 않음" · 완료 기한 2027년 5월 14일 · 예상 5분). 공식 문서(`docs.aws.amazon.com/awsaccountbilling/latest/aboutv2/free-tier-plans-activities.html` · 2026-11-24 확인): 상한 $100(= 5개) · 예산 활동만 "서비스 요금이 크레딧에서 빠진다"는 주의가 없다 · **위젯을 거치지 않고 만든 것이 인정되는지는 문서에 없다** → 예산 · EC2(②) · RDS(⑧ 실습)는 **위젯의 "활동 시작"으로 시작한다**(어차피 할 일 셋으로 $60)
  - ⚠️ **콘솔 홈 Security 위젯의 "요금제 업그레이드" 버튼을 누르지 않는다** — 유료 플랜 전환이다(무료 플랜의 "크레딧을 넘어 청구하지 않는다"가 사라진다)
  - **Cost Anomaly Detection 이 저절로 켜졌다** — 결제 화면에 처음 들어가면 Cost Explorer 가 켜지고, AWS 가 무료 모니터 1개 + 일일 요약 메일 구독 1개를 만든 뒤 안내 메일을 보낸다(문턱: 이상 지출 $100 이상 + 예상 대비 40% 초과). 문턱이 크레딧 전체와 같아 **예산 알림을 대신하지 못한다** · 그대로 둔다 · AWS 메일의 링크는 누르지 않고 콘솔에서 확인한다
  - 설명이 통한 방식 — **메뉴 이름은 한국어 화면 글자로 준다**(영어 "Budgets" · "Explore AWS" · "카드"로 안내해 두 번 못 찾았다 → "예산" · "AWS 살펴보기" · "위젯") · 화면을 말로 옮기게 하지 말고 **캡처를 바탕 화면에 저장**하게 해 Claude 가 읽는다(`Win+Shift+S` · `jun98` PC 의 바탕 화면은 `C:\Users\jun98\OneDrive\바탕 화면`)
- **4단계 ① 예산 알림에서 측정한 것** (2026-11-24 · 데스크톱 · 루트로 · 요금 없음)
  - 순서 — 콘솔 홈 "AWS 살펴보기" → "AWS Budgets를 사용하여 비용 예산 설정" → 안내 창의 **"활동 시작"** → 예산 생성 화면에서 "템플릿 사용(단순)" · **월별 비용 예산** · 이름 `newbeez-monthly-cost` · 금액 `25`(EC2 한 대 월 예상 $20.66 보다 조금 위) · 이메일 수신자 → "예산 생성"
  - 결과 — 배너 `예산 newbeez-monthly-cost을(를) 생성했습니다. … 모든 지출 데이터를 채우는 데 최대 24시간` · **예산 (1)**(그 전에 0개였다는 가정이 맞았다) · 임계값 "확인" · 상태 "정상" · 오른쪽 "자습서 - 신규" 창에 `축하합니다! 자습서를 완료했습니다` · 금액 $25 · **남은 크레딧이 곧바로 $120**(활동 보상 $20 은 만들자마자 들어왔다 — 사용자가 화면에서 확인)
  - ⬜ 아직 안 본 것 — 알림이 울리는 조건(템플릿 기본값 · 화면 미확인) · **예산이 크레딧으로 낸 지출도 세는지**(문서에 기본값이 없다) → EC2 를 켠 다음 날 예산의 실제 지출이 $0 보다 커졌는지로 확인한다. 세지 않으면 "사용자 지정"으로 고친다. 안전망: AWS 가 크레딧 50% · 25% · 10% 남았을 때 메일을 보낸다
  - 예산은 매달 1일에 합계가 0 으로 돌아간다 — 크레딧 **총 잔액**이 아니라 **이번 달 속도**를 본다(총 잔액은 콘솔 홈 "비용 및 사용량" 위젯). 예산은 지출을 막지 않고 알려 주기만 한다
- **4단계 ① IAM 사용자에서 측정한 것** (2026-11-24 · 요금 없음)
  - **만든 것** — IAM → 사용자 → 사용자 생성: `newbeez-admin`(11:45 생성) · 콘솔 액세스 켬 · 권한 `AdministratorAccess`(AWS 관리형 - 직무 · 연결 방식 "직접") · 액세스 키 없음(**만들지 않는다**) · Identity Center 는 고르지 않았다. 사용자 요약에 **"로그인 인증 유지 기간"**(12시간 · 1일 · 7일 · 30일 · 90일 중 선택)이 있다 → 12시간. 이것은 IAM 의 사용자별 설정이다 — Claude 가 "콘솔 세션은 12시간 고정"이라고 한 것은 옛 기준이라 틀렸다
  - **MFA 는 사용자마다 2개** — 루트 · `newbeez-admin` 둘 다 패스키(노트북 지문) + 휴대폰 인증 앱. 패스키 하나뿐이면 그 노트북 밖에서는 로그인이 막힐 수 있다(패스키는 만든 기기에 저장된다) · 사용자당 최대 8개 · 로그인 때는 그중 하나만 쓴다. 요약의 콘솔 액세스가 `MFA 없이 활성화됨` → `MFA로 활성화됨`
  - **로그인** — 콘솔 로그인 URL(계정 번호가 들어 있다 — 문서에 적지 않는다) + 사용자 이름 + 암호 + MFA · 오른쪽 위에 `newbeez-admin` 이 뜬다 · **리전은 사용자마다 따로 기억된다** — 첫 로그인은 다시 기본값이라 서울로 바꿨다
  - **결제 정보는 IAM 사용자에게 기본으로 안 보인다**(관리자 권한이어도) — "비용 및 사용량" 위젯의 남은 크레딧 $120 · 남은 일수 173일은 보이고 이번 달 · 비용 세부 내역 · 예상 월말은 `액세스 거부됨` → **루트로** 계정 이름 → "계정" → "결제 정보에 대한 IAM 사용자 및 역할 액세스" → 편집 → "IAM 액세스 활성화" → 업데이트 → `newbeez-admin` 으로 다시 로그인하니 `데이터 사용 불가`(쓴 돈이 없어서 · 정상)
  - **"AWS 살펴보기"는 IAM 사용자에게도 보인다** — 완료 1/6 · US$20 / US$100 → EC2 활동은 `newbeez-admin` 으로 시작한다
  - 헷갈린 곳 — "계정이 루트와 IAM 2개인가" → **계정은 하나, 그 안의 사용자가 둘**(크레딧 · 예산 · 서버는 계정에 속한다 · 약관의 "계정 둘 이상"과 무관) · 관리자 권한을 줘도 루트 전용 작업(계정 삭제 · 결제 수단 · 요금제 변경)은 못 하고, 문제가 생기면 루트로 지우면 된다
- **4단계 ② 연습용 인스턴스(EC2 활동)에서 측정한 것** (2026-11-24 · `newbeez-admin` · 서울)
  - **왜 연습용을 따로** — "AWS 살펴보기" → "EC2를 사용하여 인스턴스 실행" → "활동 시작"을 누르면 EC2 "인스턴스 시작" 화면 오른쪽에 "지침" 창이 뜬다: Task 1 테스트 인스턴스 시작(8단계: 이름 · AMI · 인스턴스 유형 · 새 키 페어 · 보안 그룹 규칙 편집 · 보안 그룹 설정 · 스토리지 · 검토 및 시작) · Task 2 시작 후 설정(1단계) · **Task 3 인스턴스 종료(삭제)**(2단계). 마지막이 삭제라 이 인스턴스를 운영 서버로 쓸 수 없다 → **활동은 연습용으로 끝까지 따라가고, 진짜 서버는 따로 만든다**(운영 서버는 Arm · `t4g.small` · 20GB · 접속 규칙이 활동 기본값과 다르다)
  - **결과**(사용자가 화면에서 읽어 줌 · 캡처는 없다) — 활동 완료 · 인스턴스 목록에 연습용이 **2개** 남아 있었고 둘 다 "종료됨"(종료한 인스턴스는 목록에 약 1시간 남는다 · 요금 없음) · EC2 "볼륨" 목록 비어 있음(디스크도 함께 지워졌다) · "AWS 살펴보기" 완료 2/6 · 획득 US$40 / US$100
  - ⬜ 안 본 것 — 2개가 된 이유("인스턴스 시작"이 두 번 실행된 것으로 추정) · 연습 때 실제로 넣은 값(Claude 가 권한 값: 이름 `newbeez-test` · Amazon Linux 2023 x86 · 프리 티어 기본 유형 · 키 페어 `newbeez-key`) · 남은 크레딧 잔액(획득 $40 이니 약 $140 일 것) · 연습 때 만든 키 페어와 보안 그룹(`launch-wizard-…`)이 AWS 에 남아 있는지(둘 다 요금 없음) · 보상이 종료 작업까지 해야 들어오는지(둘 다 종료한 뒤에 봐서 가르지 못했다). **이 PC 의 다운로드 · 바탕 화면 · `~/.ssh` 에 `.pem` 파일이 없다**(파일 이름만 찾아봄) → 진짜 서버용 키 페어는 새로 만든다
  - 헷갈린 곳 — "EC2 는 실제로 쓸 건데 왜 연습용인가" → 위 이유 · "중지"(전원만 끔 · 디스크 요금은 계속)와 "종료"(삭제)는 다르다 · 요금은 목록에 보이는지가 아니라 **인스턴스 상태**로 정해진다
  - **진짜 서버에 넣을 값**(Claude 가 권함 → 2026-11-24 이 값으로 만들었다 · 결과는 아래 묶음) — EC2 → 인스턴스 → "인스턴스 시작"(안내 창 없음): 이름 `newbeez-server` · Amazon Linux 2023 · 아키텍처 **64비트(Arm)**(먼저 바꿔야 유형 목록에 `t4g` 가 나온다) · 유형 `t4g.small` · 새 키 페어 `newbeez-server-key`(RSA · `.pem` — 한 번만 내려받는다 · 내용은 열지도 캡처하지도 않는다 · 커밋 금지) · 네트워크 설정 "편집" → 보안 그룹 이름 `newbeez-server-sg` · 설명 `newbeez server`(영문만) · SSH 규칙 소스 유형 **"내 IP"**(80 · 443 은 ⑤에서 연다) · 스토리지 8 → **20** GiB gp3 · 고급 세부 정보 → 크레딧 사양 **"표준"** · 종료 방지 "활성화". 시작 전 "요약"에서 인스턴스 개수 1 · `t4g.small` · 20GiB 확인 · "인스턴스 시작"은 한 번만
  - ⚠️ 만든 뒤 알아 둘 것 — 켜 둔 동안 하루 약 $0.69(월 $20.66) · "내 IP"는 집 IP 가 바뀌거나 다른 PC 에서 붙으면 SSH 가 시간 초과된다 → 보안 그룹 규칙의 IP 를 고친다 · 키 파일은 PC 를 넘어가지 않는다(다른 PC 에서 붙는 방법은 접속 단계에서 정한다) · 공인 IP 는 인스턴스를 중지했다 켜면 바뀐다(⑤ 도메인 연결 때 고정 IP 를 정한다)
- **4단계 ② 진짜 서버 `newbeez-server` 에서 측정한 것** (2026-11-24 · `newbeez-admin` · 서울 · ⚠️ **14:46 부터 켜져 있다 — 크레딧이 빠진다**)
  - **만든 값 확인**(인스턴스 화면의 요약 · "세부 정보" · "보안" · "스토리지" 탭 캡처 4장) — 상태 "실행 중" · 유형 `t4g.small` · AMI `al2023-ami-2023.12.20261120.0-kernel-6.18-arm64` · 시작 2026-11-24 14:46:10 KST · 키 페어 `newbeez-server-key` · **크레딧 사양 `standard`** · 종료 방지 "활성"(중지 방지는 비활성) · 보안 그룹 `newbeez-server-sg`: 인바운드 TCP 22 하나, 원본은 만든 곳의 공인 IP `/32` · 아웃바운드 전체 · 루트 볼륨 `/dev/xvda` 20GiB "사용 중" · 퍼블릭 IPv4 자동 할당(탄력적 IP 없음 — 중지했다 켜면 바뀐다) · IAM 역할 없음 · IMDSv2 Required · 부트 모드 uefi
  - **문서에 적지 않는 것** — 인스턴스 ID · 퍼블릭 IP · 접속을 허용한 집 IP · 계정 번호(캡처의 "소유자" · "소유자 ID" · 인스턴스 ARN 에 12자리가 그대로 나온다 — 캡처를 밖에 올리지 않는다). 접속 주소는 콘솔의 인스턴스 화면에서 본다
  - **AMI 목록에서 고른 것** — "Amazon Linux" 아래에 `Amazon Linux 2023 kernel-6.18 AMI` · `kernel-6.1 AMI` · Deep Learning(CUDA) 등이 함께 나온다 → `kernel-6.18`(프리 티어 사용 가능). 두 커널 판의 지원 기간은 확인하지 않았다 · AMI 를 바꾸면 아키텍처가 x86 으로 돌아갈 수 있어 다시 본다
  - **왜 이 값인가**(사용자가 물은 것) — **아키텍처**: 내 PC(Windows · x86)와 서버 CPU 는 같을 필요가 없다. 맞아야 하는 것은 서버 CPU 와 이미지(`newbeez-back:arm64`)다 · Arm 을 고른 이유는 가격(`t3.small` 보다 약 20% 싸다 — `t3` 가격은 가격표에서 뽑지 않은 Claude 기억값) · **OS**: Amazon Linux 와 Ubuntu 둘 다 표준이다 — 앱과 DB 가 컨테이너 안에서 돌아 차이가 작다(`Dockerfile` 의 `eclipse-temurin:21-jre` 는 Ubuntu 기반이라 컨테이너 안은 이미 Ubuntu · 컨테이너는 서버의 커널을 함께 쓴다). AL2023 을 고른 이유: AWS 관리형 서비스의 기본 OS · RHEL 계열과 같은 `dnf` · 기본 설치가 적다(메모리 사용량은 재지 않았다) · 포트폴리오에서는 OS 이름보다 "왜 이 구성인가"(가격 계산 · HTTPS · 백업)를 본다
  - 설명이 통한 방식 — EC2 = 서비스 이름 · 인스턴스 = 그 서비스로 빌린 컴퓨터 한 대 · AMI:인스턴스 = Docker 이미지:컨테이너 · 아키텍처(CPU 종류) · 인스턴스 유형(크기) · OS 를 "내 PC 로 치면" 열과 함께 표로 · 사용자가 자기 말로 다시 정리해 물으면 "맞다 / 이것만 덧붙인다"로 답한다
  - **키 파일 `newbeez-server-key.pem` 은 노트북의 다운로드 폴더에 있다**(사용자 확인 2026-11-24 · AWS 콘솔을 노트북에서 조작했다 — `jun98` PC 에는 `.pem` 이 없다) → ⬜ SSH 접속을 어느 PC 에서 할지 정한다(키가 있는 노트북에서 하는 것이 가장 단순 · 보안 그룹의 "내 IP"도 콘솔을 연 곳 기준이다). 키 파일은 한 번만 내려받을 수 있다(잃으면 새 키를 서버에 넣는 절차가 따로 필요) · 커밋 금지 · 메신저 · 메일 · 클라우드 폴더로 옮기지 않는다 · 내용을 열거나 캡처하지 않는다
  - **캡처 정리**(2026-11-24 사용자 요청) — 콘솔 캡처에는 계정 번호 · 서버 주소 · 집 IP 가 찍힌다 → Claude 가 읽고 나면 지운다(다운로드 · 바탕 화면 · **휴지통** · 바탕 화면이 OneDrive 폴더인 PC 는 OneDrive 휴지통까지). 점검 결과: 이 저장소의 추적 파일 · 오늘 커밋 · Claude 메모리에는 그 값이 없다(패턴 검색 0건). 비밀번호 · MFA 비밀 값 · 키 파일 내용 · 액세스 키는 캡처에도 대화에도 나온 적이 없다
  - ⬜ 다음 날 볼 것 — 예산 `newbeez-monthly-cost` 의 실제 지출이 $0 보다 커졌는지(크레딧으로 낸 지출도 세는지) · 청구서에 T4g 무료 체험이 적용됐는지 · 남은 크레딧 잔액
- **운영 형태 — EC2 한 대에 앱 + MySQL 컨테이너 · RDS 는 1주 실습** (2026-11-24 사용자와 검토 · 옛 계획 "EC2 + RDS 상시"를 바꿈)
  - **왜** — MVP 는 지인에게 링크로 보내는 규모(사용자 극소 · 1회성)이고 목적은 학습이다. 요금은 사용자 수가 아니라 **켜 둔 시간**에 붙고, 무료 플랜은 2027-05-14 에 끝나 남은 크레딧은 사실상 못 쓴다(유료로 전환해야 쓴다) → 아끼지 않되 **5/14 전에 바닥나지 않게** 쓴다. 배움은 만들고 옮기고 지우는 데서 나온다
  - **서울 리전 공식 가격**(AWS 가격 목록 2026-11-20 게시분 — `pricing.us-east-1.amazonaws.com/offers/v1.0/aws/{AmazonEC2,AmazonRDS}/current/ap-northeast-2/index.csv` 와 EC2 가격 페이지 데이터에서 직접 뽑음): `t4g.small` $0.0208/시간(2 vCPU · 2 GiB) · `t4g.micro` $0.0104(1 GiB) · 공인 IPv4 $0.005/시간(전 리전 · 쓰든 안 쓰든) · EBS gp3 $0.0912/GB·월 · RDS `db.t4g.micro` MySQL Single-AZ $0.025/시간 · RDS gp3 $0.131/GB·월(최소 20GB)
  - **계산**(월 = 730시간) — EC2 한 대: 15.18 + 3.65 + 디스크 20GB 1.82 = **$20.66/월** · RDS 를 더하면 +$20.87 = $41.53/월 · EC2 한 대를 2026-11-24 ~ 2027-05-14(171일) 켜면 **약 $116** · 옛 계획은 12/15 부터 켜도 $205 라 크레딧 최대($200)로도 모자란다 · RDS 1주 실습 약 $5
  - **크레딧** — **지금 $120**(가입 $100 + 예산 활동 $20 · 2026-11-24) → 오늘부터 켜도 5/14 까지 간다(약 $116) · $140(EC2 활동까지)이면 약 $24 가 남아 RDS 실습에 쓴다 · $100 뿐이었다면 4월 20일경 바닥이었다
  - **맞는지 본 것** — `t4g.small` 은 무료 플랜에서 쓸 수 있는 인스턴스 목록에 있다(EC2 Free Tier 문서) · `db.t4g.micro` MySQL 도 무료 플랜 대상(RDS Free 페이지) · 메모리는 측정값 앱 358MiB + MySQL 445MiB ≈ 0.8GiB 라 2GiB 에 들어간다(`t4g.micro` 1GiB 는 모자란다) · `mysql:8.4` 공식 이미지에 arm64 가 있다(Docker Hub 2026-11-24)
  - **만들 때 넣을 것** — 크레딧 사양 **Standard**(T4g 기본값은 Unlimited — 24시간 평균 CPU 가 기준 20% 를 넘으면 vCPU·시간당 $0.04) · DB 데이터는 이름 있는 볼륨(예행 때는 `-v` 로 지우는 익명 볼륨이었다) · JVM 힙과 MySQL 메모리 한도 · 스왑 · `mysqldump` 주기 백업(`participant_logs` 는 다시 만들 수 없는 유일한 데이터)
  - ⚠️ **`t4g.small` 무료 체험(2026-12-31 까지 월 750시간)은 계산에 넣지 않았다** — EC2 FAQ 는 "모든 고객 자동 등록", 결제 문서는 "무료 플랜 계정은 상시 무료 혜택만 켜져 있고 단기 체험은 유료 플랜"이라 서로 다르다. 적용되면 12월 서버 요금 약 $15 가 덤 → EC2 를 켜고 하루 이틀 뒤 청구 내역으로 확인한다
  - **계정 이력** — 이 계정은 2025-01-03 에 만들어 두기만 했고(옛 환영 메일 "향후 12개월 동안 Free Tier 한도 내에서 …") 2026-11-14 에 다시 로그인해 **무료 플랜을 선택·활성화**했다(메일 "무료 플랜 계정이 준비되었습니다 · 만기일 14 May 2027 · 청구 및 계정 지원을 위한 AWS Basic Support"). **계정은 하나뿐**이다 — 약관의 "계정을 둘 이상 만들면 크레딧 대상이 아니다"에 해당하지 않는다(약관: 대상이 아니라고 판정되면 정상 요금을 청구한다). 두 메일은 근거로 보관한다
  - **2027-04 에 할 일** — 참여 기록 내려받기(계정이 닫히면 접근하지 못한다 · AWS 는 90일 보관하고 그 안에 유료로 전환하면 되살아난다) · 5/14 이후(유료 전환 · 이전 · 종료) 결정
  - 프론트 설계 문서(FUTURE §4 · ARCHITECTURE §6·§7)에는 아직 반영하지 않았다 — 아래 "프론트에 넘길 것". **F7 팀 소개 위치(A 프론트 `lib/` 추천)는 여전히 사용자 결정 대기**
- Windows 의 Docker = Docker Desktop 4.51.0 + WSL 2.6.1(이미 설치돼 있었다 · 켜기만) — 컨테이너는 Linux 프로그램이라 Windows 에선 WSL 이 Linux 를 제공한다(`docker version` 의 server 가 `linux/amd64`). **AWS 서버는 Linux 라 Docker Engine 만 깐다**
- 설명이 통한 방식 — "그 컴퓨터에 미리 있어야 하는 것"이 줄어드는 순서(소스 + Gradle → jar + Java → 이미지 + Docker) · 이미지 : 컨테이너 = jar : `java` 프로세스 · 파일 내용(IntelliJ)과 명령(터미널)을 **표로 갈라서** 준다 — 한 답에 섞어 주니 "Docker Desktop 에 입력하느냐"고 물었다 · IntelliJ 의 New → File 을 못 찾아 `New-Item <이름> -ItemType File` 로 빈 파일을 만들고 열어서 쳤다 · **DB 주소는 "앱이 있는 자리에서 본 DB 의 위치"** 를 표(앱 위치 × DB 위치 → 주소)로 — "MySQL 은 컨테이너 아니었나"를 물었다(예행용 컨테이너를 지운 것과 지금 붙는 DB 가 헷갈렸다) · 예행용 MySQL 컨테이너는 만들려는 것이 아니라 **RDS 를 흉내 낸 일회용 측정 도구** · 실행마다 "바꾼 것 하나 · 확인한 질문 하나"를 표로

---

## ✅ 끝난 슬라이스 — 결론만

> 계약·스키마의 원본은 프론트 ARCHITECTURE(§5·§6), 구현의 원본은 코드다. 여기에는 **코드와 설계 문서만 봐서는 모르는 이유·규칙**과 **다른 절이 가리키는 것**(F6-1 PC별 표·절차 · F6-2 비교용 SQL)만 둔다. 측정과 과정은 각 PR 의 커밋 본문에, 줄이기 전 전문은 `git show 8adc89f:HANDOFF.md` 에 있다(2026-11-09 압축 · PR 은 지금처럼 merge 커밋으로 — squash 하면 이 해시가 사라진다). F6-5 절의 줄이기 전 전문(진행 표 · 측정 · 3단계 메모)은 `git show 0b28efa:HANDOFF.md`(2026-11-20 압축).

### ✅ F6.5 Flyway 전환 — 2026-11-22 (PR #9 · `4d10883`)

- **테이블의 원본은 `src/main/resources/db/migration/V1__participant_logs.sql`, 엔티티는 따라가는 쪽.** `application.yml` 의 `ddl-auto: validate` 는 **글자로 고정**(환경변수 `${…}` 없음) — 어느 PC 에 옛 `NEWBEEZ_DDL_AUTO=update` 가 남아 있어도 Hibernate 가 Flyway 몰래 테이블을 못 만진다. `validate` 는 칸 이름·타입만 보고 **인덱스·VARCHAR 길이·칸 순서는 안 본다** → 엔티티의 `@Index`·`length` 는 설명용이지만 V1 과 같은 모양으로 둔다(코드가 거짓말하지 않게)
- **적용된 `V` 파일은 고치지 않는다 — 바꾸려면 `V2`.** 체크섬 = 파일을 줄 단위로 읽어 각 줄의 UTF-8 바이트를 CRC32 에 넣은 값(`flyway-core` 12.4.0 `ChecksumCalculator` javap · 장부 값 668339657 을 Python 으로 재현). **보는 것**: 글자 · 주석 · 줄 끝 공백. **안 보는 것**: 빈 줄 · CRLF/LF · 파일 끝 줄바꿈 → Windows/Linux 체크섬이 같다(데스크톱 2026-11-23: 작업 파일이 CRLF(`autocrlf=true`)인데 장부 값은 노트북과 같은 668339657). 실측(2026-11-22): 주석 한 줄 추가 → 기동 실패 `Validate failed … Applied to database 668339657 / Resolved locally -285027153` · DB 는 그대로(운영이면 "새 버전이 안 뜨고 옛 버전이 계속 돈다"). `repair`(장부 체크섬을 지금 파일 값으로 덮어쓰기)는 주석 오타 정도에만
- **기동 순서** Hikari → Flyway(validate → migrate) → Hibernate validate → Tomcat. Boot 가 `entityManagerFactory` 를 `flywayInitializer` 에 의존시킨다(실패 메시지 첫 줄에 그대로 보인다). 매 기동 Flyway 로그 4줄(`Database: … (MySQL 8.0)` · `Successfully validated 1 migration` · `Current version of schema: 1` · `up to date. No migration necessary`) · **Hibernate validate 는 통과하면 무음** · Flyway 의 "validated" 는 테이블이 아니라 **파일 ↔ 장부** 대조라 표를 만들기 전에도 통과한다
- 의존성 `implementation spring-boot-starter-flyway`(→ `spring-boot-flyway` 4.1.0 + `flyway-core` 12.4.0 + `starter-jdbc`) + `runtimeOnly org.flywaydb:flyway-mysql`(Flyway 10 부터 DB 별 모듈 분리 · 없으면 `No database found to handle jdbc:mysql`). `application.yml` 에 Flyway 키 없음 — jar 가 있으면 자동 설정이 켠다(`db/migration` 기본 위치). MySQL 8.0.46 에 경고 없음
- `flyway_schema_history` 1행 — `installed_on` 은 **세션 시간대(KST)** 라 `created_at` 의 UTC 와 다르다(D1 의 JDBC UTC 옵션을 넣으면 같아진다) · `installed_rank`(실행 순번) ≠ `version`(파일 이름) · `description` 은 파일 이름의 밑줄을 공백으로 · 실패하면 `success=0` 행이 남아 다음 기동을 막는다
- **로컬 전환(옛 표 `DROP` → `bootRun`)은 두 PC 모두 끝** — 옛 표를 둔 채 켜면 어떻게 멈추는지는 위 "다른 PC" 절 1번. **첫 적용 로그**(데스크톱 2026-11-23 · D1 운영 DB 첫 기동도 이 모양이어야 한다): `Schema history table … does not exist yet` → `Successfully validated 1 migration` → `Creating Schema History table` → `Current version of schema: << Empty Schema >>` → `Migrating schema … to version "1 - participant logs"` → `Successfully applied 1 migration …, now at version v1`. 테스트도 `@SpringBootTest` 마다 Flyway 가 돈다(앱 3개 → `up to date` 3번) · 16건 통과 · 테스트 롤백은 장부에 영향 없음
- `V1` 에 2026-11-09 결정 반영 — 인덱스 `idx_cat_created (category_slug, created_at)` · 생성자 `Instant.now().truncatedTo(ChronoUnit.SECONDS)`(`import java.time.temporal.ChronoUnit`) · `created_at DATETIME`(= `(0)` · `timestamp` ✗) · 칸 순서는 설계 순서(Hibernate 의 알파벳순 아님) · `ENGINE`·`CHARSET` 은 안 적고 서버 기본값(8.0·8.4 모두 InnoDB·utf8mb4). 전환 전 HANDOFF(함정·V1 계획 원문)는 `git show c53dc1b:HANDOFF.md`
- **설명이 통한 방식** — `SHOW CREATE TABLE` 로 "지금 상태"를 먼저 보고(과거 명령이 아니라 현재 구조를 MySQL 이 다시 쓴 것) · "Hibernate 가 추측한다"는 "엔티티에 없는 것(타입 변환·이름 규칙·칸 순서·`ENGINE`)을 규칙으로 채운다"로 바꿔 표로 대조 · 체크섬은 **실행 전에 숫자를 예측**(Python CRC32)하고 오류 문구의 두 숫자로 확인 · 빈 줄을 넣었는데 통과한 것이 "체크섬이 안 보는 것"을 가르친 지점

### ✅ F6-5 CORS — 2026-11-20 (PR #8 · `a4185c9`)

- **`CorsConfig` 는 제한이 아니라 여는 코드다 — 줄을 지우면 닫히지 않고 더 열린다.** `registry.addMapping(경로)` 가 열고(기본값 `applyPermitDefaultValues`: 출처 `*` · 메서드 `GET`·`HEAD`·`POST` · 헤더 `*` · `maxAge` 1800초) `.allowedOrigins`·`.allowedMethods`·`.allowedHeaders` 가 좁힌다. 빼도 컴파일·기동은 멀쩡하다 → 테스트가 헤더의 **값**을 본다(빨간불 2026-11-20: POST 규칙의 `.allowedOrigins` 를 지우면 ⑮ ⑯ 이 `expected: "http://localhost:3000" but was: "*"` · 상태는 200 · 201 그대로)
- **허용 출처는 환경변수 `NEWBEEZ_CORS_ALLOWED_ORIGINS`**(쉼표로 여러 개 · 조각 앞뒤 공백은 Boot 변환기가 자른다) → `application.yml` 의 `newbeez.cors.allowed-origins` · **기본값 없음** — 줄이 없으면 기동 실패. CORS 가 빠지면 화면은 멀쩡하고 기록만 조용히 사라지기 때문이다(§5.2 "기록하지 않은 답은 되살릴 수 없다"). 운영 프론트 주소는 D1 에서 환경변수에 **추가만** 한다(코드 변경 없음). `*` ✗ · `https://*.vercel.app` 패턴 ✗(남의 Vercel 사이트까지 열린다)
  - ⚠️ **줄은 있는데 값이 비면**(`=`) 기동은 되고 Origin 이 붙은 요청만 전부 403 이다 — Origin 없는 헬스 체크는 200 이라 서버 쪽에선 멀쩡해 보인다. 생성자 검사 코드는 넣지 않는다(2026-11-05 사용자 결정) → 배포 확인 때 ⑬ 모양 요청으로 본다(맨 아래 "그 외")
  - 출처 비교(`CorsConfiguration.checkOrigin`) — 끝 `/` · 대소문자는 무시 · **호스트 이름**(`localhost` ≠ `127.0.0.1`) · 포트 · 프로토콜이 다르면 거절
- **경로는 두 개를 정확히, 각자 쓰는 것만** — `/api/participants` 는 `POST` + `Content-Type` · `/api/participants/count` 는 `GET`. `/api/**` ✗ — F7 `/results` 는 Next 서버가 빌드 때 불러 CORS 가 필요 없다(원본은 프론트 ARCHITECTURE §6 표). 쿠키·로그인이 없어 `allowCredentials` 는 기본값(꺼짐)
- **Spring 의 거절 동작**(Framework 7.0.8 jar 확인) — 설정이 있는 경로에서 목록 밖 **출처·메서드**는 403 `Invalid CORS request`(컨트롤러까지 가지 않는다). ⚠️ **헤더는 다르다**: 사전 요청이 묻는 헤더 중 허락된 것만 골라 돌려주고 하나도 없을 때만 403 → **서버 200 ≠ 브라우저 허락**(빠진 헤더를 보고 막는 쪽은 브라우저). 헤더 이름은 대소문자 무시 · 응답에는 요청이 보낸 철자 그대로
- **`Origin` 헤더가 없는 요청은 CORS 요청이 아니라서 그대로 통과**한다 — `.http`·curl·Next 서버(F7)는 영향 없음. **CORS 는 남용 방어가 아니다**(브라우저 안에서만 동작한다)
- `Allow-Methods` · `Allow-Headers` · `Max-Age` 는 **사전 요청 응답에만** 붙는다. 진짜 POST 응답에는 `Allow-Origin` 만 — 이것이 없으면 201 이어도 브라우저는 실패로 본다(행은 저장되는데 프론트는 에러)
- ⚠️ **`@EnableWebMvc` 를 붙이지 않는다** — `WebMvcConfigurer` 만 구현한다. 붙이면 Boot 의 MVC 자동 설정 전체가 꺼진다. Boot 에는 MVC CORS 설정 키가 없어 Java 설정(`addCorsMappings`)으로 한다

**테스트 범위** (2026-11-20 사용자와 줄였다 — "테스트가 왜 이렇게 많나 · 실제 개발은 언제")
- 자동 테스트는 `CorsConfigTest` **4건**(`.http` ⑬~⑯) — 허용 출처 200 + Allow-Origin · 다른 출처 403 · 사전 요청 허락 헤더 3개 · 진짜 POST 201 + Allow-Origin. 기준: **우리가 `CorsConfig` 에 쓴 설정만 고정하고 Spring 이 해 주는 일은 테스트하지 않는다** — `Max-Age` 1800 은 Spring 기본값이라 검사하지 않는다(값을 직접 정하게 되면 그때 `.maxAge(…)` 와 검사를 같이 넣는다)
- 메서드·헤더 거절(`.http` ⑰~⑲)은 **손 확인만** — 설정을 하나씩 바꿔 본 결과(spring-web 7.0.8 `CorsConfiguration`) 4건이 못 잡는 것은 **더 열리는 쪽** 둘(`allowedMethods("*")` · `allowedHeaders` 줄 삭제)이고 지금은 해가 없다(DELETE 핸들러 없음 → 405 · 모르는 헤더는 무시 · 쿠키·인증 없음). ⚠️ **로그인·인증·쓰기 메서드가 늘면 ⑰~⑲ 를 자동 테스트로 넣는다**
- 허용 출처는 `@SpringBootTest(properties = "newbeez.cors.allowed-origins=" + CorsConfigTest.ALLOWED)` 로 **테스트가 정한다**(PC 마다 다른 `.env` 에 기대지 않는다 · 덮어쓰기 증명은 `37e0d71` 본문). 배포 때 환경변수 값이 틀리는 것은 테스트로 못 잡는다 → D1 확인(맨 아래 "그 외")

**설명이 통한 방식** (2026-11-19~20 · 다음 슬라이스에서도 쓴다) — 파일을 **3층**(준비 = 클래스 위 애너테이션 · 재료 = 상수와 `@Autowired` 필드 · 시나리오 = `@Test` 메서드)으로 나눠 조각마다 치고 확인 · 테스트 하나 = **"무엇을 보내고 → 무엇을 확인하나"** 두 덩어리 · `.http` 손 동작에 대응(`bootRun` = `@SpringBootTest` · ▶ 한 번 = `@Test` 하나 · 눈으로 확인 = `assertThat`) · 두 번째부터는 앞 테스트를 복사해 바꿀 곳만 짚는다 · **실행 전에 예상을 적게 하고, 결과가 다른 지점을 설명한다**. 사용자가 헷갈린 곳: "테스트에 `CorsConfig` 가 없는데 어떻게 아나"(→ `@SpringBootTest` 가 앱 전체를 켠다) · "테스트는 다른 환경 아닌가"(→ 포트와 덮어쓴 값만 다르고 **DB 는 같다**) · 요청 `Origin` vs 응답 `Access-Control-Allow-Origin` · `Request-Method`(단수) vs `Allow-Methods`(복수) · 번호(⑬ …)만 부르면 모른다 → **메서드 이름과 함께** 부른다

### ✅ F6-4 참여자 수 `GET /api/participants/count` — 2026-11-03 (PR #7 · `009796a`)

- **캐시 설정 세 가지는 조용한 실패를 드러내려고 둔 것이다 — 지우지 않는다.** `spring.cache.type: caffeine`(Caffeine 이 빠지면 오류 없이 TTL 없는 기본 캐시로 바뀌는 것을 **기동 실패**로) · `cache-names: participantCount`(`@Cacheable` 이름 오타가 **첫 요청에서** `Cannot find cache named` 로 드러난다 · 기동은 된다) · `@EnableCaching`(없으면 기동도 요청도 멀쩡한 채 `@Cacheable` 만 무시된다 — **캐시 테스트로만** 드러난다)
- `expireAfterWrite` — `expireAfterAccess` 는 계속 읽히면 영영 갱신되지 않는다 · `sync = true` — 만료 순간 동시 요청의 `COUNT` 를 하나로 줄인다 · 10초 만료 자체는 테스트하지 않는다(`sleep` 은 느리고 흔들린다 · `.http` 로 손으로 쟀다)
- ⚠️ **허용 목록 `"football"` 이 두 곳에 있다** — `ParticipantLogRequest`(POST) 와 Controller `count` 의 `@Pattern`. 카테고리를 더할 때 같이 고친다
- 쿼리 값 검사는 **매개변수에 `@Pattern` 을 직접** 붙인다(Spring 내장 메서드 검사 → 400) · 클래스 수준 `@Validated` ✗(다른 검사 경로가 켜진다) · `@RequestParam` 은 Java 매개변수 이름으로 찾는다 — **매개변수 이름이 곧 API 계약**
- **서버 로그 없는 400 = 허용 목록일 가능성** — `categorySlug` 누락은 WARN 이 찍히지만 `baseball`·빈 값은 아무것도 안 찍힌다(`HandlerMethodValidationException` 을 로그 없는 리졸버가 처리 · 7.0.8 javap)

**선택 과제 — Redis 캐시 실습** (2026-11-03 사용자 요청 · 학습용 · 실습 브랜치에서 · `main` 은 Caffeine 유지) — `@Cacheable` 은 그대로 두고 저장소만 바꾼다: `spring-boot-starter-data-redis` · `spring.cache.type: redis` · `spring.cache.redis.time-to-live: 10s` · 접속 주소는 DB 처럼 환경변수로(키는 그때 4.1.0 jar 로 확인) · `docker run -d --name redis -p 6379:6379 redis`. 같은 `.http` 측정에 더해 Redis 를 끄면 어떻게 되는지 본다 — Spring 캐시 기본 오류 처리는 예외를 그대로 던져 API 가 500 이 된다. 캐시 테스트는 고치지 않고 통과해야 한다(테스트도 Redis 가 떠 있어야 돈다). MVP 가 Caffeine 인 이유는 서버가 한 대라 Redis 의 이점(여러 대가 같은 값을 공유)이 없어서다

### ✅ F6-3 `POST /api/participants` — 2026-10-29 (PR #3·#4·#5 · `8e640af`)

- ⚠️ **`answerCode` 에는 `@Pattern` 을 걸지 않는다** — 걸면 요청 전체가 400 이 되어 "형식이 틀려도 참여 기록은 남긴다"(ARCHITECTURE §5.2)를 어긴다. Service 가 범용 형식(숫자 1~32자)만 보고 틀리면 `null` 로 저장한다. 축구 9자리 규칙은 Java 에 복사하지 않는다(원본 `lib/answer-code.ts`)

**테스트 방식** (이후 슬라이스도 같다 · 예시는 `ParticipantLogControllerTest`)
- `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester` + `@Transactional` — 실제 로컬 MySQL 에 붙고 테스트마다 롤백(테스트 전용 DB 는 MVP 에 과하다). 테스트 메서드 하나 = `http/participants.http` 요청 하나
- **기존 행에 기대지 않는다** — 로컬 DB 마다 행이 다르다. 행 수는 전후 차이로, 방금 저장한 행은 id 가 가장 큰 행으로 본다
- **빨간불 확인** — 통과한 테스트가 정말 잡는지, 지키는 코드를 잠깐 막아 실패를 본 뒤 되돌린다

### ✅ F6-2 `participant_logs` 엔티티 · Repository — 2026-10-27 (PR #2 · `5559dd6`)

- **`protected` 빈 생성자** — Hibernate 가 행을 객체로 복원할 때 쓴다. 프록시가 자식 클래스라 `private` 은 안 된다
- **Lombok 은 이 엔티티에 쓰지 않는다** — 무엇을 줄여 주는지 한 번은 직접 써 봐야 안다(학습 결정 · `build.gradle` 에 들어 있어도). 도입은 F7 `team_results`

**테이블의 원본은 F6.5 부터 `src/main/resources/db/migration/V1__participant_logs.sql` 이다** — Hibernate 가 `update` 로 만들던 시절의 SQL(알파벳순 칸 · `idx_created`)은 `git show c53dc1b:HANDOFF.md` 에 있다. 엔티티의 `@Index` 는 V1 과 같은 모양으로 유지한다(위 F6.5 절)

### ✅ F6-1 DB 설정 — 2026-10-06 (`44e2283`)

**접속 정보는 레포 밖에 있다.** `application.yml` 에는 빈칸 3개(`${NEWBEEZ_DB_URL}` · `_USERNAME` · `_PASSWORD`)만 있고, 로컬 값은 프로젝트 루트 **`.env`**(git 무시)에서 `spring.config.import` 로 읽는다. 배포에서는 `.env` 없이 같은 이름의 환경변수를 준다(환경변수가 우선).

**PC별 준비 상태** — DB·계정·`.env` 는 git 으로 오가지 않는다. PC마다 한 번씩 만든다.

| | `jun98` (데스크톱) | `이현준` |
| --- | --- | --- |
| MySQL | ✅ 8.0.46 (`MySQL80`) | ✅ 8.0.46 (`MySQL80` · 2026-11-02 실행 중 확인) |
| DB `newbeez` | ✅ | ✅ (2026-09-04 기록) |
| 앱 전용 계정 `newbeez` | ✅ | ✅ (2026-10-20 `bootRun` 접속 성공) |
| `.env` | ✅ | ✅ (2026-10-20 · 2026-11-02 있음 확인) |
| `.env` 의 `NEWBEEZ_CORS_ALLOWED_ORIGINS=http://localhost:3000` (**F6-5 2-3 부터 필수** — 없으면 기동 실패 · 비어 있으면 켜지지만 브라우저 요청 전부 403) | ✅ (2026-11-05) | ✅ (2026-11-20) |
| `.env` 에서 `NEWBEEZ_DDL_AUTO` 줄 **삭제** (F6.5 · 남아 있어도 무시되지만 치운다) | ✅ (2026-11-23) | ✅ (2026-11-22) |
| 로컬 `participant_logs` 를 Flyway 가 만든 것으로 교체 — 행 수 적고 `DROP` → `bootRun` (F6.5 · 위 "다른 PC" 절 1번) | ✅ (2026-11-23 · `.http` 손 요청 11행 버림 · 전환 뒤 `{"count":0}`) | ✅ (2026-11-22 · 행 0개였음) |
| `GRADLE_USER_HOME=C:\dev\gradle-home` 사용자 환경변수 (**한글 사용자명 PC 만** · 아래 함정 모음) | 불필요 (`jun98` 은 ASCII) | ✅ (2026-11-22) |

**⬜ 인 PC에서 할 일**

1. MySQL 에 root 로 접속해 실행 — 앱은 root 가 아니라 `newbeez` DB만 만질 수 있는 계정으로 붙는다
   ```sql
   CREATE DATABASE newbeez;   -- 이미 있으면 생략
   CREATE USER 'newbeez'@'localhost' IDENTIFIED BY '비밀번호';
   GRANT ALL PRIVILEGES ON newbeez.* TO 'newbeez'@'localhost';
   ```
2. `.env.example` 을 같은 폴더에 `.env` 로 복사하고 DB 값 3개를 채운다
   - `NEWBEEZ_CORS_ALLOWED_ORIGINS` 줄은 그대로 둔다(F6-5 2-3 부터 필수). **이미 `.env` 가 있는 PC 는 `.env.example` 과 줄을 맞춘다** — `.env` 는 git 으로 오가지 않아서 `.env.example` 이 바뀌어도 저절로 바뀌지 않는다(옛 `NEWBEEZ_DDL_AUTO` 줄은 지운다)
3. `./gradlew bootRun` → Flyway 의 `Migrating schema \`newbeez\` to version "1 - participant logs"` 와 `Started NewbeezBackApplication` 이 찍히면 성공. 확인은 `http/participants.http` 의 ⑦(참여자 수)

### 🧹 2026-09-04 레거시 폐기

백엔드가 채점하던 시절의 `docs/`·`data/`·코드를 전부 버리고 스캐폴드 커밋(`03f9357` · 2026-07-03)으로 되돌렸다(버린 파일은 git 기록에 없다) — 교훈은 [CLAUDE.md](CLAUDE.md) "여기에 설계를 복사해두지 말 것".

---

## ⬜ F7 `team_results` (배포·연동 뒤) — 설계 확정 (2026-10-20)

**⏸ F7 팀 소개 위치 재검토 중** (2026-11-09 · 사용자 결정 대기) — A 프론트 `lib/` 로 이동(추천 · 학습용 서버에 사이트 배포를 묶지 않는다) · B 백엔드 유지 + 빌드 때 안 부르기(배포 직후 백엔드가 죽어 있으면 첫 방문 500) · C 지금 설계. 호스팅은 AWS 무료 플랜으로 결정됐다(2026-11-14 · 맨 아래 "그 외") — 위치 결정만 남았다

**스키마·칸 설명·검증 규칙의 원본은 프론트 ARCHITECTURE §5.1** (여기에 복사하지 않는다). 백엔드가 알아야 할 것만:

- **읽기 전용 테이블.** 엔티티는 setter 없음 + `@org.hibernate.annotations.Immutable`(ORM 애노테이션 — "벤더 함수 금지" 규칙과 무관) · Repository 는 조회만. 배치·동기화 코드 없음 — 옛 "하이브리드(API 동기화 영역)" 설계는 폐기됐다
- **데이터는 Flyway 시드 파일이 진실** — `src/main/resources/db/migration/`: DDL 은 `V1__participant_logs.sql`·`V2__team_results.sql`, **15행은 `R__team_results_seed.sql`**(`DELETE … WHERE category_slug='football'; INSERT …`). DDL 과 DML 을 한 파일에 섞지 않는다(MySQL DDL 암묵적 커밋). 각 행 주석에 출처 URL·확인일. 벤더 구문(`ON DUPLICATE KEY UPDATE`) ✗
- Flyway 는 F6.5 에서 도입됐다(위 ✅ F6.5 절) — `V2__team_results.sql` 은 같은 폴더에 다음 번호로. MySQL 8.4 는 Flyway 문서 목록에 없지만 Redgate 가 지원 확인(2026-08) — D1 에서 1회 검증
- **`kit_post_url` 정규화·검증은 Java** — 쿼리스트링 제거 → `https://www.instagram.com/p/{code}/` → `@Pattern`(validation 은 F6-3 에서 이미 추가). 사진 게시물 여부는 등록 체크리스트
- **`GET /results` 응답 형태**는 ARCHITECTURE §6 에 적혀 있다 — `kit: { postUrl, seasonLabel } | null`. 프론트는 실패 시 던지므로(빌드 fail-fast) **백엔드가 먼저 떠 있어야 프론트 S8 이 배포된다**
- Lombok 은 여기서 도입(기존 결정 유지)
- 시즌 교체·삭제 요청 런북은 ARCHITECTURE §7.1 — 백엔드 쪽 할 일은 "시드 파일 수정 → 배포" 뿐

---

## 📌 함정 모음

### 설정 · 실행

- 🚨 **`'url' must start with "jdbc"` 로 죽으면 `.env` 가 없거나 변수 이름이 틀린 것.** Spring Boot 는 못 채운 `${...}` 를 오류 없이 **글자 그대로** 넘기기 때문에, "빈칸을 못 채웠다"가 아니라 이 엉뚱한 문구가 나온다
- ⚠️ **YAML 들여쓰기 실수는 조용히 무시된다.** `datasource:` 가 한 단계 깊이 들어가면 `spring.application.config.datasource.url` 같은 **Spring 이 모르는 이름**이 되고 오류도 안 난다. 증상은 "고쳤는데 오류 문구가 그대로"
- ⚠️ **`build` 도 DB를 요구한다** — `contextLoads` 와 Controller 테스트가 앱을 통째로 띄운다. 지금은 CI 가 없어 괜찮지만 배포용 빌드(D1)를 만들 때 다시 만난다
- 로그의 `The following 1 profile is active: "loc"` 는 `jun98` PC 의 사용자 환경변수 탓이다(다른 프로젝트용). 해롭지 않지만 **설정을 프로필에 기대면 안 되는 이유**다 → [CLAUDE.md](CLAUDE.md) 규칙
- ⚠️ **PowerShell 에서 `curl` 은 `Invoke-WebRequest` 의 별칭이다** — `curl.exe` 로 쳐야 한다. 게다가 **Windows PowerShell 5.1 은 `curl.exe` 같은 프로그램에 넘기는 인자에서 큰따옴표를 지운다** — `-d '{"categorySlug":"football"}'` 가 `{categorySlug:football}` 로 넘어가 400 이 된다(측정). **요청은 `http/participants.http`(IntelliJ HTTP Client · 요청 옆 ▶)로 보낸다.** Postman CLI 도 이 PC 에 있다
- ⚠️ **`.http` 를 "전체 실행"하면 전 → 행동 → 후 측정이 깨진다** — 값의 변화를 볼 땐 ▶ 를 하나씩. IntelliJ HTTP Client 는 `?categorySlug=` 를 `?categorySlug` 로 보낸다(로그 기준 · 400 은 같음)
- ⚠️ **IntelliJ HTTP Client 는 응답 헤더를 파일로 남기지 않는다** — `.idea/httpRequests/` 에는 본문(`<시각>.<상태>.json`·`.txt`)과 요청 기록(`http-requests-log.http`)만 있다(2026-11-03 확인). 헤더를 봐야 하는 측정(CORS)은 응답 창을 보거나, 같은 요청을 Bash 의 `curl -s -i` 로 보낸다
- ⚠️ **`Port 8080 was already in use` 는 이미 떠 있는 앱이 원인이다** — 2026-11-03: 11:19 에 IntelliJ ▶ 로 켠 newbeez 자신이 4시간 넘게 8080 을 쥐고 있었다. 누가 쓰는지는 PowerShell `Get-NetTCPConnection -LocalPort 8080 -State Listen` 의 `OwningProcess` 로 찾고, 프로세스 이름(`java.exe`)이 아니라 **실행 대상(main 클래스)**까지 본다. 실행은 한 방법(▶ 또는 `./gradlew bootRun`)만 쓰고, 측정 전에 `Started` 시각이 소스 저장보다 뒤인지 본다 — 옛 앱이 답하면 새 코드가 안 먹는 것처럼 보인다. 듣는 것이 없으면 빈 출력이 아니라 빨간 `ObjectNotFound` 오류가 나온다 — 그게 "없음"이다(2026-11-22)
- 🚨 **한글 사용자명 PC(노트북 `이현준`)에서 `./gradlew test` 가 테스트를 하나도 못 돌리고 `ClassNotFoundException: worker.org.gradle.process.internal.worker.GradleWorkerMain` 으로 죽는다** — Gradle 9.5.1 이 테스트 JVM 의 클래스패스를 `<GRADLE_USER_HOME>/.tmp/gradle-worker-classpath…txt` 인자 파일(`java @파일`)에 UTF-8 로 쓰는데 JDK 런처(Corretto 21 · OpenJDK 24 모두)는 `@파일` 을 시스템 코드페이지 CP949 로 읽어 `이현준` 이 깨진다(2026-11-22 · 같은 내용을 CP949 로 쓰면 성공하는 실험으로 확정). `bootRun`·IntelliJ ▶ 는 이 경로를 안 타서 멀쩡하다 → 사용자 환경변수 `GRADLE_USER_HOME=C:\dev\gradle-home` 으로 옮기고 **IntelliJ 재시작**(Settings → Build Tools → Gradle 의 Gradle user home 도 같은 값인지). 배포판·캐시를 다시 받는다(수백 MB). `jun98` 은 ASCII 라 해당 없음

### JPA · DB

- ⚠️ **`import` 는 보이는 대로 직접 친다** — 이름이 같은 다른 타입이 있어 IntelliJ 자동 가져오기가 엉뚱한 것을 잡는다. 우리가 쓰는 것은 `jakarta.persistence.Id`(Spring Data 의 `@Id` 아님) · `org.springframework.transaction.annotation.Transactional`(`jakarta.transaction` 쪽 아님)
- ⚠️ **테이블은 Flyway 의 `V` 파일로만 바뀐다(F6.5).** 엔티티를 고쳐도 테이블은 안 바뀌고, 칸 이름·타입이 어긋나면 `validate` 가 기동을 멈춘다 → `V2__…sql` 을 쓰고 엔티티를 맞춘다. MySQL 은 `datetime(0)` 을 `datetime` 으로 표시한다 — `(6)` 이 보이면 정밀도가 안 먹은 것
- ⚠️ **Hibernate 가 실행한 SQL 은 로그에 안 나온다** — `application.yml` 에 `show-sql` 이 없다. 테이블이 바뀌었는지는 Workbench 로 본다
- ⚠️ **auto_increment 번호는 롤백돼도 소비된다** — id 에 빈 번호가 생기는 게 정상이다. 참여자 수는 `COUNT(*)` 로 세고 `MAX(id)` 로 세지 않는다

### 테스트

- ⚠️ **테스트는 로컬 DB `newbeez` 에 실제로 붙는다**(`bootRun` 과 같은 DB). `@Transactional` 이라 행은 남지 않는다. Spring 은 테스트 트랜잭션의 시작·롤백을 기본 로그(INFO)에 찍지 않는다 — 롤백은 Workbench 에서 실행 전후 `COUNT(*)` 가 같은지로 확인한다
- ⚠️ **`@Transactional` 테스트 안에서 다시 읽은 값은 DB 값이 아니다** — 같은 트랜잭션이 들고 있는 객체가 그대로 돌아온다. DB 에 실제로 들어간 값(반올림된 시각 등)을 보려면 `flush` · `clear` 뒤에 읽는다(노트북 2026-10-27)
- ⚠️ **캐시는 롤백되지 않는다** — 앱 메모리라 테스트 트랜잭션 밖이다. 캐시를 쓰는 기능의 테스트는 시작 전에 캐시를 비운다(예: `ParticipantLogControllerTest` 의 `@BeforeEach`)
- 테스트 데이터는 **프론트가 실제로 만들 수 있는 값**으로 — 답코드는 `032102223` 같은 9자리(노트북 2026-10-27)
- ⚠️ **`@SpringBootTest` 없이 `@Autowired` 를 쓰면** IntelliJ 가 빨간 밑줄(`Autowired members must be defined in valid Spring bean`)을 띄우고, 실행하면 필드가 `null` 로 남아 **`NullPointerException`** 으로 실패한다(노트북 정정)
- ⚠️ **통과한 테스트는 콘솔에 아무것도 안 찍는다** — `BUILD SUCCESSFUL` 대신 `build/test-results/test/TEST-*.xml` 의 tests·failures·시각으로 확인한다. 로그·`println` 도 거기 있다. 한 클래스만 실행은 `./gradlew test --tests "com.newbeez.newbeezback.participant.ParticipantLogControllerTest"`
- ⚠️ **코드가 그대로면 Gradle 이 테스트를 건너뛴다**(`UP-TO-DATE`) — 그래도 `BUILD SUCCESSFUL` 이 뜬다. 마지막 줄 `N actionable tasks` 에 `executed` 가 있는지 본다. 다시 돌리려면 `--rerun`, PR 전에는 `./gradlew clean build`. IntelliJ 에서 고친 파일은 **저장해야** Gradle 이 본다
- ⚠️ **결과는 컴파일된 시점의 코드에 대한 것이다** — 소스를 고친 뒤에는 `build/classes/java/test/…/*.class` 시각이 소스 저장 시각보다 나중인지 본다
- **테스트 메서드 순서는 보장되지 않는다** — 작성 순서와 다르게 돈다. 테스트마다 롤백 + 전후 차이로 비교하면 순서와 무관하다
- **앱은 테스트 설정 조합 수만큼 뜬다** — `@SpringBootTest` 만 붙은 클래스끼리는 앱 하나를 공유하고, `@AutoConfigureMockMvc` 가 붙은 클래스 · `properties` 를 덮어쓴 클래스(`CorsConfigTest`)는 각각 따로 띄운다 — 지금 3개(`./gradlew test` 로그의 `HikariPool-1`~`3` · 2026-11-20)
- **MockMvc 의 400 응답은 본문이 비어 있다** — 실제 서버의 `{timestamp, status, error, path}` 는 Boot 가 `/error` 로 넘겨서 만든 것이고 MockMvc 는 넘기지 않는다. 어느 쪽이든 어느 검사에 걸렸는지는 본문으로 구별 못 한다 → 테스트는 상태 코드만 본다
- Spring Boot 4.1 의 테스트 애노테이션 패키지가 바뀌었다 — `@AutoConfigureMockMvc` 는 `org.springframework.boot.webmvc.test.autoconfigure`. JUnit 은 **Jupiter 6.0.3**(JUnit 6)
- ⚠️ **주소 끝 `/` 는 다른 주소다** — `/api/participants/` 는 404. 실패 로그의 `Handler Type = ResourceHttpRequestHandler` · `No static resource …` 가 주소 오타 신호다(맞으면 컨트롤러가 찍힌다). 로그가 가리키는 줄은 **걸린 검사**이고, 원인은 그 위 요청 기록에서 찾는다(2026-11-20)
- **`@Test` 는 서로 독립이다 — 테스트는 브라우저가 아니다.** 사전 요청 테스트가 실패해도 진짜 POST 테스트는 따로 나간다(2026-11-20 사용자 예상과 달랐던 곳)
- **애너테이션에 넣는 상수**는 `static final` 이고 `private` 이 아니어야 한다 — 클래스 위 애너테이션은 몸통 밖이라 `CorsConfigTest.ALLOWED` 처럼 클래스 이름을 붙여 쓴다(javac 21: `has private access` · `cannot find symbol` · `element value must be a constant expression`)
- import 후보가 둘일 때 — `assertThat` 은 `org.assertj.core.api.Assertions`(Hamcrest 쪽은 컴파일 오류) · `HttpHeaders` 는 `org.springframework.http`(`java.net.http` 쪽은 `ORIGIN` 상수가 없다)

### IntelliJ

- ⚠️ **IntelliJ 의 빨간 표시는 판정 기준이 아니다** — 판정은 Gradle 출력과 결과 xml 로 한다. 실제로 본 것: F6-3 테스트의 빨간 표시(`compileTestJava` 는 성공 · 메시지는 확인 안 함) · **`Could not autowire. No beans of 'CacheManager' type found.`**(F6-4 — 실행하면 빈이 있다. IntelliJ 의 Gradle 모델에는 캐시 jar 가 들어 있어 Sync 문제가 아니다. Boot 가 `ImportSelector` 로 캐시 설정을 고르는 걸 정적 검사가 못 따라가는 것으로 추정). `jun98` PC 에서 괄호가 빨간 것은 ColorBrackets 플러그인 색이다
- IntelliJ 는 텍스트 블록 안에서도 `{` 를 치면 `}` 를 자동으로 넣는다 — 일부러 깨뜨린 JSON 은 친 뒤 확인한다
- **여백 ▶ 는 위치마다 범위가 다르다** — 메서드 옆은 그 메서드만 돈다. 커밋 전에는 클래스 옆 ▶ · 무엇이 돌았는지는 로그 마지막 줄 `--tests "…"` 와 결과 xml 의 `tests=` 로 본다(2026-11-19 한 건만 돌리고 전체 통과로 볼 뻔했다)

### ✅ `team_results` 는 읽기 전용 시드 테이블이다 (2026-10-20)

옛 설계의 "API 동기화 영역 + 수동 큐레이션 영역 하이브리드" 와 그에 따른 `save()` 사고 경고는 **폐기**됐다. 런타임에 이 테이블을 쓰는 코드는 0개여야 한다 — 값을 바꾸려면 `R__team_results_seed.sql` 을 고쳐 배포한다. 운영 DB 를 직접 `UPDATE` 하면 다음 시드 변경 때 덮어써진다(ARCHITECTURE §7.1 런북 2단계).

### ⚠️ 두 테이블의 카테고리 확장 준비도가 다르다

서비스는 3단 계층(홈 → 카테고리 → 테스트)으로 커진다. 그런데 `category_slug` 가 실제로 일하는 곳은 한쪽뿐이다.

| 테이블 | 카메라·등산 행을 넣을 수 있나 |
| --- | --- |
| `participant_logs` (id · category_slug · result_slug · answer_code · created_at) | ✅ **완전 제네릭** — 그대로 됨 |
| `team_results` (stadium · league_name · manager · legend …) | ❌ **축구 전용 컬럼 덩어리** |

→ 카테고리가 늘면 `team_results` 를 늘리는 게 아니라 **별도 구조로 간다** (형태는 4차에 결정 · 프론트 `docs/ARCHITECTURE.md` §5).
🚫 **`team_results` 에 범용 컬럼을 덧붙이는 방향으로 가지 말 것.**

### ⚠️ 스택 버전이 학습 데이터와 다르다

`spring-boot-starter-webmvc`(옛 `-web` 아님) · Jackson 3 는 `tools.jackson` · JPA 는 `jakarta.persistence.*`. 자세한 건 [CLAUDE.md](CLAUDE.md).

### 🔀 프론트와의 관계

- **레포는 2개로 유지한다** (폴리레포). 두 레포가 공유하는 파일이 0개고, Java↔TS 는 타입도 공유할 수 없어 모노레포 이득이 없다. 2026-10-06 의 재검토 제안(문서 공유·PC 두 대 비용 때문에 F6-3 전에 합치자)은 그 시점이 지났고 폴리레포로 계속 진행 중이다 — 다시 꺼낼지는 사용자가 정한다
- 프론트 파일을 볼 때는 **절대 경로로 읽는다** — 경로는 PC마다 다르다 → [CLAUDE.md](CLAUDE.md)
- **프론트의 앱 코드는 고치지 않는다** — 계약의 원본이 프론트에 있고 백엔드는 참조하는 쪽이다
- **설계 문서와 프론트 HANDOFF 는 사용자 승인을 받고 고친다** — 결정이 난 자리에서 원본에 바로 적어야 다른 창·다른 PC에 전달된다. 대화에만 남은 결정은 사라진다 (2026-10-06 답코드 건이 첫 사례)

**📤 프론트에 넘길 것** (노트북 2026-11-02 확인 · 프론트 HANDOFF 로 옮기면 여기서 지운다 — 옮기는 것은 사용자 승인 뒤)
- `BAD_CODES` 2개 교체 — 길이 검사 변이를 audit 가 못 잡는다
- ARCHITECTURE §9 #3 · #5 · #6 — #6(임베드 실기기)은 아이폰 카톡 인앱을 사용자가 확인했다
- 노트북의 커밋 안 한 바이에른 변경 — 사용자 결정 대기
- `app/football/quiz/_components/ParticipantCount.tsx` 주석의 `GET /api/v1/participants/count` — 주소 버전은 PR #4 에서 없앴다 → `/api/participants/count` (2026-11-03 발견)
- `docs/FUTURE.md` §4 정정(2026-11-24 계정 화면 기준) — 크레딧 만료는 **2027-11-14**이고 무료 플랜 종료 2027-05-14 와 다른 날짜다("유료 전환 뒤 만료 … 5/14 로 본다" 줄) · 활동은 6개에 각 $20(상한 $100) · 위젯의 한국어 이름은 "AWS 살펴보기" · 예산 활동은 서비스 요금 없음
- `docs/FUTURE.md` §4 · `docs/ARCHITECTURE.md` §6·§7 — 운영 형태 변경(2026-11-24): EC2 한 대에 앱 + MySQL 컨테이너 · RDS 는 1주 실습 · 월 $20.66(서울 리전 공식 가격으로 숫자 교체) · 백업은 `mysqldump` · 계정 이력(2025-01-03 생성 → 2026-11-14 무료 플랜 활성화 · 계정 하나). 근거는 위 "D1 진행"의 "운영 형태"

---

## 그 외

- **로컬 DB**: MySQL 8.0.x · `MySQL80` 서비스 · DB `newbeez` — **PC마다 따로 있고 내용이 오가지 않는다**(위 F6-1 표). ⚠️ 8.0 은 2026-04-30 에 지원이 끝난 버전이다. 로컬 학습용으론 문제없지만 **배포 DB는 8.4 이상**으로 고른다
- **배포처: AWS 무료 플랜으로 결정 (2026-11-14 · 만료 2027-05-14)** — 근거는 프론트 `docs/FUTURE.md` §4(프론트 `main` 의 `0cbb130` · PR #9 로 머지됨 · 옛 §4 는 AWS Free 를 "피할 것"으로 분류했었다). 옛 1순위 Oracle Always Free 는 폐기. ⚠️ **필수 조건(2026-10-27): 유휴 회수가 없을 것**(F7 이후 결과 페이지 빌드가 백엔드에 의존) · 만료 전에 다음 호스팅을 정한다
- **D1 준비물** — Docker 28.5.2 가 `jun98` PC 에 있다(Dockerfile 시험 가능) · 레포에 Dockerfile 은 아직 없다 · `./gradlew bootJar` 가 만드는 실행용 jar(`build/libs/newbeez-back-0.0.1-SNAPSHOT.jar` · 약 55MB · 라이브러리 포함 · 테스트를 안 돌려 DB 없이 만들어진다)가 컨테이너에 넣을 파일이다. `-plain.jar` 는 우리 코드만이라 혼자 실행되지 않는다
- **D1 배포 확인에 넣을 것** — 운영 환경변수에 `NEWBEEZ_CORS_ALLOWED_ORIGINS`(운영 프론트 주소 · 로컬 주소는 빼도 된다)를 넣고, 띄운 뒤 **`Origin: <운영 프론트 주소>` 를 붙인 GET count(⑬ 모양)** 를 보내 `Access-Control-Allow-Origin` 이 오는지 본다 — 값이 비어 있으면 Origin 없는 확인(헬스 체크)은 200 이라 놓친다(F6-5 2-3 실측)
- **D1 체크리스트** (2026-11-09 jar·공식 문서 검증 — 근거는 그날 커밋 본문)
  - **이미지** — ✅ 2026-11-23 만들었다(위 "D1 진행" 2단계 · jar 는 PC 에서 `./gradlew bootJar` · `COPY` 는 파일 이름을 정확히 · `.dockerignore` 는 허용 목록 — 옛 계획의 "`build/`·`.env` 제외"는 이미지 안에서 빌드하는 방식을 가정한 것이었다) · `ENTRYPOINT` 는 exec form `["java","-jar",…]`(shell form 은 SIGTERM 을 못 받는다) · arm64 서버(Oracle A1 · AWS Graviton)면 `docker buildx build --platform linux/arm64`
  - **종료** — graceful 은 Boot 3.4 부터 기본값이라 설정하지 않는다. `docker stop` 유예 10초가 Boot 대기 30초보다 짧으니 한쪽을 맞춘다
  - **헬스** — `spring-boot-starter-actuator` 한 줄이면 `/actuator/health`(DB 가 죽으면 503 · 노출 기본값이 이미 health 뿐). ⚠️ DB 가 꺼진 채 기동하면 503 이 아니라 앱이 안 뜬다 → 호스팅 자동 재시작 설정과 함께 본다
  - **시간대** — 운영 JDBC URL 끝에 `?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true`(`NOW()`·수동 INSERT 까지 UTC) · `hibernate.jdbc.time_zone` 은 넣지 않는다 · 배포 뒤 1회 실측
  - **DB** — 운영 DB 는 **빈 DB** 로 만들고 첫 기동 때 Flyway 가 `V1` 로 표를 만든다(baseline 불필요 · 로컬처럼 `DROP` 할 것이 없다 · 표가 하나라도 먼저 있으면 `Found non-empty schema(s)` 로 기동이 멈춘다 — 위 "다른 PC" 절 1번). MySQL 8.4 + Flyway 12.4 조합은 여기서 1회 검증(로컬 예행 2026-11-23 — `mysql:8.4.13` 컨테이너에서 경고 없이 통과 · 서버의 MySQL 8.4 컨테이너에서 한 번 더 본다) · 기동 로그에 `Migrating schema … to version "1 - participant logs"` 가 찍히는지 본다
  - **Testcontainers 는 F6.5 뒤** — 좌표는 2.x 이름(`org.testcontainers:testcontainers-mysql`) · 로컬 MySQL 대신 Docker 가 필요해진다
- **락인 방어(지금부터 적용)**: 컨테이너화 · DB 접속은 환경변수로만 · Flyway · 트리거/프로시저 금지
- **Instagram 임베드 테스트 페이지** `C:\dev\embed-test\index.html` (`이현준` PC · git 밖) — 327px 본문에 게시물/캡션/계정 카드 3종을 띄우고 렌더 시간·크기를 재는 정적 HTML. `python -m http.server 8000 --bind 127.0.0.1 --directory C:\dev\embed-test` 로 열람. 2026-10-20 PC 크롬 확인 · 아이폰 카톡 인앱은 사용자가 확인함(노트북 기록 · 프론트 §9 #6 반영은 위 "프론트에 넘길 것")
