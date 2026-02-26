package com.releasescribe.backend_api.project.dto;

import java.time.LocalDateTime;

public record ProjectResponse(
        Long id,
        String owner,
        String repo,
        String repoUrl,
        String tokenHint,
        LocalDateTime createdAt
) {}