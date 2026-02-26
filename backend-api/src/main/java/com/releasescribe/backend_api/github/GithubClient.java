package com.releasescribe.backend_api.github;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class GithubClient {

    private final RestClient client;

    public GithubClient(RestClient githubRestClient) {
        this.client = githubRestClient;
    }

    /**
     * 토큰 유효성/권한/레포 존재 여부 검증 겸 repo 기본정보 조회
     */
    public GithubRepoResponse getRepo(String owner, String repo, String token) {
        if (owner == null || owner.isBlank()) throw new IllegalArgumentException("owner is blank");
        if (repo == null || repo.isBlank()) throw new IllegalArgumentException("repo is blank");
        if (token == null || token.isBlank()) throw new IllegalArgumentException("token is blank");

        try {
            ResponseEntity<GithubRepoResponse> res = client.get()
                    .uri("/repos/{owner}/{repo}", owner, repo)
                    .header("Authorization", "Bearer " + token.trim())
                    .retrieve()
                    .toEntity(GithubRepoResponse.class);

            if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                return res.getBody();
            }
            throw new GithubApiException(res.getStatusCode().value(), "GitHub API unexpected response");
        } catch (RestClientResponseException e) {
            int code = e.getStatusCode().value();

            // 401/403/404를 MVP에서 구분해서 메시지 주면 UX 좋아짐
            if (code == HttpStatus.UNAUTHORIZED.value() || code == HttpStatus.FORBIDDEN.value()) {
                throw new GithubApiException(code, "Token invalid or insufficient permission (401/403)");
            }
            if (code == HttpStatus.NOT_FOUND.value()) {
                throw new GithubApiException(code, "Repository not found (404). Wrong owner/repo or no access to private repo.");
            }

            throw new GithubApiException(code, "GitHub API error: " + code);
        }
    }
}