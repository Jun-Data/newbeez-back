# HANDOFF — 진행 상황 & 다음 할 일 (백엔드)

> 세션 시작 시 이 파일을 먼저 읽고 이어서 작업.
>
> **이 파일은 "상태 · 다음 할 일 · 함정"만 담는다.** 설계는 아래로 미루고 여기에 중복하지 않는다.
> - 시스템 설계(스키마·API·역할분담) → **프론트 레포 `docs/ARCHITECTURE.md`** (로컬 경로는 PC마다 다름 → [CLAUDE.md](CLAUDE.md))
> - 작업 규칙(역할·커밋·스택 주의점) → [CLAUDE.md](CLAUDE.md)
> - 프론트 진행 상황 → 프론트 레포 `HANDOFF.md`

**마지막 업데이트: 2026-10-06** — F6-1 DB 설정 완료(`bootRun`·`build` 통과). **다음은 F6-2 `participant_logs` 엔티티**
브랜치 `main` 하나 · origin 동기화 · 워킹트리 깨끗

---

## 🧹 2026-09-04 — 레거시 폐기

7월 15일에 만든 `docs/`·`data/`·`Category.java`·`application.yml` 을 **전부 버리고** 스캐폴드 커밋(`03f9357`)으로 되돌렸다.

**이유**: 그것들은 *백엔드가 채점하던 시절*의 설계였다. 프론트가 "계산은 프론트, 표시 데이터는 백엔드"로 넘어갔는데 이 레포만 따라가지 못했다.

| | 7월 문서 | 현행 |
| --- | --- | --- |
| 문항 | 10개 (Q10 색상 포함) | **9개** |
| 매칭 | 유클리드 + 색 타이브레이크 | **코사인** (근소동점만 유클리드) |
| 결과 | 우승 + 상극팀 | 우승 + **라이벌**(고정값) |
| 채점 위치 | 백엔드 Service | **프론트 순수함수** |
| 테이블 | Category·Question·QuizOption·Item | **`team_results`·`participant_logs` 둘** |

🔑 **교훈 — 설계를 두 레포에 복사해두면 한쪽만 낡는다.** 그래서 [CLAUDE.md](CLAUDE.md) 에 "여기에 설계를 복사하지 말 것"을 규칙으로 박았다.

> 15팀 좌표는 폐기 대상이 아니다 — **여전히 유효하고 프론트 `lib/clubs.ts` 가 원본**이다. 백엔드는 좌표를 쓰지 않으므로 사본을 두지 않는다.

---

## 🗺️ 슬라이스 로드맵

**MVP 경계는 프론트 ARCHITECTURE §6 (엔드포인트 4개).**

| | 슬라이스 | 상태 |
| --- | --- | --- |
| **F6-1** | **DB 설정** — `application.yml` + 환경변수 접속 → `bootRun` 성공 | ✅ `44e2283` |
| **F6-2** | `participant_logs` 엔티티 + Repository | ⬜ **다음** |
| F6-3 | `POST /api/v1/participants` — 4계층 첫 관통 | ⬜ |
| F6-4 | `GET /api/v1/participants/count` + **1분 캐시** | ⬜ |
| F6-5 | **CORS** — 브라우저가 직접 부르는 두 엔드포인트만 | ⬜ |
| F7 | `team_results` + `GET /results` | ⬜ 프론트 S3(배지·팀 데이터 수집) 이후 |

이후: 배포처 확보 → Flyway 전환 → 댓글

### ✅ F6-1 DB 설정 — 2026-10-06

**접속 정보는 레포 밖에 있다.** `application.yml` 에는 빈칸 3개(`${NEWBEEZ_DB_URL}` · `_USERNAME` · `_PASSWORD`)만 있고, 로컬 값은 프로젝트 루트 **`.env`**(git 무시)에서 `spring.config.import` 로 읽는다. 배포에서는 `.env` 없이 같은 이름의 환경변수를 준다(환경변수가 우선).

