# HANDOFF — 진행 상황 & 다음 할 일 (백엔드)

> 세션 시작 시 이 파일을 먼저 읽고 이어서 작업.
>
> **이 파일은 "상태 · 다음 할 일 · 함정"만 담는다.** 설계는 아래로 미루고 여기에 중복하지 않는다.
> - 시스템 설계(스키마·API·역할분담) → **프론트 레포 `docs/ARCHITECTURE.md`** (로컬 경로는 PC마다 다름 → [CLAUDE.md](CLAUDE.md))
> - 작업 규칙(역할·커밋·스택 주의점) → [CLAUDE.md](CLAUDE.md)
> - 프론트 진행 상황 → 프론트 레포 `HANDOFF.md`

**마지막 업데이트: 2026-10-26** — **F6-2 4a ④ 테스트 완료**(`14ff6ff` · `save` 가 id 를 부여하고 UTC 로 저장함을 측정으로 확인). **다음은 4b — 테스트에 `@Transactional`.** 2026-10-20 에 **F7 의 설계가 확정**됐다(이미지 없음 · `team_results` 전부 수동 시드 · Flyway 를 F7 직전으로) → 아래 F7 절 · 프론트 ARCHITECTURE §5.1·§6·§7 · `docs/LEGAL.md`
🔀 **작업은 브랜치 `feat/participant-log` 에 있다** — `main` 에는 4a ③ 까지 병합됐다(PR #1 · 2026-10-16). 그 뒤의 문서 커밋 2개(10-20)와 4a ④ 테스트(`14ff6ff`)는 브랜치에만 있다 · origin 에 푸시됨

### ▶ 다른 PC에서 이어갈 때

대화 기록과 `.env`·DB 는 PC를 넘어가지 않는다. 넘어가는 것은 git 에 올린 것뿐이다.

1. **백엔드** — `git pull` 후 `git switch feat/participant-log`
2. **프론트** — `git pull` (설계 문서가 `72f7197` 로 갱신됐다 — 이미지 없음 · Instagram 임베드 · `docs/LEGAL.md` 신설)
3. **그 PC 의 준비** — 아래 F6-1 표에서 ⬜ 인 것부터(계정 · `.env`, `NEWBEEZ_DDL_AUTO=update` 포함). `bootRun` 이 404 까지 가면 그 PC 의 DB 에도 `participant_logs` 가 생긴 것이다 — Workbench `SHOW CREATE TABLE participant_logs;` 가 아래 SQL 과 같은지 본다
4. **IntelliJ 를 쓰면 두 가지를 먼저 본다** — `.idea` 는 git 이 무시해서 PC마다 설정이 다르다
   - **Project SDK 가 21 인가** (File → Project Structure → Project → SDK). `jun98` PC 에는 실험판 `valhalla-ea-23` 이 잡혀 있었다 → 2026-10-16 `ms-21` (21.0.11) 로 바꿈
   - **실행은 상단 드롭다운의 `NewbeezBackApplication`(Spring Boot) ▶** — Gradle 실행 설정은 task 칸이 비어 있으면 `> Task :help` 만 돌고 앱이 안 켜진다
5. **F6-2 의 4b 부터** 이어간다 (아래 "F6-2 진행 중") — 테스트 파일은 이미 있고, `@Transactional` 한 줄을 붙이는 단계
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
| **F6-2** | `participant_logs` 엔티티 + Repository | 🟡 **진행 중** (`feat/participant-log`) |
| F6-3 | `POST /api/v1/participants` — 4계층 첫 관통 | ⬜ |
| F6-4 | `GET /api/v1/participants/count` + **1분 캐시** | ⬜ |
| F6-5 | **CORS** — 브라우저가 직접 부르는 두 엔드포인트만 | ⬜ |
| **F6.5** | **Flyway 전환** — `V1__participant_logs.sql` · `ddl-auto` 는 `validate` 고정 | ⬜ F7 직전 (2026-10-20 당김) |
| F7 | `team_results`(읽기 전용 · Flyway `V2__` + `R__` 시드 15행) + `GET /results` | ⬜ **설계 확정 2026-10-20** — 프론트 S3 선행조건 없음(S3 폐기). 상세는 아래 "F7 설계 확정" |

이후: 배포처 확보(⚠️ Oracle Always Free 는 **7일 유휴 시 인스턴스 회수** — 프론트 FUTURE §4) → 댓글

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
- 부팅 로그의 `spring.jpa.open-in-view is enabled by default` WARN 은 실패가 아니다. F6-3 에서 다룬다

### 🟡 F6-2 `participant_logs` 엔티티 — 진행 중 (브랜치 `feat/participant-log`)

**설계 원본은 이미 고쳤다** — 프론트 `docs/ARCHITECTURE.md` §5.2 (프론트 커밋 `0a1f68c`).

**정한 것** — 2026-10-06 (패키지만 2026-10-16)

| | 결정 | 이유 |
| --- | --- | --- |
| 패키지 | **기능별** — `participant/` 에 엔티티 · Repository (`domain/` · `repository/` 같은 계층별 폴더 안 씀) | Spring Boot 문서 "Structuring Your Code" 예시가 기능별이다 · 두 테이블이 외래키 없이 독립이라 기능 단위로 깔끔히 갈린다 · 패키지는 DB 와 무관(테이블 이름은 `@Table` 이 정함)해서 나중에 바꿔도 된다 |
| 칸 수 | **5칸** — `answer_code VARCHAR(32)` NULL 허용을 추가 | 분석용. 기록하지 않은 답은 되살릴 수 없다. 백엔드는 해석하지 않고 보관만 한다 |
| `ddl-auto` | `${NEWBEEZ_DDL_AUTO:validate}` — 기본은 `validate`, 로컬 `.env` 에서만 `update` | 값을 안 준 환경(배포)에서 테이블이 멋대로 바뀌지 않게. **배포 전 Flyway 전환**은 확정 사항 |
| `created_at` | 자바 `Instant` → **UTC 로 저장** | PC(KST)와 배포 서버의 시간대가 달라도 섞이지 않게. Workbench 에서는 9시간 이르게 보인다 |
| Lombok | 이 엔티티는 **쓰지 않는다** | 무엇을 줄여 주는지 한 번은 직접 써 봐야 안다. F7 `team_results`(30칸)에서 도입 |

**진행 상황**

| | 할 일 | 누가 | 상태 · 통과 기준 |
| --- | --- | --- | --- |
| 0 | 브랜치 생성, `.env.example` 에 `NEWBEEZ_DDL_AUTO` 추가 | Claude | ✅ |
| 1 | `application.yml` 에 `ddl-auto` 3줄 | 사용자 | ✅ 파일 비교로 확인 (실행은 아직 안 했다) |
| 2 | 엔티티 `ParticipantLog.java` | 사용자 | ✅ 2026-10-14 — `missing table [participant_logs]` 로 실패 확인 |
| 3 | `.env` 에 `NEWBEEZ_DDL_AUTO=update` | 사용자 | ✅ 2026-10-14 — `bootRun` 성공 + `SHOW CREATE TABLE` 이 아래 SQL 과 일치(인덱스 2개 포함) |
| 4a | ① 생성자 ✅ (2026-10-14) · ② getter ✅ · ③ Repository ✅ (2026-10-16 — 기능별 패키지로 이동 포함) · ④ 테스트 ✅ (2026-10-26 · `14ff6ff`) | 사용자 | ✅ 테스트 통과 + Workbench 에 행 1개(id=1), 시각이 9시간 이름 |
| **4b** | 테스트에 `@Transactional` **← 다음** | 사용자 | ⬜ 실행 전후로 행 수가 같음 (출력되는 id 는 계속 올라간다 — auto_increment 는 롤백되지 않는다) |
| 5 | HANDOFF 정리 · 커밋 · 병합 · 푸시 | Claude | ⬜ |

**코드는 `participant/` 폴더가 원본이다.** 여기에 코드 사본을 두지 않는다.
- [`ParticipantLog.java`](src/main/java/com/newbeez/newbeezback/participant/ParticipantLog.java) — 엔티티 · 5칸 + 생성자 2개 + getter 5개
- [`ParticipantLogRepository.java`](src/main/java/com/newbeez/newbeezback/participant/ParticipantLogRepository.java) — `JpaRepository<ParticipantLog, Long>` 를 상속만 하는 빈 interface. 구현 객체는 Spring Data JPA 가 실행 시 만든다
- [`ParticipantLogRepositoryTest.java`](src/test/java/com/newbeez/newbeezback/participant/ParticipantLogRepositoryTest.java) — `@SpringBootTest` 로 앱을 띄워 로컬 MySQL 에 실제로 저장 · `save` 전후 `getId()` (null → 번호) 검증 + `createdAt` 출력

만들어지는 테이블 — 칸이 알파벳순으로 나오는 것은 정상이다

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

**알아 둘 것**

- ⚠️ **`import` 두 줄은 보이는 대로 직접 친다.** IntelliJ 자동 가져오기에 맡기면 Spring Data 의 다른 `@Id` 가 잡힐 수 있다
- ⚠️ **`update` 는 추가만 한다.** 필드 이름을 바꾸면 옛 컬럼이 남는다 → Workbench 에서 테이블을 지우고 다시 켠다
- 생성자는 둘이다 (4a ① ✅). **`public` 3개짜리** = 새 기록용 — `id` 는 안 받고(MySQL 이 매김) `createdAt` 은 안에서 `Instant.now()` (시각을 DB 함수에 맡기지 않는다 — 규칙). **`protected` 빈 생성자** = Hibernate 가 행을 객체로 복원할 때 쓴다(JPA 규칙 · 프록시가 자식 클래스라 `private` 불가)
- getter 는 5칸 모두 (4a ② ✅) · **setter 는 만들지 않는다** — 참여 기록은 쌓기만 하고 고치지 않는다(append-only)
- 4a ③ 의 확인법 — 부팅 로그 `Found 1 JPA repository interface` (엔티티만 있을 땐 `0`). 이 줄은 interface 를 **찾았다**는 뜻일 뿐, `save` 가 실제로 되는지는 ④ 에서 본다
- ✅ 4a ④ 결과 (2026-10-26) — `getId()` `null` → `1` · Workbench `SELECT *` 에 행 1개. `save` 는 자기 트랜잭션으로 커밋하므로 **테스트가 끝나도 행이 남는다** → 4b 에서 `@Transactional` 로 되돌린다. `jun98` PC DB 에는 4b 이전에 넣은 테스트 행이 남아 있으므로 **4b 는 "실행 전후 행 수가 같은가"로 판정**한다
- 테스트만 골라 실행: `./gradlew test --tests "com.newbeez.newbeezback.participant.ParticipantLogRepositoryTest"`. **통과한 테스트는 콘솔에 아무것도 안 찍는다** — `println` 출력과 Spring 로그는 `build/test-results/test/*.xml` 또는 `build/reports/tests/test/index.html` 에 있다
- ⚠️ **`@SpringBootTest` 없이 `@Autowired` 를 쓰면 IntelliJ 가 빨간 밑줄**(`Autowired members must be defined in valid Spring bean`)을 띄운다 — 컴파일 오류가 아니라 Spring 검사 경고다. 실행하면 필드가 `null` 로 남아 `expected: not <null>` 로 실패한다(2026-10-26 일부러 확인). `@SpringBootTest` 를 붙이면 밑줄이 사라진다
- ⚠️ **Hibernate 가 실행한 SQL 은 로그에 안 나온다** — `application.yml` 에 `show-sql` 이 없다. `create table`·`alter table` 이 돌았는지는 로그가 아니라 Workbench 로 본다
- ✅ **UTC 저장 확인 (2026-10-26)** — Java `Instant` 출력 `2026-10-26T07:57:29.28Z` = DB `created_at` `07:57:29` = 로컬 KST 16:57:29 − 9시간. 드라이버를 거쳐도 변환되지 않는다. `datetime(0)` 이라 소수점 아래는 반올림된다
- ✅ `update` 는 인덱스까지 만든다 (2026-10-14 `SHOW CREATE TABLE` 에 `KEY` 2줄). MySQL 은 `datetime(0)` 을 `datetime` 으로 표시한다 — `(6)` 이 보이면 `secondPrecision` 이 안 먹은 것
- 이 절은 인계용이라 길다. F6-2 가 끝나면 결정과 함정만 남기고 줄인다

### ⬜ F6-3 첫 엔드포인트를 `participants` 로 잡은 이유

`GET /results`(팀 표시 정보)를 먼저 하고 싶어지지만 **채울 내용이 아직 없다** — 배지·감독·경기장 수집이 프론트 S3 슬라이스에 걸려 있다. `participants` 는 의존성이 0이고, 프론트에 **받을 자리가 이미 있다**(`_components/ParticipantCount.tsx` 가 `return null` 로 대기 중).

⚠️ **시작 전에 프론트 문서부터** — ARCHITECTURE §6 에는 경로만 있고 **요청·응답 본문 형태가 없다.** 프론트에도 아직 호출부가 없으니, 계약을 §6 에 먼저 적고 양쪽이 그걸 따른다.

**답코드도 함께 받는다**(설계 §4.4·§5.2). 백엔드는 형식만 본다 — 형식이 틀렸을 때 **요청을 거절할지, 답코드만 비우고 기록은 남길지**를 여기서 정한다. 칸을 NULL 허용으로 둔 것은 뒤쪽을 가능하게 하려는 것이다.

### ⬜ F6-4 참여자 수 — `COUNT(*)` 를 매 요청 하지 말 것

바이럴로 수십만 행이 쌓이면 체감된다. **1분 TTL 캐시**(`@Cacheable`). 1분 늦게 갱신돼도 문제없다.
→ `spring-boot-starter-cache` 의존성이 **아직 build.gradle 에 없다.** F6-4 에서 추가.

⚠️ **starter 만으로는 만료가 안 된다** — 기본 캐시(`ConcurrentMapCacheManager`)에는 TTL 기능이 없다(2026-10-06 jar 확인). 1분 만료를 쓰려면 Caffeine 같은 구현체가 함께 필요하다.

### ⬜ F6.5 Flyway + F7 `team_results` — 설계 확정 (2026-10-20)

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
- **배포처**: 1순위 Oracle Cloud Always Free 서울(A1 + MySQL HeatWave 50GB). **결정 보류** — A1 확보가 복불복이라 미리 시도 권장. 근거는 프론트 `docs/FUTURE.md` §4
- **락인 방어(지금부터 적용)**: 컨테이너화 · DB 접속은 환경변수로만 · Flyway · 트리거/프로시저 금지
- **Instagram 임베드 테스트 페이지** `C:\dev\embed-test\index.html` (`이현준` PC · git 밖) — 327px 본문에 게시물/캡션/계정 카드 3종을 띄우고 렌더 시간·크기를 재는 정적 HTML. `python -m http.server 8000 --bind 127.0.0.1 --directory C:\dev\embed-test` 로 열람. 2026-10-20 PC 크롬 확인 완료, 실기기(iOS Safari·카톡 인앱)는 미확인(프론트 ARCHITECTURE §9 #6)
