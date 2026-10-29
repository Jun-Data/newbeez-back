package com.newbeez.newbeezback.participant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ParticipantLogControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ParticipantLogRepository repository;

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
}