**PC별 준비 상태** — DB·계정·`.env` 는 git 으로 오가지 않는다. PC마다 한 번씩 만든다.

| | `jun98` (데스크톱) | `이현준` |
| --- | --- | --- |
| MySQL | ✅ 8.0.46 (`MySQL80`) | `MySQL80` |
| DB `newbeez` | ✅ | ✅ (2026-09-04 기록) |
| 앱 전용 계정 `newbeez` | ✅ | ⬜ |
| `.env` | ✅ | ⬜ |

**⬜ 인 PC에서 할 일**

1. MySQL 에 root 로 접속해 실행 — 앱은 root 가 아니라 `newbeez` DB만 만질 수 있는 계정으로 붙는다
   ```sql
   CREATE DATABASE newbeez;   -- 이미 있으면 생략
   CREATE USER 'newbeez'@'localhost' IDENTIFIED BY '비밀번호';
   GRANT ALL PRIVILEGES ON newbeez.* TO 'newbeez'@'localhost';
   ```
2. `.env.example` 을 같은 폴더에 `.env` 로 복사하고 값 3개를 채운다
3. `./gradlew bootRun` → `Started NewbeezBackApplication` 이 찍히고 http://localhost:8080 이 **404** 면 성공 (컨트롤러가 아직 없어 404 가 정상)

**겪은 함정**

- 🚨 **`'url' must start with "jdbc"` 로 죽으면 `.env` 가 없거나 변수 이름이 틀린 것.** Spring Boot 는 못 채운 `${...}` 를 오류 없이 **글자 그대로** 넘기기 때문에, "빈칸을 못 채웠다"가 아니라 이 엉뚱한 문구가 나온다
- ⚠️ **YAML 들여쓰기 실수는 조용히 무시된다.** `datasource:` 가 한 단계 깊이 들어가면 `spring.application.config.datasource.url` 같은 **Spring 이 모르는 이름**이 되고 오류도 안 난다. 증상은 "고쳤는데 오류 문구가 그대로"
- ⚠️ **`build` 도 DB를 요구한다** — `contextLoads` 테스트가 앱을 통째로 띄운다. 지금은 CI 가 없어 괜찮지만 배포용 빌드를 만들 때 다시 만난다
- 로그의 `The following 1 profile is active: "loc"` 는 `jun98` PC 의 사용자 환경변수 탓이다(다른 프로젝트용). 해롭지 않지만 **설정을 프로필에 기대면 안 되는 이유**다 → [CLAUDE.md](CLAUDE.md) 규칙
- 부팅 로그의 `spring.jpa.open-in-view is enabled by default` WARN 은 실패가 아니다. F6-3 에서 다룬다

### ⬜ F6-2 `participant_logs` 엔티티 ← 다음

테이블 정의는 ARCHITECTURE §5.2. 시작 전에 정할 것 둘:

- **`ddl-auto`** — 아직 설정하지 않았다. MySQL 에서는 기본값이 `none` 이라 **엔티티를 만들어도 테이블이 생기지 않는다.** 학습 단계용 값을 여기서 정한다 (배포에서 켜지지 않게 할 방법 포함). **배포 전 Flyway 전환**은 확정 사항
- **`created_at` 의 시간대** — `DATETIME` 은 시간대 정보가 없다. 로컬(KST)과 배포 서버의 시간대가 다르면 값이 섞이고, **쌓인 뒤에는 고치기 어렵다.** 첫 행이 들어가기 전에 정한다

### ⬜ F6-3 첫 엔드포인트를 `participants` 로 잡은 이유

`GET /results`(팀 표시 정보)를 먼저 하고 싶어지지만 **채울 내용이 아직 없다** — 배지·감독·경기장 수집이 프론트 S3 슬라이스에 걸려 있다. `participants` 는 의존성이 0이고, 프론트에 **받을 자리가 이미 있다**(`_components/ParticipantCount.tsx` 가 `return null` 로 대기 중).

