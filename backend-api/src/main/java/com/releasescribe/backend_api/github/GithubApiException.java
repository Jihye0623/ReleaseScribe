package com.releasescribe.backend_api.github;

public class GithubApiException extends RuntimeException {
    private final int status;

    public GithubApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int status() {
        return status;
    }
}