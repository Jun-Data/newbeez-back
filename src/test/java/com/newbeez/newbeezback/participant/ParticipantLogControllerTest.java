package com.newbeez.newbeezback.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ParticipantLogControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @Autowired
    private ParticipantLogRepository repository;
}
