package com.newbeez.newbeezback.participant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ParticipantLogRequest(
        @NotBlank
        @Pattern(regexp = "football")
        String categorySlug,

        @NotBlank
        @Pattern(regexp = "[a-z0-9-]{1,32}")
        String resultSlug,

        String answerCode) {
}
