package com.releasescribe.backend_api.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubRepoResponse(
        String full_name,
        String name,
        String description,
        @JsonProperty("stargazers_count") int stargazersCount,
        @JsonProperty("forks_count") int forksCount,
        @JsonProperty("private") boolean isPrivate
) {}