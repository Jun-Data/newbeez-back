# HANDOFF — 진행 상황 & 다음 할 일 (백엔드)

> 세션 시작 시 이 파일을 먼저 읽고 이어서 작업.
>
> **이 파일은 "상태 · 다음 할 일 · 함정"만 담는다.** 설계는 아래로 미루고 여기에 중복하지 않는다. **끝난 슬라이스는 결론만 남기고 자세한 과정은 git 기록**에 맡긴다(2026-11-03 정리 — 진행 표·측정 기록은 커밋 본문에 있다).
> - 시스템 설계(스키마·API·역할분담) → **프론트 레포 `docs/ARCHITECTURE.md`** (로컬 경로는 PC마다 다름 → [CLAUDE.md](CLAUDE.md))
> - 작업 규칙(역할·커밋·브랜치·스택 주의점) → [CLAUDE.md](CLAUDE.md)
> - 프론트 진행 상황 → 프론트 레포 `HANDOFF.md`

**마지막 업데이트: 2026-11-03 (데스크톱)** — **F6-4 구현·테스트 완료, PR 대기** — `GET /api/participants/count?categorySlug=football` → `{"count": N}` + **10초 Caffeine 캐시**. `./gradlew clean build` 12건 통과 · jar 에 Caffeine 포함 확인. PR 이 머지되면 다음은 **F6-5 CORS**. F7 설계는 2026-10-20 확정 · Flyway 는 첫 배포 전 F6.5
🔀 **브랜치** — F6-4 전체가 `feat/participant-count` 에 있다. PR 머지를 확인하면 이 브랜치를 로컬·원격 모두 지우고, F6-5 는 `main` 에서 새 브랜치로 시작한다. `main` 에 직접 커밋·push 하지 않는다 — PR 로만 ([CLAUDE.md](CLAUDE.md))

### ▶ 다른 PC에서 이어갈 때

대화 기록과 `.env`·DB 는 PC를 넘어가지 않는다. 넘어가는 것은 git 에 올린 것뿐이다. 떠나는 PC 에서 할 일은 [CLAUDE.md](CLAUDE.md) "PC 를 떠날 때".

