package com.releasescribe.backend_api.project.dto;

public record CreateProjectRequest(
        String repoUrl,
        String token
) {}