package com.newbeez.newbeezback.participant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/participants")
public class ParticipantLogController {

    private final ParticipantLogService service;

    public ParticipantLogController(ParticipantLogService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody ParticipantLogRequest request) {
        service.create(request);
    }

    @GetMapping("/count")
    public ParticipantCountResponse count(@RequestParam @Pattern(regexp = "football") String categorySlug) {
        return new ParticipantCountResponse(service.count(categorySlug));
    }
}