1. **백엔드** — `git fetch --prune` 후 작업 브랜치로(`git switch <브랜치>` · `git pull`). PR 이 이미 머지됐으면 `git switch main` · `git pull` 하고 머지된 로컬 브랜치는 `git branch -d` 로 지운다(옛 `feat/participant-log` · `feat/participant-api` 가 남아 있으면 같이) · ⚠️ **커밋 안 한 변경부터 치운다** — 원격에서 지운 파일(예: 2026-10-29 에 지운 `ParticipantLogRepositoryTest.java`)을 그 PC 에서 고치고 있었다면 `switch` 가 거부되지 않고 **그 파일을 새 브랜치로 조용히 들고 간다**(2026-11-02 노트북 재현). 먼저 커밋하거나 `git stash`
2. **프론트** — `git switch main` 후 `git pull` (§6 계약: PR #3 `POST /participants` · #4 주소 버전 제거 · #5 참여자 수 · #6 캐시 10초)
3. **그 PC 의 준비** — 아래 F6-1 표에서 ⬜ 인 것부터(계정 · `.env`, `NEWBEEZ_DDL_AUTO=update` 포함). `bootRun` 이 뜨면 그 PC 의 DB 에도 `participant_logs` 가 생긴 것이다 — Workbench `SHOW CREATE TABLE participant_logs;` 가 아래 F6-2 의 SQL 과 같은지 본다
4. **IntelliJ 를 쓰면 세 가지를 먼저 본다** — `.idea` 는 git 이 무시해서 PC마다 설정이 다르다
   - **Project SDK 가 21 인가** (File → Project Structure → Project → SDK). `jun98` PC 에는 실험판 `valhalla-ea-23` 이 잡혀 있었다 → 2026-10-16 `ms-21` (21.0.11) 로 바꿈
   - **실행은 상단 드롭다운의 `NewbeezBackApplication`(Spring Boot) ▶** — Gradle 실행 설정은 task 칸이 비어 있으면 `> Task :help` 만 돌고 앱이 안 켜진다
   - **`build.gradle` 이 바뀌었으면 Gradle 창의 Sync All Gradle Projects** — 안 하면 새 라이브러리의 `import` 가 빨갛다. Gradle 은 그 PC 에서 쓴 것만 받아 두고 30일 안 쓴 캐시는 지운다(2026-11-02 노트북 캐시엔 validation 이 없었다)
5. 아래 **로드맵에서 ⬜ 인 첫 슬라이스**를 이어간다
6. ⚠️ **Claude 의 메모리는 PC마다 따로다** — 다른 PC가 알아야 할 결론은 이 파일과 프론트 문서에 옮겨 둔다. 학습 진행 방식과 작업 규칙은 [CLAUDE.md](CLAUDE.md) 에 적었다

---

## 🗺️ 슬라이스 로드맵

**MVP 경계는 프론트 ARCHITECTURE §6 (엔드포인트 4개).**

| | 슬라이스 | 상태 |
| --- | --- | --- |
| **F6-1** | **DB 설정** — `application.yml` + 환경변수 접속 → `bootRun` 성공 | ✅ `44e2283` (2026-10-06) |
| **F6-2** | `participant_logs` 엔티티 + Repository | ✅ PR #2 `5559dd6` (2026-10-27) |
| **F6-3** | `POST /api/participants` — 4계층 첫 관통 | ✅ PR #3·#4·#5 `8e640af` (2026-10-29) |
| **F6-4** | `GET /api/participants/count?categorySlug=` + **10초 캐시** | ✅ 2026-11-03 — **PR 대기** (`feat/participant-count`) |
| 선택 | **Redis 캐시 실습** — 학습용 · 배포에 넣지 않는다. 아래 F6-4 절 "선택 과제" | ⬜ 아무 때나 (2026-11-03 추가) |
| F6-5 | **CORS** — 브라우저가 직접 부르는 두 엔드포인트만 | ⬜ **← 다음** |
| **F6.5** | **Flyway 전환** — `V1__participant_logs.sql` · `ddl-auto` 는 `validate` 고정 | ⬜ **배포 전** (2026-10-27 F7 앞에서 배포 앞으로 이동) |
| **D1** | **첫 배포** — Dockerfile · 운영 MySQL 8.4 · 환경변수 · 유휴 회수 없는 호스팅 | ⬜ 2026-10-27 추가 |
| 연동 | 프론트가 완료 시 `POST` + `ParticipantCount` — 실제 참여자 수·답코드가 쌓이기 시작 | ⬜ (프론트 작업) |
| F7 | `team_results`(읽기 전용 · Flyway `V2__` + `R__` 시드 15행) + `GET /results` | ⬜ **배포·연동 뒤** — 설계 확정 2026-10-20 · 백엔드 유지 2026-10-27. 상세는 아래 "F7 설계 확정" |

**순서 결정 (2026-10-27)** — 참여 기록 하나를 **배포까지 먼저** 끝낸다. 실제로 쓰이는 기능이 일찍 생기고, 답코드가 일찍 쌓이며(§5.2 "기록하지 않은 답은 되살릴 수 없다"), S8 이 의존하는 백엔드 호스팅이 S8 전에 준비된다.
**팀 콘텐츠는 백엔드에 둔다 (2026-10-27 재검토)** — 프론트 정적 데이터로 옮기는 안을 검토했으나 유지. 팀 소개·인물은 코드와 따로 바뀌는 **편집 콘텐츠**라 DB 가 맞고, 3차 UGC·4차 카테고리도 DB 에 쌓인다. 대가: S8 이후 **백엔드가 죽으면 프론트 새 배포가 실패**한다(기존 결과 페이지는 마지막 성공본 유지) → D1 의 호스팅은 **유휴 회수가 없어야** 한다

이후: 댓글 (2차)

---

## ⬜ 다음 — F6-5 CORS

- 대상은 **브라우저가 직접 부르는 두 엔드포인트**뿐 — `POST /api/participants` · `GET /api/participants/count`
- `application/json` POST 라서 브라우저가 **`OPTIONS` 사전 요청**을 먼저 보낸다 → `OPTIONS` 메서드와 `Content-Type` 헤더를 허용해야 한다(F6-3 계약 때 확인)
- 계약 원본은 프론트 ARCHITECTURE §6 — 시작 전에 프론트 클론을 `git fetch` 로 맞추고 다시 읽는다

---

## ✅ 끝난 슬라이스 — 결론만

### ✅ F6-4 참여자 수 `GET /api/participants/count` — 2026-11-03 (PR 대기)

**계약의 원본은 프론트 ARCHITECTURE §6** (2026-10-29 확정 · 10초는 2026-11-02). 백엔드가 알아야 할 것만:

- **임계값 없음** (2026-10-29 사용자 결정) — 0 이어도 그대로 돌려준다
- **카테고리별** — 쿼리 `categorySlug` 필수 · `POST` 와 같은 허용 목록(`football`) · 어기면 400. 전체 수(`repository.count()`)가 아니라 `category_slug` 로 거른 수를 센다
- 응답 `200` · `{ "count": N }` — 칸 이름이 있는 객체
- **10초 캐시** — 바이럴로 수십만 행이 쌓이면 매 요청 `COUNT(*)` 가 체감된다. 10초면 DB `COUNT` 는 방문자 수와 무관하게 분당 **최대 6번**(`sync = true` 기준)이고 숫자는 최대 10초 늦다. "1분"은 정석이 아니라 설계 때 고른 값이었다(2026-11-02 사용자 결정 · 프론트 §5·§6 은 프론트 PR #6 `78dee33` 로 고침)

**만든 것** — 코드가 원본이다
- Repository `countByCategorySlug` → Service `count`(`@Cacheable(cacheNames = "participantCount", sync = true)` + `@Transactional(readOnly = true)`) → Controller `@GetMapping("/count")`(`@RequestParam @Pattern(regexp = "football") String categorySlug`) → 응답 record `ParticipantCountResponse(long count)`
- 캐시 설정: `build.gradle` 에 `spring-boot-starter-cache` + `caffeine`(BOM 이 3.2.4) · `application.yml` 의 `spring.cache.type: caffeine` · `cache-names: participantCount` · `caffeine.spec: expireAfterWrite=10s` · 메인 클래스에 `@EnableCaching`
- 수동 확인 `http/participants.http` ⑦~⑫ · 테스트 5개 + `@BeforeEach`(아래)

**캐시를 이렇게 둔 이유** (Boot 4.1.0 · Framework 7.0.8 jar 확인)
- **starter 만으로는 만료가 안 된다** — 기본 캐시(`ConcurrentMapCacheManager`)에는 TTL 이 없다 → Caffeine
- **조용한 실패를 시끄럽게 바꾼다** — ① Caffeine 이 빠지면 Boot 는 오류 없이 기본 캐시로 바꾼다(`@ConditionalOnClass(Caffeine, CaffeineCacheManager)`) → `type: caffeine` 을 적어 두면 대신 기동에서 멈춘다(`No cache manager could be auto-configured, check your configuration (caching type is 'CAFFEINE')`) ② `cache-names` 를 적으면 이름이 고정돼(`dynamic=false`) `@Cacheable` 이름 오타가 `Cannot find cache named` 로 드러난다 ③ `@EnableCaching` 이 없으면 캐시 자동 설정 전체가 꺼지고(`@ConditionalOnBean(CacheAspectSupport)`) `@Cacheable` 은 **오류 없이 무시된다** — 이건 캐시 테스트가 잡는다
- **`sync = true`** — 만료 순간 동시에 들어온 요청이 전부 `COUNT` 를 돌리는 것(캐시 쇄도)을 하나로 줄인다(측정은 안 함)
- **`expireAfterWrite`** — `expireAfterAccess` 는 읽을 때마다 시간이 다시 시작돼, 계속 읽히면 영영 갱신되지 않는다
- 캐시 키는 매개변수 값(`SimpleKeyGenerator` — 매개변수 하나면 그 값) → 카테고리마다 따로 저장된다. 허용 목록 덕에 칸 수가 늘지 않는다

**측정** — 5단계(캐시 전): ⑦ `6` → ① POST → ⑦ `7` · ⑧⑨⑩ 400. 6-3(캐시 후): 11:22:00 `8`(저장) → POST(DB 9) → 11:22:08 `8`(적중) → 11:22:11 `9`(만료) — `@EnableCaching`·`@Cacheable`·Caffeine·`expireAfterWrite`·10초를 한 번에 확인

**테스트** — `ParticipantLogControllerTest`
- `@BeforeEach` 캐시 비우기 — **캐시는 테스트 롤백에 묶이지 않는다**. 롤백된 행까지 센 숫자가 다음 테스트로 넘어가지 않게. `getCache` 가 `@Nullable` 이라 `Objects.requireNonNull`
- 정상 → `200` · `{"count": DB 수}` 엄격 비교 — 정답은 `repository.countByCategorySlug("football")` 로 직접 구한다(같은 기준을 다른 경로로)
- 400 세 경우 — 없음(`@RequestParam` 필수) · `baseball` · 빈 값(`@Pattern`). 빈 값은 이름이 있어 필수 검사를 통과하고 `@Pattern` 에서 막힌다 → 허용 목록에 빈 갈래(`"football|"`)가 생기면 이 테스트만 잡는다
- 캐시 — GET(N) → POST → GET(**N** = 적중) → 캐시 비우기 → GET(N+1). **10초 만료는 테스트하지 않는다** — `sleep` 은 느리고 시각에 흔들린다. 만료 자체는 Caffeine 의 일이고, 10초 설정은 6-3 에서 손으로 쟀다
- 빨간불 확인(2026-11-03): `@Cacheable` 을 막으면 캐시 테스트만 실패 — `count Expected: 9 got: 10`
- `./gradlew clean build` — 12건 · `8 executed` · jar `BOOT-INF/lib/` 에 `caffeine-3.2.4` · `spring-context-support-7.0.8` · `spring-boot-cache-4.1.0`. `.env` 는 jar 에 없다

**알게 된 것**
- ⚠️ **허용 목록 `"football"` 이 두 곳에 있다** — DTO `ParticipantLogRequest`(POST) 와 Controller `count` 의 `@Pattern`. 카테고리를 추가할 때 같이 고친다
- 쿼리 값 검사는 **매개변수에 `@Pattern` 을 직접** 붙인다(Spring 6.1+ 내장 메서드 검사 → `HandlerMethodValidationException` → 400). **클래스 수준 `@Validated` 는 붙이지 않는다** — 다른 검사 경로(AOP)가 켜진다
- `@RequestParam` 이 찾는 이름은 Java 매개변수 이름 — `-parameters` 로 컴파일돼 `.class` 에 `MethodParameters` 가 있다(javap 확인). **매개변수 이름이 곧 API 계약**
- **400 이라고 다 로그에 남지는 않는다** — `categorySlug` 없음은 WARN(`MissingServletRequestParameterException`)이 찍히지만, `baseball`·빈 값은 아무것도 안 찍힌다. `HandlerMethodValidationException` 이 `ResponseStatusException` 을 상속해서, 로그를 남기지 않는 `ResponseStatusExceptionResolver` 가 먼저 처리한다(7.0.8 javap). **서버 로그 없는 400 = 허용 목록일 가능성**
- 텍스트 블록 `{"count": %d}` 의 `%d` 에 따옴표를 두면 문자열 `"9"` 가 되어 엄격 비교가 실패한다
- `.http` 의 POST 는 롤백되지 않고 로컬 DB 에 쌓인다 — 2026-11-03 `jun98` PC 기준 football 9행

**F6-4 뒤 선택 과제 — Redis 캐시 실습 (2026-11-03 사용자 요청 · 학습용)**

- **왜 MVP 는 Caffeine 인가** — 백엔드가 한 대(D1)라 Redis 의 장점(서버 여러 대가 같은 값 공유)이 쓸모없다. 여러 대여도 이 숫자는 서버마다 최대 10초 달라도 괜찮다. Redis 는 인프라가 하나 늘고, **Redis 가 죽으면 Spring 캐시 기본 오류 처리가 예외를 그대로 던져 API 가 500** 이 된다
- **실습 내용** — `@Cacheable` 코드는 그대로 두고 **저장소만 바꾼다**(틀/저장소 분리 확인). `build.gradle` 에 `spring-boot-starter-data-redis`(Boot 4.1 BOM 에 있음) · `spring.cache.type: redis` · `spring.cache.redis.time-to-live: 10s`(4.0.0 jar 에서 키 확인) · 접속 주소는 DB 처럼 환경변수로 — 접속 설정 키는 그때 4.1.0 jar 로 확인
- **실행** — Redis 는 Windows 공식 지원이 없다 → Docker(`jun98` PC 에 28.5.2 있음 · 2026-11-03): `docker run -d --name redis -p 6379:6379 redis`. 개인 학습은 무료(Redis 8 은 AGPLv3 선택 가능 · 오픈소스 갈래 Valkey 도 있음)
- **측정** — 6-3 과 같은 `.http` 측정(10초 안엔 안 바뀌고 지나면 바뀌는지) + Redis 를 끄면 무슨 일이 나는지. 캐시 테스트(`count_is_cached_until_cache_is_cleared`)는 저장소와 상관없이 그대로 통과해야 한다
- **경계** — 실습 브랜치에서 한다. `main` 에 넣을지는 그때 결정 — 기본은 Caffeine 유지. 테스트도 Redis 가 떠 있어야 돈다는 점에 주의

### ✅ F6-3 `POST /api/participants` — 2026-10-29 (PR #3·#4·#5 · `8e640af`)

**주소에 버전을 넣지 않는다** (2026-10-28 · 프론트 ARCHITECTURE §6) — 베이스 `/api`. 깨지는 변경은 백엔드를 먼저 배포해 옛 모양·새 모양을 둘 다 지원 → 프론트 배포 → 옛 모양 제거 순서로 한다.

**계약의 원본은 프론트 ARCHITECTURE §6** (2026-10-27 확정). 백엔드가 알아야 할 것만:

- `categorySlug` 는 **서버 허용 목록**(MVP: `football`)으로 검사 · `resultSlug` 는 **형식만** 검사(15팀 목록 대조 ✗) — 어기면 400
- ⚠️ **`answerCode` 에는 `@Pattern` 을 걸지 않는다** — 걸면 요청 전체가 400 이 되어 "형식이 틀려도 참여 기록은 남긴다"(§5.2)를 어긴다. Service 에서 범용 형식(숫자 1~32자)만 보고 틀리면 `null` 로 저장. 축구 9자리 규칙은 Java 에 복사하지 않는다(원본 `lib/answer-code.ts`)
- 성공은 **`201` 본문 없음** · **`id` 를 응답에 넣지 않는다** — 프론트가 쓰지 않는다
- 알고 받아들인 위험: 서버 쪽 중복·남용 방어 없음(중복 차단은 프론트 `sessionStorage` 뿐 · §4.4)

**테스트 방식** (이후 슬라이스도 같다)
- `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvcTester` + `@Transactional` — 실제 로컬 MySQL 에 붙고 테스트마다 롤백(테스트 전용 DB 는 MVP 에 과하다). 테스트 메서드 하나 = `http/participants.http` 요청 하나
- **기존 행에 기대지 않는다** — 로컬 DB 마다 행이 다르다. 행 수는 전후 차이로, 방금 저장한 행은 `repository.findAll(Sort.by(Sort.Direction.DESC, "id")).getFirst()` 로 본다
- 확인한 API (spring-test 7.0.8 · AssertJ 3.27.7): `assertThat(mvc.post().uri(…).contentType(MediaType.APPLICATION_JSON).content("""…"""))` 가 요청을 실행하고 `.hasStatus(HttpStatus.CREATED)` · `.hasStatusOk()` · `.headers().hasValue("Allow", "POST")` · `.bodyJson().isStrictlyEqualTo(…)` 로 검사한다. `assertThat` 은 `org.assertj.core.api.Assertions`
- **빨간불 확인** — 통과한 테스트가 정말 잡아내는지, 지키는 코드를 잠깐 막고 실패를 본 뒤 되돌린다(F6-4 캐시 테스트에서 함 · F6-3 `@Valid` 는 2026-10-29 사용자 결정으로 건너뜀)

### ✅ F6-2 `participant_logs` 엔티티 · Repository — 2026-10-27 (PR #2 · `5559dd6`)

**설계 원본** — 프론트 `docs/ARCHITECTURE.md` §5.2. 코드는 `participant/` 폴더가 원본이다.

- **기능별 패키지** — `participant/` 에 엔티티 · Repository · Service · Controller (`domain/` · `repository/` 같은 계층별 폴더 안 씀). Spring Boot 문서 "Structuring Your Code" 예시가 기능별이고, 두 테이블이 외래키 없이 독립이다. 패키지는 DB 와 무관해서 나중에 바꿔도 된다
- **5칸** — `answer_code VARCHAR(32)` NULL 허용. 분석용 · 기록하지 않은 답은 되살릴 수 없다 · 백엔드는 해석하지 않고 보관만 한다
- **`ddl-auto`** = `${NEWBEEZ_DDL_AUTO:validate}` — 기본은 `validate`, 로컬 `.env` 에서만 `update`. 값을 안 준 환경(배포)에서 테이블이 멋대로 바뀌지 않게
- **`created_at`** = 자바 `Instant` → **UTC 로 저장** — **Hibernate 가 UTC 달력을 붙여 JDBC 에 넘기기 때문**이다(드라이버가 바꾸는 게 아니다 · 2026-11-02 노트북 정정). 2026-10-26 측정: Java `07:57:29.28Z` = DB `07:57:29` = 로컬 KST 16:57:29 − 9시간. `datetime(0)` 이라 소수점 아래는 반올림 · Workbench 에서는 9시간 이르게 보인다
- **생성자** — `public` 3개짜리(새 기록용 — `id` 는 안 받고 `createdAt` 은 안에서 `Instant.now()`) + `protected` 빈 생성자(Hibernate 가 행을 객체로 복원할 때 · 프록시가 자식 클래스라 `private` 불가)
- **setter 없음** — getter 5개만. 참여 기록은 쌓기만 한다(append-only)
- **Lombok 은 이 엔티티에 쓰지 않는다** — 무엇을 줄여 주는지 한 번은 직접 써 봐야 안다. F7 `team_results`(30칸)에서 도입
- 이때 만든 `ParticipantLogRepositoryTest` 는 2026-10-29 삭제(`6489263`) — F6-3 Controller 테스트가 HTTP 부터 DB 까지 같은 것을 본다. ⚠️ 그 테스트가 실제로 확인한 것은 **INSERT 성공과 id 생성뿐**이었다(UTC·인덱스를 본 게 아님 · 노트북 정정). 내용은 `git show e8b1d13:src/test/java/com/newbeez/newbeezback/participant/ParticipantLogRepositoryTest.java`

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

### ✅ F6-1 DB 설정 — 2026-10-06 (`44e2283`)

**접속 정보는 레포 밖에 있다.** `application.yml` 에는 빈칸 3개(`${NEWBEEZ_DB_URL}` · `_USERNAME` · `_PASSWORD`)만 있고, 로컬 값은 프로젝트 루트 **`.env`**(git 무시)에서 `spring.config.import` 로 읽는다. 배포에서는 `.env` 없이 같은 이름의 환경변수를 준다(환경변수가 우선).

**PC별 준비 상태** — DB·계정·`.env` 는 git 으로 오가지 않는다. PC마다 한 번씩 만든다.

| | `jun98` (데스크톱) | `이현준` |
| --- | --- | --- |
| MySQL | ✅ 8.0.46 (`MySQL80`) | ✅ 8.0.46 (`MySQL80` · 2026-11-02 실행 중 확인) |
| DB `newbeez` | ✅ | ✅ (2026-09-04 기록) |
| 앱 전용 계정 `newbeez` | ✅ | ✅ (2026-10-20 `bootRun` 접속 성공) |
| `.env` | ✅ | ✅ (2026-10-20 · 2026-11-02 있음 확인) |
| `.env` 의 `NEWBEEZ_DDL_AUTO=update` | ✅ (2026-10-14) | ✅ (2026-10-20 · `SHOW CREATE TABLE` 이 F6-2 의 SQL 과 같음) |

**⬜ 인 PC에서 할 일**

1. MySQL 에 root 로 접속해 실행 — 앱은 root 가 아니라 `newbeez` DB만 만질 수 있는 계정으로 붙는다
   ```sql
   CREATE DATABASE newbeez;   -- 이미 있으면 생략
   CREATE USER 'newbeez'@'localhost' IDENTIFIED BY '비밀번호';
   GRANT ALL PRIVILEGES ON newbeez.* TO 'newbeez'@'localhost';
   ```
2. `.env.example` 을 같은 폴더에 `.env` 로 복사하고 DB 값 3개를 채운다
   - `NEWBEEZ_DDL_AUTO=update` 줄은 **켜 둔다** — 켜져 있어야 엔티티대로 테이블이 생긴다(F6.5 Flyway 전환 때 지운다)
3. `./gradlew bootRun` → `Started NewbeezBackApplication` 이 찍히면 성공. 확인은 `http/participants.http` 의 ⑦(참여자 수)

### 🧹 2026-09-04 — 레거시 폐기

7월에 만든 `docs/`·`data/`·`Category.java`·`application.yml` 은 *백엔드가 채점하던 시절*의 설계(문항 10개 · 유클리드 · 백엔드 채점 · 테이블 4개)라 **전부 버리고** 스캐폴드 커밋(`03f9357`)으로 되돌렸다. 🔑 **교훈 — 설계를 두 레포에 복사해두면 한쪽만 낡는다** → [CLAUDE.md](CLAUDE.md) "여기에 설계를 복사하지 말 것". 15팀 좌표는 여전히 유효하고 원본은 프론트 `lib/clubs.ts` 다(백엔드는 좌표를 쓰지 않아 사본을 두지 않는다).

---

## ⬜ F6.5 Flyway (배포 전) + F7 `team_results` (배포·연동 뒤) — 설계 확정 (2026-10-20)

⚠️ **F6.5 함정 — 로컬 테이블은 Hibernate(`ddl-auto=update`)가 만들었다.** Flyway `V1__participant_logs.sql` 의 `create table` 이 "이미 있음"으로 실패한다 → 로컬 `participant_logs` 를 지우고 Flyway 가 만들게 하거나 baseline 설정. PC 마다 한 번씩(DB 가 PC 별로 따로라서)

**⏸ `V1` 을 쓰기 전에 정할 것** (노트북 2026-10-27·11-02 · 사용자 결정 대기)
- `createdAt` 을 Java 에서 **초 단위로 자를지** — `datetime(0)` 이 반올림하므로 저장한 Java 객체의 값과 DB 에 들어간 값의 초가 다를 수 있다
- **`(category_slug, created_at)` 인덱스**를 둘지 — 지금은 `(category_slug, result_slug)` · `(created_at)` 두 개

**스키마·칸 설명·검증 규칙의 원본은 프론트 ARCHITECTURE §5.1** (여기에 복사하지 않는다). 백엔드가 알아야 할 것만:

- **읽기 전용 테이블.** 엔티티는 setter 없음 + `@org.hibernate.annotations.Immutable`(ORM 애노테이션 — "벤더 함수 금지" 규칙과 무관) · Repository 는 조회만. 배치·동기화 코드 없음 — 옛 "하이브리드(API 동기화 영역)" 설계는 폐기됐다
- **데이터는 Flyway 시드 파일이 진실** — `src/main/resources/db/migration/`: DDL 은 `V1__participant_logs.sql`·`V2__team_results.sql`, **15행은 `R__team_results_seed.sql`**(`DELETE … WHERE category_slug='football'; INSERT …`). DDL 과 DML 을 한 파일에 섞지 않는다(MySQL DDL 암묵적 커밋). 각 행 주석에 출처 URL·확인일. 벤더 구문(`ON DUPLICATE KEY UPDATE`) ✗
- **Flyway 도입과 동시에 `.env` 의 `NEWBEEZ_DDL_AUTO=update` 를 지운다** — Hibernate 가 Flyway 밖에서 스키마를 바꾸면 드리프트. `validate` 는 유지하되 엔티티 `@Column(length…)`·타입을 DDL 과 맞춰야 기동된다(`validate` 는 VARCHAR 길이는 검사하지 않음). 의존성 `spring-boot-starter-flyway` + `org.flywaydb:flyway-mysql`(Spring Boot 4.1 → Flyway 12.4). MySQL 8.4 는 문서 목록에 없지만 Redgate 가 지원 확인(2026-08) — 배포 DB 로 1회 검증
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

### JPA · DB

- ⚠️ **`import` 는 보이는 대로 직접 친다** — 이름이 같은 다른 타입이 있어 IntelliJ 자동 가져오기가 엉뚱한 것을 잡는다. 우리가 쓰는 것은 `jakarta.persistence.Id`(Spring Data 의 `@Id` 아님) · `org.springframework.transaction.annotation.Transactional`(`jakarta.transaction` 쪽 아님)
- ⚠️ **`update` 는 추가만 한다.** 필드 이름을 바꾸면 옛 컬럼이 남는다 → Workbench 에서 테이블을 지우고 다시 켠다. 인덱스는 만든다(`SHOW CREATE TABLE` 에 `KEY` 2줄). MySQL 은 `datetime(0)` 을 `datetime` 으로 표시한다 — `(6)` 이 보이면 `secondPrecision` 이 안 먹은 것
- ⚠️ **Hibernate 가 실행한 SQL 은 로그에 안 나온다** — `application.yml` 에 `show-sql` 이 없다. 테이블이 바뀌었는지는 Workbench 로 본다
- ⚠️ **auto_increment 번호는 롤백돼도 소비된다** — id 에 빈 번호가 생기는 게 정상이다. 참여자 수는 `COUNT(*)` 로 세고 `MAX(id)` 로 세지 않는다

### 테스트

- ⚠️ **테스트는 로컬 DB `newbeez` 에 실제로 붙는다**(`bootRun` 과 같은 DB). `@Transactional` 이라 행은 남지 않는다. Spring 은 테스트 트랜잭션의 시작·롤백을 기본 로그(INFO)에 찍지 않는다 — 롤백은 Workbench 에서 실행 전후 `COUNT(*)` 가 같은지로 확인한다
- ⚠️ **`@Transactional` 테스트 안에서 다시 읽은 값은 DB 값이 아니다** — 같은 트랜잭션이 들고 있는 객체가 그대로 돌아온다. DB 에 실제로 들어간 값(반올림된 시각 등)을 보려면 `flush` · `clear` 뒤에 읽는다(노트북 2026-10-27)
- ⚠️ **캐시는 롤백되지 않는다** — 앱 메모리라 테스트 트랜잭션 밖이다. 캐시를 쓰는 기능의 테스트는 시작 전에 캐시를 비운다(F6-4 `@BeforeEach`)
- 테스트 데이터는 **프론트가 실제로 만들 수 있는 값**으로 — 답코드는 `032102223` 같은 9자리(노트북 2026-10-27)
- ⚠️ **`@SpringBootTest` 없이 `@Autowired` 를 쓰면** IntelliJ 가 빨간 밑줄(`Autowired members must be defined in valid Spring bean`)을 띄우고, 실행하면 필드가 `null` 로 남아 **`NullPointerException`** 으로 실패한다(노트북 정정)
- ⚠️ **통과한 테스트는 콘솔에 아무것도 안 찍는다** — `BUILD SUCCESSFUL` 대신 `build/test-results/test/TEST-*.xml` 의 tests·failures·시각으로 확인한다. 로그·`println` 도 거기 있다. 한 클래스만 실행은 `./gradlew test --tests "com.newbeez.newbeezback.participant.ParticipantLogControllerTest"`
- ⚠️ **코드가 그대로면 Gradle 이 테스트를 건너뛴다**(`UP-TO-DATE`) — 그래도 `BUILD SUCCESSFUL` 이 뜬다. 마지막 줄 `N actionable tasks` 에 `executed` 가 있는지 본다. 다시 돌리려면 `--rerun`, PR 전에는 `./gradlew clean build`. IntelliJ 에서 고친 파일은 **저장해야** Gradle 이 본다
- ⚠️ **결과는 컴파일된 시점의 코드에 대한 것이다** — 소스를 고친 뒤에는 `build/classes/java/test/…/*.class` 시각이 소스 저장 시각보다 나중인지 본다
- **테스트 메서드 순서는 보장되지 않는다** — 작성 순서와 다르게 돈다. 테스트마다 롤백 + 전후 차이로 비교하면 순서와 무관하다
- **앱은 테스트 설정 조합 수만큼 뜬다** — `@SpringBootTest` 만 붙은 클래스끼리는 앱 하나를 공유하고, `@AutoConfigureMockMvc` 가 붙은 클래스는 따로 띄운다(결과 xml 의 `HikariPool-N` 2개)
- **MockMvc 의 400 응답은 본문이 비어 있다** — 실제 서버의 `{timestamp, status, error, path}` 는 Boot 가 `/error` 로 넘겨서 만든 것이고 MockMvc 는 넘기지 않는다. 어느 쪽이든 어느 검사에 걸렸는지는 본문으로 구별 못 한다 → 테스트는 상태 코드만 본다
- Spring Boot 4.1 의 테스트 애노테이션 패키지가 바뀌었다 — `@AutoConfigureMockMvc` 는 `org.springframework.boot.webmvc.test.autoconfigure`. JUnit 은 **Jupiter 6.0.3**(JUnit 6)

### IntelliJ

- ⚠️ **IntelliJ 의 빨간 표시는 판정 기준이 아니다** — 판정은 Gradle 출력과 결과 xml 로 한다. 실제로 본 것: F6-3 테스트의 빨간 표시(`compileTestJava` 는 성공 · 메시지는 확인 안 함) · **`Could not autowire. No beans of 'CacheManager' type found.`**(F6-4 — 실행하면 빈이 있다. IntelliJ 의 Gradle 모델에는 캐시 jar 가 들어 있어 Sync 문제가 아니다. Boot 가 `ImportSelector` 로 캐시 설정을 고르는 걸 정적 검사가 못 따라가는 것으로 추정). `jun98` PC 에서 괄호가 빨간 것은 ColorBrackets 플러그인 색이다
- IntelliJ 는 텍스트 블록 안에서도 `{` 를 치면 `}` 를 자동으로 넣는다 — 일부러 깨뜨린 JSON 은 친 뒤 확인한다

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

---

## 그 외

- **로컬 DB**: MySQL 8.0.x · `MySQL80` 서비스 · DB `newbeez` — **PC마다 따로 있고 내용이 오가지 않는다**(위 F6-1 표). ⚠️ 8.0 은 2026-04-30 에 지원이 끝난 버전이다. 로컬 학습용으론 문제없지만 **배포 DB는 8.4 이상**으로 고른다
- **배포처**: 1순위 Oracle Cloud Always Free 서울(A1 + MySQL HeatWave 50GB). **결정은 D1 에서** — A1 확보가 복불복이라 미리 시도 권장. 근거는 프론트 `docs/FUTURE.md` §4. ⚠️ **필수 조건(2026-10-27): 유휴 회수가 없을 것** — Always Free 그대로면 7일 유휴 시 회수되므로 PAYG 전환이나 다른 호스팅이 필요하다(F7 이후 결과 페이지 빌드가 백엔드에 의존)
- **D1 준비물** — Docker 28.5.2 가 `jun98` PC 에 있다(Dockerfile 시험 가능) · 레포에 Dockerfile 은 아직 없다 · `./gradlew build` 가 만드는 실행용 jar(`build/libs/newbeez-back-0.0.1-SNAPSHOT.jar` · 약 55MB · 라이브러리 포함)가 컨테이너에 넣을 파일이다. `-plain.jar` 는 우리 코드만이라 혼자 실행되지 않는다
- **락인 방어(지금부터 적용)**: 컨테이너화 · DB 접속은 환경변수로만 · Flyway · 트리거/프로시저 금지
- **Instagram 임베드 테스트 페이지** `C:\dev\embed-test\index.html` (`이현준` PC · git 밖) — 327px 본문에 게시물/캡션/계정 카드 3종을 띄우고 렌더 시간·크기를 재는 정적 HTML. `python -m http.server 8000 --bind 127.0.0.1 --directory C:\dev\embed-test` 로 열람. 2026-10-20 PC 크롬 확인 · 아이폰 카톡 인앱은 사용자가 확인함(노트북 기록 · 프론트 §9 #6 반영은 위 "프론트에 넘길 것")
