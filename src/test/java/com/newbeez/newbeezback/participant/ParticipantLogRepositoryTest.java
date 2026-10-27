package com.newbeez.newbeezback.participant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
class ParticipantLogRepositoryTest {

    @Autowired
    private ParticipantLogRepository repository;

    @Test
    void save_assigns_id() {
        ParticipantLog log = new ParticipantLog("football", "man-city", "032104213");
        assertNull(log.getId());

        ParticipantLog saved = repository.save(log);

        assertNotNull(saved.getId());
        System.out.println("saved id=" + saved.getId() + " createdAt=" + saved.getCreatedAt());
    }
}
