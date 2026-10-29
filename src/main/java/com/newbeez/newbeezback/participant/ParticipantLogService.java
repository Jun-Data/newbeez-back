package com.newbeez.newbeezback.participant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipantLogService {
    private final ParticipantLogRepository repository;

    public ParticipantLogService(ParticipantLogRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void create(ParticipantLogRequest request) {
        ParticipantLog log = new ParticipantLog(
                request.categorySlug(),
                request.resultSlug(),
                normalizeAnswerCode(request.answerCode()));
        repository.save(log);
    }

    // 범용 형식(숫자 1~32자)만 본다 - 틀리면 null로 바꾸고 기록은 남긴다
    private static String normalizeAnswerCode(String answerCode) {
        if (answerCode == null || !answerCode.matches("[0-9]{1,32}")) {
            return null;
        }
        return answerCode;
    }
}
