# HANDOFF — 진행 상황 & 다음 할 일 (백엔드)

> 세션 시작 시 이 파일을 먼저 읽고 이어서 작업.
>
> **이 파일은 "상태 · 다음 할 일 · 함정"만 담는다.** 설계는 아래로 미루고 여기에 중복하지 않는다.
> - 시스템 설계(스키마·API·역할분담) → **프론트 레포 `docs/ARCHITECTURE.md`** (로컬 경로는 PC마다 다름 → [CLAUDE.md](CLAUDE.md))
> - 작업 규칙(역할·커밋·스택 주의점) → [CLAUDE.md](CLAUDE.md)
> - 프론트 진행 상황 → 프론트 레포 `HANDOFF.md`

**마지막 업데이트: 2026-10-27** — **F6-2 완료**(PR #2 머지 · `5559dd6`). **F6-3 진행 중** — 계약 확정(2026-10-27 · 프론트 ARCHITECTURE §6), 코드는 아래 F6-3 절의 2단계부터. 2026-10-20 에 **F7 의 설계가 확정**됐다(이미지 없음 · `team_results` 전부 수동 시드 · Flyway 를 F7 직전으로) → 아래 F7 절 · 프론트 ARCHITECTURE §5.1·§6·§7 · `docs/LEGAL.md`
🔀 **작업은 브랜치 `feat/participant-api` 에 있다**(F6-3 · origin 에 푸시됨) — `main` 에는 F6-2 까지 병합됐다(PR #1 · #2). `main` 에 직접 커밋·push 하지 않는다 — PR 로만

### ▶ 다른 PC에서 이어갈 때

대화 기록과 `.env`·DB 는 PC를 넘어가지 않는다. 넘어가는 것은 git 에 올린 것뿐이다.

1. **백엔드** — `git fetch --prune` 후 `git switch feat/participant-api` · `git pull`. 남아 있는 옛 로컬 브랜치 `feat/participant-log` 는 `git branch -d feat/participant-log` (PR #2 로 머지 완료)
2. **프론트** — `git switch main` 후 `git pull` (2026-10-27 PR #2 로 §3.1 답코드 예시·문서 정정이 들어갔고, §6 계약은 별도 PR)
3. **그 PC 의 준비** — 아래 F6-1 표에서 ⬜ 인 것부터(계정 · `.env`, `NEWBEEZ_DDL_AUTO=update` 포함). `bootRun` 이 404 까지 가면 그 PC 의 DB 에도 `participant_logs` 가 생긴 것이다 — Workbench `SHOW CREATE TABLE participant_logs;` 가 아래 SQL 과 같은지 본다
4. **IntelliJ 를 쓰면 두 가지를 먼저 본다** — `.idea` 는 git 이 무시해서 PC마다 설정이 다르다
   - **Project SDK 가 21 인가** (File → Project Structure → Project → SDK). `jun98` PC 에는 실험판 `valhalla-ea-23` 이 잡혀 있었다 → 2026-10-16 `ms-21` (21.0.11) 로 바꿈
   - **실행은 상단 드롭다운의 `NewbeezBackApplication`(Spring Boot) ▶** — Gradle 실행 설정은 task 칸이 비어 있으면 `> Task :help` 만 돌고 앱이 안 켜진다
5. **F6-3** 을 이어간다 — 아래 "F6-3" 절의 진행 표에서 ⬜ 인 첫 단계부터
6. ⚠️ **Claude 의 메모리는 PC마다 따로다** — 다른 PC가 알아야 할 결론은 이 파일과 프론트 문서에 옮겨 둔다 (F7 설계 확정 2026-10-20 · F6-2 의 패키지 구조 결정). 학습 진행 방식은 [CLAUDE.md](CLAUDE.md) 에 적었다

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
| **F6-2** | `participant_logs` 엔티티 + Repository + 테스트 | ✅ PR #2 `5559dd6` (2026-10-27) |
| **F6-3** | `POST /api/v1/participants` — 4계층 첫 관통 | 🟡 **진행 중** (`feat/participant-api`) |
| F6-4 | `GET /api/v1/participants/count` + **1분 캐시** + **임계값은 서버에서** | ⬜ |
| F6-5 | **CORS** — 브라우저가 직접 부르는 두 엔드포인트만 | ⬜ |
| **F6.5** | **Flyway 전환** — `V1__participant_logs.sql` · `ddl-auto` 는 `validate` 고정 | ⬜ **배포 전** (2026-10-27 F7 앞에서 배포 앞으로 이동) |
| **D1** | **첫 배포** — Dockerfile · 운영 MySQL 8.4 · 환경변수 · 유휴 회수 없는 호스팅 | ⬜ 2026-10-27 추가 |
| 연동 | 프론트가 완료 시 `POST` + `ParticipantCount` — 실제 참여자 수·답코드가 쌓이기 시작 | ⬜ (프론트 작업) |
| F7 | `team_results`(읽기 전용 · Flyway `V2__` + `R__` 시드 15행) + `GET /results` | ⬜ **배포·연동 뒤** — 설계 확정 2026-10-20 · 백엔드 유지 2026-10-27. 상세는 아래 "F7 설계 확정" |

**순서 결정 (2026-10-27)** — 참여 기록 하나를 **배포까지 먼저** 끝낸다. 실제로 쓰이는 기능이 일찍 생기고, 답코드가 일찍 쌓이며(§5.2 "기록하지 않은 답은 되살릴 수 없다"), S8 이 의존하는 백엔드 호스팅이 S8 전에 준비된다.
**팀 콘텐츠는 백엔드에 둔다 (2026-10-27 재검토)** — 프론트 정적 데이터로 옮기는 안을 검토했으나 유지. 팀 소개·인물은 코드와 따로 바뀌는 **편집 콘텐츠**라 DB 가 맞고, 3차 UGC·4차 카테고리도 DB 에 쌓인다. 대가: S8 이후 **백엔드가 죽으면 프론트 새 배포가 실패**한다(기존 결과 페이지는 마지막 성공본 유지) → D1 의 호스팅은 **유휴 회수가 없어야** 한다

이후: 댓글 (2차)

### ✅ F6-1 DB 설정 — 2026-10-06

**접속 정보는 레포 밖에 있다.** `application.yml` 에는 빈칸 3개(`${NEWBEEZ_DB_URL}` · `_USERNAME` · `_PASSWORD`)만 있고, 로컬 값은 프로젝트 루트 **`.env`**(git 무시)에서 `spring.config.import` 로 읽는다. 배포에서는 `.env` 없이 같은 이름의 환경변수를 준다(환경변수가 우선).

**PC별 준비 상태** — DB·계정·`.env` 는 git 으로 오가지 않는다. PC마다 한 번씩 만든다.

| | `jun98` (데스크톱) | `이현준` |
| --- | --- | --- |
| MySQL | ✅ 8.0.46 (`MySQL80`) | `MySQL80` |
| DB `newbeez` | ✅ | ✅ (2026-09-04 기록) |
| 앱 전용 계정 `newbeez` | ✅ | ⬜ |
| `.env` | ✅ | ⬜ |
| `.env` 의 `NEWBEEZ_DDL_AUTO=update` | ✅ (2026-10-14) | ⬜ |

**⬜ 인 PC에서 할 일**

1. MySQL 에 root 로 접속해 실행 — 앱은 root 가 아니라 `newbeez` DB만 만질 수 있는 계정으로 붙는다
   ```sql
   CREATE DATABASE newbeez;   -- 이미 있으면 생략
   CREATE USER 'newbeez'@'localhost' IDENTIFIED BY '비밀번호';
   GRANT ALL PRIVILEGES ON newbeez.* TO 'newbeez'@'localhost';
   ```
2. `.env.example` 을 같은 폴더에 `.env` 로 복사하고 DB 값 3개를 채운다
   - `NEWBEEZ_DDL_AUTO=update` 줄은 **켜 둔다** — F6-2 의 "일부러 실패"(2단계) 확인은 2026-10-14 에 끝났고, 켜져 있어야 엔티티대로 테이블이 생긴다
3. `./gradlew bootRun` → `Started NewbeezBackApplication` 이 찍히고 http://localhost:8080 이 **404** 면 성공 (컨트롤러가 아직 없어 404 가 정상)

**겪은 함정**

- 🚨 **`'url' must start with "jdbc"` 로 죽으면 `.env` 가 없거나 변수 이름이 틀린 것.** Spring Boot 는 못 채운 `${...}` 를 오류 없이 **글자 그대로** 넘기기 때문에, "빈칸을 못 채웠다"가 아니라 이 엉뚱한 문구가 나온다
- ⚠️ **YAML 들여쓰기 실수는 조용히 무시된다.** `datasource:` 가 한 단계 깊이 들어가면 `spring.application.config.datasource.url` 같은 **Spring 이 모르는 이름**이 되고 오류도 안 난다. 증상은 "고쳤는데 오류 문구가 그대로"
- ⚠️ **`build` 도 DB를 요구한다** — `contextLoads` 테스트가 앱을 통째로 띄운다. 지금은 CI 가 없어 괜찮지만 배포용 빌드를 만들 때 다시 만난다
- 로그의 `The following 1 profile is active: "loc"` 는 `jun98` PC 의 사용자 환경변수 탓이다(다른 프로젝트용). 해롭지 않지만 **설정을 프로필에 기대면 안 되는 이유**다 → [CLAUDE.md](CLAUDE.md) 규칙
- ✅ 부팅 로그의 `spring.jpa.open-in-view is enabled by default` WARN — F6-3 에서 `open-in-view: false` 로 껐다(2026-10-27 · `e18fc06`)

### ✅ F6-2 `participant_logs` 엔티티 · Repository · 테스트 — 2026-10-27 (PR #2 · `5559dd6`)

**설계 원본** — 프론트 `docs/ARCHITECTURE.md` §5.2.

**정한 것** — 2026-10-06 (패키지 2026-10-16 · 테스트 2026-10-26~27)

| | 결정 | 이유 |
| --- | --- | --- |
| 패키지 | **기능별** — `participant/` 에 엔티티 · Repository (`domain/` · `repository/` 같은 계층별 폴더 안 씀) | Spring Boot 문서 "Structuring Your Code" 예시가 기능별이다 · 두 테이블이 외래키 없이 독립이라 기능 단위로 깔끔히 갈린다 · 패키지는 DB 와 무관(테이블 이름은 `@Table` 이 정함)해서 나중에 바꿔도 된다 |
| 칸 수 | **5칸** — `answer_code VARCHAR(32)` NULL 허용을 추가 | 분석용. 기록하지 않은 답은 되살릴 수 없다. 백엔드는 해석하지 않고 보관만 한다 |
| `ddl-auto` | `${NEWBEEZ_DDL_AUTO:validate}` — 기본은 `validate`, 로컬 `.env` 에서만 `update` | 값을 안 준 환경(배포)에서 테이블이 멋대로 바뀌지 않게. **배포 전 Flyway 전환**은 확정 사항 |
| `created_at` | 자바 `Instant` → **UTC 로 저장**. 2026-10-26 측정으로 확인 — Java 출력 `07:57:29.28Z` = DB `07:57:29` = 로컬 KST 16:57:29 − 9시간 (드라이버가 변환하지 않음 · `datetime(0)` 이라 소수점 아래는 반올림) | PC(KST)와 배포 서버의 시간대가 달라도 섞이지 않게. Workbench 에서는 9시간 이르게 보인다 |
| 생성자 | **`public` 3개짜리**(새 기록용 — `id` 는 안 받고 `createdAt` 은 안에서 `Instant.now()`) + **`protected` 빈 생성자**(Hibernate 가 행을 객체로 복원할 때) | `id` 는 MySQL 이 매기고, 시각을 DB 함수에 맡기지 않는다(규칙). 빈 생성자는 JPA 규칙이고 프록시가 자식 클래스라 `private` 불가 |
| setter | **없음** — getter 5개만 | 참여 기록은 쌓기만 하고 고치지 않는다(append-only) |
| 테스트 | **`@SpringBootTest` + `@Transactional`** — 실제 로컬 MySQL 에 `INSERT` 하고, 끝나면 롤백 | 진짜 DB 동작(UTC·인덱스)을 보면서도 행을 남기지 않는다. 테스트 전용 DB 는 MVP 에 과하다 |
| Lombok | 이 엔티티는 **쓰지 않는다** | 무엇을 줄여 주는지 한 번은 직접 써 봐야 안다. F7 `team_results`(30칸)에서 도입 |

**코드는 `participant/` 폴더가 원본이다.** 여기에 코드 사본을 두지 않는다.
- [`ParticipantLog.java`](src/main/java/com/newbeez/newbeezback/participant/ParticipantLog.java) — 엔티티 · 5칸 + 생성자 2개 + getter 5개
- [`ParticipantLogRepository.java`](src/main/java/com/newbeez/newbeezback/participant/ParticipantLogRepository.java) — `JpaRepository<ParticipantLog, Long>` 를 상속만 하는 빈 interface. 구현 객체는 Spring Data JPA 가 실행 시 만든다
- [`ParticipantLogRepositoryTest.java`](src/test/java/com/newbeez/newbeezback/participant/ParticipantLogRepositoryTest.java) — `@SpringBootTest` 로 앱을 띄워 로컬 MySQL 에 실제로 저장 · `save` 전후 `getId()` (null → 번호) 검증 + `createdAt` 출력 · `@Transactional` 로 끝나면 롤백

**만들어지는 테이블** — 칸이 알파벳순으로 나오는 것은 정상이다. F6.5 에서 Flyway `V1__participant_logs.sql` 로 옮긴다

```sql
create table participant_logs (
    id bigint not null auto_increment,
    answer_code varchar(32),
    category_slug varchar(32) not null,
    created_at datetime(0) not null,
    result_slug varchar(32) not null,
    primary key (id)
);
create index idx_cat_slug on participant_logs (category_slug, result_slug);
create index idx_created on participant_logs (created_at);
```

**함정**

- ⚠️ **`import` 는 보이는 대로 직접 친다** — 이름이 같은 다른 타입이 있어 IntelliJ 자동 가져오기가 엉뚱한 것을 잡는다. 우리가 쓰는 것은 `jakarta.persistence.Id`(Spring Data 의 `@Id` 아님) · `org.springframework.transaction.annotation.Transactional`(`jakarta.transaction` 쪽 아님)
- ⚠️ **`update` 는 추가만 한다.** 필드 이름을 바꾸면 옛 컬럼이 남는다 → Workbench 에서 테이블을 지우고 다시 켠다
- ✅ `update` 는 인덱스까지 만든다 (`SHOW CREATE TABLE` 에 `KEY` 2줄). MySQL 은 `datetime(0)` 을 `datetime` 으로 표시한다 — `(6)` 이 보이면 `secondPrecision` 이 안 먹은 것
- ⚠️ **Hibernate 가 실행한 SQL 은 로그에 안 나온다** — `application.yml` 에 `show-sql` 이 없다. 테이블이 바뀌었는지는 Workbench 로 본다
- ⚠️ **테스트는 로컬 DB `newbeez` 에 실제로 붙는다**(`bootRun` 과 같은 DB). `@Transactional` 이 있어 행은 남지 않지만 **auto_increment 번호는 소비된다** — 롤백·`DELETE` 로 되돌아가지 않으므로 id 에 빈 번호가 생기는 게 정상이다. 참여자 수는 `COUNT(*)` 로 세고 `MAX(id)` 로 세지 않는다
- ⚠️ **Spring 은 테스트 트랜잭션의 시작·롤백을 기본 로그(INFO)에 찍지 않는다** — 롤백은 Workbench 에서 실행 전후 `COUNT(*)` 가 같은지로 확인한다(2026-10-27 · 2/2 → id 3·4 출력 → 2/2)
- ⚠️ **`@SpringBootTest` 없이 `@Autowired` 를 쓰면 IntelliJ 가 빨간 밑줄**(`Autowired members must be defined in valid Spring bean`)을 띄운다 — 컴파일 오류가 아니라 Spring 검사 경고다. 실행하면 필드가 `null` 로 남아 `expected: not <null>` 로 실패한다(2026-10-26 일부러 확인)
- ⚠️ **통과한 테스트는 콘솔에 아무것도 안 찍는다** — `println` 출력과 Spring 로그는 `build/test-results/test/*.xml` · `build/reports/tests/test/index.html` 에 있다. 실행은 `./gradlew test --tests "com.newbeez.newbeezback.participant.ParticipantLogRepositoryTest"`
- ⚠️ **코드가 그대로면 Gradle 이 테스트를 건너뛴다**(`UP-TO-DATE`) — 그래도 `BUILD SUCCESSFUL` 이 뜬다. 마지막 줄 `N actionable tasks` 에 `executed` 가 있는지 보고, 다시 돌리려면 `--rerun` 을 붙인다. IntelliJ 에서 고친 파일은 **저장해야** Gradle 이 본다

### 🟡 F6-3 `POST /api/v1/participants` — 진행 중 (브랜치 `feat/participant-api`)

**왜 이것부터** — `GET /results`(팀 표시 정보)는 **채울 내용이 아직 없다** — 15행 시드는 F6.5(Flyway) 뒤 F7 에서 만든다(2026-10-20 설계 · 옛 S3 슬라이스는 폐기). `participants` 는 의존성이 0이고, 프론트에 **받을 자리가 이미 있다**(프론트 HANDOFF "F6 연동").

**계약의 원본은 프론트 ARCHITECTURE §6** (2026-10-27 확정 · 여기에 복사하지 않는다). 백엔드가 알아야 할 것만:

- `categorySlug` 는 **서버 허용 목록**(MVP: `football`)으로 검사 · `resultSlug` 는 **형식만** 검사(15팀 목록 대조 ✗) — 어기면 400
- ⚠️ **`answerCode` 에는 `@Pattern` 을 걸지 않는다** — 걸면 요청 전체가 400 이 되어 "형식이 틀려도 참여 기록은 남긴다"(§5.2)를 어긴다. Service 에서 범용 형식(숫자 1~32자)만 보고 틀리면 `null` 로 저장. 축구 9자리 규칙은 Java 에 복사하지 않는다(원본 `lib/answer-code.ts`)
- 성공은 **`201` 본문 없음** · **`id` 를 응답에 넣지 않는다** — 순번이라 전체 참여자 수가 드러나 §4.3 임계값을 우회한다
- `application/json` POST → 브라우저가 `OPTIONS` 사전 요청을 보낸다 → **F6-5 CORS 에서 `OPTIONS`·`Content-Type` 허용**
- 알고 받아들인 위험: 서버 쪽 중복·남용 방어 없음(중복 차단은 프론트 `sessionStorage` 뿐 · §4.4)

**진행 상황** (2026-10-27 계획 승인)

| | 할 일 | 누가 | 검증 |
| --- | --- | --- | --- |
| 0 | 브랜치 `feat/participant-api` + 이 HANDOFF 갱신 | Claude | ✅ |
| 1 | 계약을 프론트 §6 에 기록 → 프론트 `docs/participants-contract` PR | Claude | ✅ 프론트 PR #3 (2026-10-27) |
| 2 | `application.yml` 에 `spring.jpa.open-in-view: false` | 사용자 | ✅ 2026-10-27 `e18fc06` — 테스트 로그의 open-in-view WARN 1건 → 0건 |
| 3 | `build.gradle` 에 `spring-boot-starter-validation` | 사용자 | ✅ 2026-10-27 `3129662` — starter 4.1.0 · hibernate-validator 9.1.0.Final 다운로드 · 테스트 통과 |
| **4** | 요청 DTO `record` (`@NotBlank` · `@Pattern`) **← 다음** | 사용자 | ⬜ 컴파일 |
| 5 | Service — 생성자 주입 · `@Transactional`(정상 종료 = 커밋) · 허용 목록 · 답코드 정리 | 사용자 | ⬜ 6·7 에서 |
| 6 | Controller — `@PostMapping` · `@ResponseStatus(CREATED)` · `@Valid @RequestBody` | 사용자 | ⬜ `bootRun` + `curl.exe` — 정상 201 + 행 · slug 틀림 400 + 행 없음 · 답코드 틀림 201 + `NULL` |
| 7 | 테스트 — `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester` + `@Transactional` | 사용자 | ⬜ `./gradlew test` |
| 8 | HANDOFF 정리 · PR | Claude | ⬜ |

- ⚠️ **PowerShell 에서 `curl` 은 `Invoke-WebRequest` 의 별칭이다** — 반드시 `curl.exe` 로 친다. IntelliJ HTTP Client(`.http` 파일)·Postman CLI 도 이 PC 에 있다
- Spring Boot 4.1 의 테스트 애노테이션 패키지가 바뀌었다 — `@AutoConfigureMockMvc` 는 `org.springframework.boot.webmvc.test.autoconfigure` (jar 에서 확인). JUnit 은 **Jupiter 6.0.3**(JUnit 6)

### ⬜ F6-4 참여자 수 — `COUNT(*)` 를 매 요청 하지 말 것

바이럴로 수십만 행이 쌓이면 체감된다. **1분 TTL 캐시**(`@Cacheable`). 1분 늦게 갱신돼도 문제없다.
→ `spring-boot-starter-cache` 의존성이 **아직 build.gradle 에 없다.** F6-4 에서 추가.

⚠️ **starter 만으로는 만료가 안 된다** — 기본 캐시(`ConcurrentMapCacheManager`)에는 TTL 기능이 없다(2026-10-06 jar 확인). 1분 만료를 쓰려면 Caffeine 같은 구현체가 함께 필요하다.

**임계값은 서버가 적용한다** (2026-10-27 · 프론트 ARCHITECTURE §4.3) — 임계값 미만이면 숫자를 내보내지 않는다. 화면에서만 숨기면 API 를 직접 불러 "3명"을 볼 수 있다. 기준값(§9 #2)과 응답 모양은 F6-4 시작 때 정해 §6 에 적는다.

### ⬜ F6.5 Flyway (배포 전) + F7 `team_results` (배포·연동 뒤) — 설계 확정 (2026-10-20)

⚠️ **F6.5 함정 — 로컬 테이블은 Hibernate(`ddl-auto=update`)가 만들었다.** Flyway `V1__participant_logs.sql` 의 `create table` 이 "이미 있음"으로 실패한다 → 로컬 `participant_logs` 를 지우고 Flyway 가 만들게 하거나 baseline 설정. PC 마다 한 번씩(DB 가 PC 별로 따로라서)

**스키마·칸 설명·검증 규칙의 원본은 프론트 ARCHITECTURE §5.1** (여기에 복사하지 않는다). 백엔드가 알아야 할 것만:

- **읽기 전용 테이블.** 엔티티는 setter 없음 + `@org.hibernate.annotations.Immutable`(ORM 애노테이션 — "벤더 함수 금지" 규칙과 무관) · Repository 는 조회만. 배치·동기화 코드 없음 — 옛 "하이브리드(API 동기화 영역)" 설계는 폐기됐다
- **데이터는 Flyway 시드 파일이 진실** — `src/main/resources/db/migration/`: DDL 은 `V1__participant_logs.sql`·`V2__team_results.sql`, **15행은 `R__team_results_seed.sql`**(`DELETE … WHERE category_slug='football'; INSERT …`). DDL 과 DML 을 한 파일에 섞지 않는다(MySQL DDL 암묵적 커밋). 각 행 주석에 출처 URL·확인일. 벤더 구문(`ON DUPLICATE KEY UPDATE`) ✗
- **Flyway 도입과 동시에 `.env` 의 `NEWBEEZ_DDL_AUTO=update` 를 지운다** — Hibernate 가 Flyway 밖에서 스키마를 바꾸면 드리프트. `validate` 는 유지하되 엔티티 `@Column(length…)`·타입을 DDL 과 맞춰야 기동된다(`validate` 는 VARCHAR 길이는 검사하지 않음). 의존성 `spring-boot-starter-flyway` + `org.flywaydb:flyway-mysql`(Spring Boot 4.1 → Flyway 12.4). MySQL 8.4 는 문서 목록에 없지만 Redgate 가 지원 확인(2026-08) — 배포 DB 로 1회 검증
- **`kit_post_url` 정규화·검증은 Java** — 쿼리스트링 제거 → `https://www.instagram.com/p/{code}/` → `@Pattern`(`spring-boot-starter-validation` 추가). 사진 게시물 여부는 등록 체크리스트
- **`GET /results` 응답 형태**는 ARCHITECTURE §6 에 적혀 있다 — `kit: { postUrl, seasonLabel } | null`. 프론트는 실패 시 던지므로(빌드 fail-fast) **백엔드가 먼저 떠 있어야 프론트 S8 이 배포된다**
- Lombok 은 여기서 도입(기존 결정 유지)
- 시즌 교체·삭제 요청 런북은 ARCHITECTURE §7.1 — 백엔드 쪽 할 일은 "시드 파일 수정 → 배포" 뿐

---

## 📌 함정 모음

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

- **레포는 2개로 유지한다** (폴리레포). 두 레포가 공유하는 파일이 0개고, Java↔TS 는 타입도 공유할 수 없어 모노레포 이득이 없다
- 프론트 파일을 볼 때는 **절대 경로로 읽는다** — 경로는 PC마다 다르다 → [CLAUDE.md](CLAUDE.md)
- **프론트의 앱 코드는 고치지 않는다** — 계약의 원본이 프론트에 있고 백엔드는 참조하는 쪽이다
- **설계 문서와 프론트 HANDOFF 는 사용자 승인을 받고 고친다** — 결정이 난 자리에서 원본에 바로 적어야 다른 창·다른 PC에 전달된다. 대화에만 남은 결정은 사라진다 (2026-10-06 답코드 건이 첫 사례)
- ⏸ **모노레포로 합칠지 재검토 중** (2026-10-06 · 사용자 결정 대기). 9월의 판단은 코드만 봤고, **두 레포가 문서를 공유한다는 점**과 PC 두 대를 오가는 비용(낡은 클론 · PC별 경로 · 다른 레포의 문서 수정)을 계산에 넣지 않았다. Claude 의 제안은 **F6-2 를 끝낸 직후, F6-3 전에** 합치는 것 — 지금이 옮길 것이 가장 적고, F6-3 이 처음으로 양쪽에 걸치는 작업이기 때문이다. 합치면 Vercel 에 프론트 폴더 지정과 "백엔드만 바뀐 푸시는 배포 건너뛰기" 설정이 필요하다

---

## 그 외

- **로컬 DB**: MySQL 8.0.x · `MySQL80` 서비스 · DB `newbeez` — **PC마다 따로 있고 내용이 오가지 않는다**(위 F6-1 표). ⚠️ 8.0 은 2026-04-30 에 지원이 끝난 버전이다. 로컬 학습용으론 문제없지만 **배포 DB는 8.4 이상**으로 고른다
- **배포처**: 1순위 Oracle Cloud Always Free 서울(A1 + MySQL HeatWave 50GB). **결정은 D1 에서** — A1 확보가 복불복이라 미리 시도 권장. 근거는 프론트 `docs/FUTURE.md` §4. ⚠️ **필수 조건(2026-10-27): 유휴 회수가 없을 것** — Always Free 그대로면 7일 유휴 시 회수되므로 PAYG 전환이나 다른 호스팅이 필요하다(F7 이후 결과 페이지 빌드가 백엔드에 의존)
- **D1 준비물** — Docker 28.5.2 가 `jun98` PC 에 있다(Dockerfile 시험 가능) · 레포에 Dockerfile 은 아직 없다
- **락인 방어(지금부터 적용)**: 컨테이너화 · DB 접속은 환경변수로만 · Flyway · 트리거/프로시저 금지
- **Instagram 임베드 테스트 페이지** `C:\dev\embed-test\index.html` (`이현준` PC · git 밖) — 327px 본문에 게시물/캡션/계정 카드 3종을 띄우고 렌더 시간·크기를 재는 정적 HTML. `python -m http.server 8000 --bind 127.0.0.1 --directory C:\dev\embed-test` 로 열람. 2026-10-20 PC 크롬 확인 완료, 실기기(iOS Safari·카톡 인앱)는 미확인(프론트 ARCHITECTURE §9 #6)