⚠️ **시작 전에 프론트 문서부터** — ARCHITECTURE §6 에는 경로만 있고 **요청·응답 본문 형태가 없다.** 프론트에도 아직 호출부가 없으니, 계약을 §6 에 먼저 적고 양쪽이 그걸 따른다.

### ⬜ F6-4 참여자 수 — `COUNT(*)` 를 매 요청 하지 말 것

바이럴로 수십만 행이 쌓이면 체감된다. **1분 TTL 캐시**(`@Cacheable`). 1분 늦게 갱신돼도 문제없다.
→ `spring-boot-starter-cache` 의존성이 **아직 build.gradle 에 없다.** F6-4 에서 추가.

⚠️ **starter 만으로는 만료가 안 된다** — 기본 캐시(`ConcurrentMapCacheManager`)에는 TTL 기능이 없다(2026-10-06 jar 확인). 1분 만료를 쓰려면 Caffeine 같은 구현체가 함께 필요하다.

---

## 📌 함정 모음

### 🚨 `team_results` 는 한 테이블에 두 성격이 섞여 있다

API 동기화 영역(배치가 건드림) + 수동 큐레이션 영역(배치가 절대 안 건드림)이 한 테이블에 있다.

**동기화 Service 는 기존 엔티티를 로드해 API 영역 setter 만 호출할 것.** 새 엔티티를 만들어 `save()` 하면 **수동 큐레이션 컬럼이 전부 날아간다.** 미러링 패턴의 1번 사고다. (ARCHITECTURE §5.1)

### ⚠️ 두 테이블의 카테고리 확장 준비도가 다르다

서비스는 3단 계층(홈 → 카테고리 → 테스트)으로 커진다. 그런데 `category_slug` 가 실제로 일하는 곳은 한쪽뿐이다.

| 테이블 | 카메라·등산 행을 넣을 수 있나 |
| --- | --- |
| `participant_logs` (id · category_slug · result_slug · created_at) | ✅ **완전 제네릭** — 그대로 됨 |
| `team_results` (stadium · league_name · manager · legend …) | ❌ **축구 전용 컬럼 덩어리** |

→ 카테고리가 늘면 `team_results` 를 늘리는 게 아니라 **별도 구조로 간다** (형태는 4차에 결정 · 프론트 `docs/ARCHITECTURE.md` §5).
🚫 **`team_results` 에 범용 컬럼을 덧붙이는 방향으로 가지 말 것.**

### ⚠️ 스택 버전이 학습 데이터와 다르다

`spring-boot-starter-webmvc`(옛 `-web` 아님) · Jackson 3 는 `tools.jackson` · JPA 는 `jakarta.persistence.*`. 자세한 건 [CLAUDE.md](CLAUDE.md).

### 🔀 프론트와의 관계

- **레포는 2개로 유지한다** (폴리레포). 두 레포가 공유하는 파일이 0개고, Java↔TS 는 타입도 공유할 수 없어 모노레포 이득이 없다
- 프론트 파일을 볼 때는 **절대 경로로 읽는다** — 경로는 PC마다 다르다 → [CLAUDE.md](CLAUDE.md)
- **읽기 위해 보는 것이지 고치지 않는다** — 계약의 원본이 프론트에 있고 백엔드는 참조하는 쪽이다

---

## 그 외

- **로컬 DB**: MySQL 8.0.x · `MySQL80` 서비스 · DB `newbeez` — **PC마다 따로 있고 내용이 오가지 않는다**(위 F6-1 표). ⚠️ 8.0 은 2026-04-30 에 지원이 끝난 버전이다. 로컬 학습용으론 문제없지만 **배포 DB는 8.4 이상**으로 고른다
- **배포처**: 1순위 Oracle Cloud Always Free 서울(A1 + MySQL HeatWave 50GB). **결정 보류** — A1 확보가 복불복이라 미리 시도 권장. 근거는 프론트 `docs/FUTURE.md` §4
- **락인 방어(지금부터 적용)**: 컨테이너화 · DB 접속은 환경변수로만 · Flyway · 트리거/프로시저 금지
