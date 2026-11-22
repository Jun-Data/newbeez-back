-- 참여 기록. 원본: 프론트 ARCHITECTURE §5.2 (2026-11-09 인덱스·초 단위 결정 반영)
CREATE TABLE participant_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_slug VARCHAR(32) NOT NULL,
    result_slug VARCHAR(32) NOT NULL,
    answer_code VARCHAR(32),
    created_at DATETIME NOT NULL,
    INDEX idx_cat_slug (category_slug, result_slug),
    INDEX idx_cat_created (category_slug, created_at)
);