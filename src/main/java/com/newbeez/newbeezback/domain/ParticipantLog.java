package com.newbeez.newbeezback.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "participant_logs", indexes = {
        @Index(name = "idx_cat_slug", columnList = "category_slug, result_slug"),
        @Index(name = "idx_created", columnList = "created_at")
})
public class ParticipantLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String categorySlug;

    @Column(nullable = false, length = 32)
    private String resultSlug;

    @Column(length = 32)
    private String answerCode;

    @Column(nullable = false, secondPrecision = 0)
    private Instant createdAt;

    protected ParticipantLog() {
    }

    public ParticipantLog(String categorySlug, String resultSlug, String answerCode) {
        this.categorySlug = categorySlug;
        this.resultSlug = resultSlug;
        this.answerCode = answerCode;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public String getResultSlug() {
        return resultSlug;
    }

    public String getAnswerCode() {
        return answerCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

}
