# HANDOFF — 진행 상황 & 다음 할 일 (백엔드)

> 세션 시작 시 이 파일을 먼저 읽고 이어서 작업.
>
> **이 파일은 "상태 · 다음 할 일 · 함정"만 담는다.** 설계는 아래로 미루고 여기에 중복하지 않는다.
> - 시스템 설계(스키마·API·역할분담) → **프론트 레포 `docs/ARCHITECTURE.md`** (`C:\Users\이현준\Desktop\newbeez`)
> - 작업 규칙(역할·커밋·스택 주의점) → [CLAUDE.md](CLAUDE.md)
> - 프론트 진행 상황 → 프론트 레포 `HANDOFF.md`

**마지막 업데이트: 2026-09-04** — 레거시 전량 폐기, 스캐폴드 상태로 초기화. **다음은 F6-1 DB 설정**
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
| **F6-1** | **DB 설정** — `application.yml` + 환경변수 접속 → `bootRun` 성공 | ⬜ **다음** |
| F6-2 | `participant_logs` 엔티티 + Repository | ⬜ |
| F6-3 | `POST /api/v1/participants` — 4계층 첫 관통 | ⬜ |
| F6-4 | `GET /api/v1/participants/count` + **1분 캐시** | ⬜ |
| F6-5 | **CORS** — 브라우저가 직접 부르는 두 엔드포인트만 | ⬜ |
| F7 | `team_results` + `GET /results` | ⬜ 프론트 S3(배지·팀 데이터 수집) 이후 |

이후: 배포처 확보 → Flyway 전환 → 댓글

### ⬜ F6-1 DB 설정 ← 다음

- **지금 `./gradlew bootRun` 하면 실패한다** — JPA 가 있는데 접속 정보가 없어 `Failed to configure a DataSource: 'url' attribute is not specified`
- 로컬 MySQL 은 **이미 준비돼 있다**: `MySQL80` 서비스 실행 중 · DB `newbeez` 생성됨 · **테이블 0개**
- ⚠️ **접속 정보를 커밋되는 파일에 쓰지 말 것** — 이 레포는 public 이고, 배포처가 정해지면 값이 달라진다. 환경변수로 주입하고 로컬 값은 `.gitignore` 뒤에 둔다
- `ddl-auto` 는 학습 단계라 자동 생성을 쓰되, **배포 전 Flyway 전환**은 확정 사항

### ⬜ F6-3 첫 엔드포인트를 `participants` 로 잡은 이유

`GET /results`(팀 표시 정보)를 먼저 하고 싶어지지만 **채울 내용이 아직 없다** — 배지·감독·경기장 수집이 프론트 S3 슬라이스에 걸려 있다. `participants` 는 의존성이 0이고, 프론트에 **받을 자리가 이미 있다**(`_components/ParticipantCount.tsx` 가 `return null` 로 대기 중).

### ⬜ F6-4 참여자 수 — `COUNT(*)` 를 매 요청 하지 말 것

바이럴로 수십만 행이 쌓이면 체감된다. **1분 TTL 캐시**(`@Cacheable`). 1분 늦게 갱신돼도 문제없다.
→ `spring-boot-starter-cache` 의존성이 **아직 build.gradle 에 없다.** F6-4 에서 추가.

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
- 프론트 파일을 볼 때는 **절대 경로로 읽는다** (`C:\Users\이현준\Desktop\newbeez\...`)
- **읽기 위해 보는 것이지 고치지 않는다** — 계약의 원본이 프론트에 있고 백엔드는 참조하는 쪽이다

---

## 그 외

- **로컬 DB**: MySQL 8 · DB `newbeez` · `MySQL80` 서비스
- **배포처**: 1순위 Oracle Cloud Always Free 서울(A1 + MySQL HeatWave 50GB). **결정 보류** — A1 확보가 복불복이라 미리 시도 권장. 근거는 프론트 `docs/FUTURE.md` §4
- **락인 방어(지금부터 적용)**: 컨테이너화 · DB 접속은 환경변수로만 · Flyway · 트리거/프로시저 금지
