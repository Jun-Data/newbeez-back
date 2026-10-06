# Newbeez 백엔드 (newbeez-back)

> **세션 시작 시 [HANDOFF.md](HANDOFF.md) 먼저 확인** — 진행 상황·다음 할 일.

**사용자 성향 팀 추천 테스트**의 백엔드. 개인 학습 프로젝트.

## 🔑 이 레포가 하는 일 / 하지 않는 일

**계산은 프론트, 표시 데이터는 백엔드.** 사용자 답을 받아 결과를 돌려주는 구조가 **아니다.**

| | 담당 |
| --- | --- |
| ✅ **한다** | 팀 표시 정보(배지·경기장·감독·카피) 제공 · 참여 기록 · 참여자 수 집계 |
| 🚫 **안 한다** | 채점 · 문항 보관 · 팀 좌표 보관 · 추천 알고리즘 |

문항·배점·좌표·채점은 **전부 프론트 `lib/`** 에 있고 이미 완성·전수검증됐다. 두 층을 잇는 계약은 **`slug`** 하나(`man-city`, `chelsea` …).

**백엔드가 죽어도 퀴즈는 정상 동작해야 한다.** 참여자 수 한 줄만 조용히 비면 된다.

## 📍 설계 원본은 프론트에 있다

**시스템 설계(스키마·API·역할분담)의 원본 = `newbeez-front` 의 `docs/ARCHITECTURE.md`.**
GitHub `Jun-Data/newbeez-front` · 로컬 경로는 **PC마다 다르다.**

| PC (Windows 사용자) | 프론트 | 백엔드 |
| --- | --- | --- |
| `이현준` | `C:\Users\이현준\Desktop\newbeez` | `C:\dev\newbeez-back` |
| `jun98` (데스크톱) | `C:\newbeez-front` | `C:\newbeez-back` |

⚠️ **프론트 로컬 클론이 낡았을 수 있다** — 읽기 전에 `git fetch` + `git status` 로 원격과 같은지 확인. 2026-10-06 에 `jun98` PC 의 클론이 63커밋 뒤처져 `docs/` 자체가 없었다.

- 스키마 → ARCHITECTURE **§5** · API 명세 → **§6** · 운영/데이터 갱신 → **§7**
- MVP 이후(댓글·허브·인증·호스팅) → `docs/FUTURE.md`

🚫 **여기에 설계를 복사해두지 말 것.** 링크만 남긴다. 복사본은 반드시 낡는다 — 2026-09-04 에 이 레포의 `docs/` 가 두 달치 낡은 설계를 붙들고 있어 전부 폐기했다.

## 스택 — 최신 버전 (학습 데이터와 다를 수 있음, 검증하고 쓸 것)

- **Spring Boot 4.1** · **Java 21** · Gradle
- Spring Data JPA + **Hibernate 7.4** · **MySQL 8** (DB `newbeez`)
- ⚠️ **starter 이름이 `spring-boot-starter-webmvc`** (옛 `-web` 아님)
- ⚠️ **Jackson 3** — core 패키지가 `com.fasterxml.jackson` → **`tools.jackson`** 으로 바뀜
- ⚠️ JPA 애노테이션은 **`jakarta.persistence.*`** (javax 아님)

## 명령어

| 목적 | 명령 |
| --- | --- |
| 실행 | `./gradlew bootRun` (또는 IntelliJ 실행) → http://localhost:8080 |
| 빌드 | `./gradlew build` |

⚠️ **둘 다 로컬 MySQL 과 `.env` 가 필요하다** (`build` 도 `contextLoads` 테스트가 DB에 붙는다). 처음 받은 PC에서는 `.env.example` 을 `.env` 로 복사해 값을 채운다 — 절차는 [HANDOFF.md](HANDOFF.md).

## 구조 — 4계층

`Controller(REST) → Service(로직) → Repository(JPA) → MySQL`

패키지 루트 `com.newbeez.newbeezback` · 엔티티는 `domain/`

## MVP 테이블은 2개뿐

`team_results` · `participant_logs` — 정의는 **ARCHITECTURE §5**. 그 외 테이블은 만들지 않는다.

**`category_slug` 선반영 원칙**: 카테고리 종속 테이블에는 지금부터 이 컬럼을 넣는다(MVP 값은 전부 `'football'`).

⚠️ **다만 "행 추가만으로 새 카테고리"가 성립하는 건 `participant_logs` 뿐이다.** `team_results` 는 `stadium`·`league_name`·`manager`·`legend` 처럼 **축구 전용 컬럼 덩어리**라 카메라·등산 행이 들어갈 수 없다.

카테고리가 늘면 `team_results` 를 늘리는 게 아니라 **별도 구조로 간다**(형태는 4차에 결정 — 프론트 `docs/ARCHITECTURE.md` §5). `team_results` 의 `category_slug` 는 확장용이 아니라 **조회를 카테고리로 가르기 위한 것**이다.

## 규칙

- **DB 접속 정보는 환경변수로만.** 접속 URL·계정·비밀번호를 커밋되는 파일에 쓰지 않는다 (public 레포 · 배포처마다 값이 다름)
  - `application.yml` 에는 `${NEWBEEZ_DB_URL}` 같은 **빈칸만** 둔다. 로컬 값은 프로젝트 루트 `.env`(git 무시), 배포에서는 같은 이름의 환경변수
  - 🚫 **프로필(`application-<이름>.yml`)에 기대지 말 것** — OS 환경변수가 설정 파일을 이기므로, PC에 `SPRING_PROFILES_ACTIVE` 가 걸려 있으면 조용히 무시된다 (`jun98` PC 에 실제로 `loc` 이 걸려 있다)
- **트리거·프로시저·벤더 함수 금지** — 자바로 못 옮기는 로직이 DB 안에 생기면 호스팅을 못 옮긴다
- `ddl-auto` 자동 생성은 **학습 단계만.** 배포 전 Flyway 로 전환
- 사용자는 **프론트 입문자**이고 백엔드도 함께 학습 중 — 개념부터 설명(explain-first), 한 번에 완성본을 던지지 말고 작은 단계로 쪼갤 것
- **`pnpm`·`gradlew` 등 실행은 사용자가 직접.** 앱 코드도 사용자가 타이핑한다 (Claude 는 설계·설명·git·문서 담당)
- 무거운 인프라(인증·배포 자동화)는 명시적 요청 전엔 도입하지 않음
- **커밋** gitmoji + Conventional: `:이모지: type: 요약`. `Co-Authored-By` 안 씀
- Windows + 한글 경로 환경
