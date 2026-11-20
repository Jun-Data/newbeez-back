package com.newbeez.newbeezback;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// 브라우저 흉내 - Origin 헤더를 붙여 보내고 응답 "헤더"를 본다
// 허용 출처는 이 테스트가 정한다 - PC 마다 다른 .env 값에 기대지 않는다
@SpringBootTest(properties = "newbeez.cors.allowed-origins=" + CorsConfigTest.ALLOWED)
@AutoConfigureMockMvc
@Transactional
class CorsConfigTest {
    static final String ALLOWED = "http://localhost:3000";
    static final String OTHER = "http://localhost:3001";

    @Autowired
    private MockMvcTester mvc;

    // 허용 출처에서 GET count
    @Test
    void allowed_origin_gets_allow_origin_header() {
        assertThat(mvc.get().uri("/api/participants/count")
                .param("categorySlug", "football")
                .header(HttpHeaders.ORIGIN, ALLOWED))
                .hasStatusOk()
                .headers().hasValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED);
    }

    // 허용 안 된 출처에서 GET count
    @Test
    void other_origin_is_rejected() {
        assertThat(mvc.get().uri("/api/participants/count")
                .param("categorySlug", "football")
                .header(HttpHeaders.ORIGIN, OTHER))
                .hasStatus(HttpStatus.FORBIDDEN)
                .headers().doesNotContainHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    // 사전 요청 - 브라우저가 JSON POST 전에 먼저 보낸다
    @Test
    void preflight_for_post_returns_allow_headers() {
        assertThat(mvc.options().uri("/api/participants")
                .header(HttpHeaders.ORIGIN, ALLOWED)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .hasStatusOk()
                .headers()
                .hasValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED)
                .hasValue(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST")
                .hasValue(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "content-type");
    }

    // POST - 사전 요청이 통과한 뒤 브라우저가 보낸다
    @Test
    void post_from_allowed_origin_gets_allow_origin_header() {
        assertThat(mvc.post().uri("/api/participants")
                .header(HttpHeaders.ORIGIN, ALLOWED)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"categorySlug": "football", "resultSlug":"man-city", "answerCode":"032102223"}
                        """))
                .hasStatus(HttpStatus.CREATED)
                .headers().hasValue(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED);
    }
}
