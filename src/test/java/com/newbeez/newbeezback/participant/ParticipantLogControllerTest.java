package com.newbeez.newbeezback.participant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParticipantLogControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ParticipantLogRepository repository;

    @Autowired
    private CacheManager cacheManager;

    // 캐시는 테스트 롤백에 묶이지 않는다 - 앞 테스트가 센 숫자가 남지 않게 매번 비운다
    @BeforeEach
    void clearParticipantCountCache() {
        Objects.requireNonNull(cacheManager.getCache("participantCount")).clear();
    }

    @Test
    void valid_request_returns_201_and_saves_answer_code() {
        long before = repository.count();

        assertThat(mvc.post().uri("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"categorySlug": "football", "resultSlug": "man-city", "answerCode": "032102223"}
                        """))
                .hasStatus(HttpStatus.CREATED);

        assertThat(repository.count()).isEqualTo(before + 1);
        ParticipantLog saved = repository.findAll(Sort.by(Sort.Direction.DESC, "id")).getFirst();
        assertThat(saved.getAnswerCode()).isEqualTo("032102223");
    }

    @Test
    void missing_answer_code_returns_201_and_saves_null() {
        long before = repository.count();

        assertThat(mvc.post().uri("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"categorySlug": "football", "resultSlug": "man-city"}
                        """))
                .hasStatus(HttpStatus.CREATED);

        assertThat(repository.count()).isEqualTo(before + 1);
        ParticipantLog saved = repository.findAll(Sort.by(Sort.Direction.DESC, "id")).getFirst();
        assertThat(saved.getAnswerCode()).isNull();
    }

    @Test
    void invalid_answer_code_returns_201_and_saves_null() {
        long before = repository.count();

        assertThat(mvc.post().uri("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"categorySlug": "football", "resultSlug": "man-city", "answerCode": "abc"}
                        """))
                .hasStatus(HttpStatus.CREATED);

        assertThat(repository.count()).isEqualTo(before + 1);
        ParticipantLog saved = repository.findAll(Sort.by(Sort.Direction.DESC, "id")).getFirst();
        assertThat(saved.getAnswerCode()).isNull();
    }

    @Test
    void invalid_result_slug_returns_400_and_saves_nothing() {
        long before = repository.count();

        assertThat(mvc.post().uri("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"categorySlug": "football", "resultSlug": "MAN CITY", "answerCode": "032102223"}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST);

        assertThat(repository.count()).isEqualTo(before);
    }

    @Test
    void malformed_json_returns_400_and_saves_nothing() {
        long before = repository.count();

        assertThat(mvc.post().uri("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"categorySlug": "football", "resultSlug":
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST);

        assertThat(repository.count()).isEqualTo(before);
    }

    @Test
    void get_returns_405_with_allow_post() {
        assertThat(mvc.get().uri("/api/participants"))
                .hasStatus(HttpStatus.METHOD_NOT_ALLOWED)
                .headers().hasValue("Allow", "POST");
    }

    @Test
    void count_returns_200_with_db_count() {
        long expected = repository.countByCategorySlug("football");

        assertThat(mvc.get().uri("/api/participants/count").param("categorySlug", "football"))
                .hasStatusOk()
                .bodyJson().isStrictlyEqualTo("""
                        {"count": %d}
                        """.formatted(expected));
    }

    @Test
    void count_without_category_slug_returns_400() {
        assertThat(mvc.get().uri("/api/participants/count"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void count_with_unknown_category_slug_returns_400() {
        assertThat(mvc.get().uri("/api/participants/count").param("categorySlug", "baseball"))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void count_with_empty_category_slug_returns_400() {
        assertThat(mvc.get().uri("/api/participants/count").param("categorySlug", ""))
                .hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void count_is_cached_until_cache_is_cleared() {
        long before = repository.countByCategorySlug("football");
        assertFootballCount(before);

        assertThat(mvc.post().uri("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"categorySlug": "football", "resultSlug": "man-city", "answerCode": "032102223"}
                        """))
                .hasStatus(HttpStatus.CREATED);

        // DB 에는 한 명 늘었지만 캐시가 앞의 숫자를 돌려준다
        assertFootballCount(before);

        // 비우면 10초가 지난 것과 같다 - 다시 DB 에서 센다
        clearParticipantCountCache();
        assertFootballCount(before + 1);
    }

    private void assertFootballCount(long expected) {
        assertThat(mvc.get().uri("/api/participants/count").param("categorySlug", "football"))
                .hasStatusOk()
                .bodyJson().isStrictlyEqualTo("""
                        {"count": %d}
                        """.formatted(expected));
    }
}
