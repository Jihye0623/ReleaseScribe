package com.releasescribe.backend_api.github;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class GithubRepoUrlParserTest {
    @Test
    void parse_https_url() {
        OwnerRepo r = GithubRepoUrlParser.parse("https://github.com/OWNER/REPO");
        assertEquals("OWNER", r.owner());
        assertEquals("REPO", r.repo());
        assertEquals("OWNER/REPO", r.fullName());
    }

    @Test
    void parse_https_url_with_dot_git() {
        OwnerRepo r = GithubRepoUrlParser.parse("https://github.com/OWNER/REPO.git");
        assertEquals("OWNER", r.owner());
        assertEquals("REPO", r.repo());
    }

    @Test
    void parse_https_url_with_trailing_slash() {
        OwnerRepo r = GithubRepoUrlParser.parse("https://github.com/OWNER/REPO/");
        assertEquals("OWNER", r.owner());
        assertEquals("REPO", r.repo());
    }

    @Test
    void parse_ssh_url() {
        OwnerRepo r = GithubRepoUrlParser.parse("git@github.com:OWNER/REPO.git");
        assertEquals("OWNER", r.owner());
        assertEquals("REPO", r.repo());
    }

    @Test
    void parse_owner_repo() {
        OwnerRepo r = GithubRepoUrlParser.parse("OWNER/REPO");
        assertEquals("OWNER", r.owner());
        assertEquals("REPO", r.repo());
    }

    @Test
    void parse_protocol_less_github_url() {
        OwnerRepo r = GithubRepoUrlParser.parse("github.com/OWNER/REPO");
        assertEquals("OWNER", r.owner());
        assertEquals("REPO", r.repo());
    }

    @Test
    void reject_blank() {
        assertThrows(IllegalArgumentException.class, () -> GithubRepoUrlParser.parse(" "));
        assertThrows(IllegalArgumentException.class, () -> GithubRepoUrlParser.parse(null));
    }

    @Test
    void reject_invalid_format() {
        assertThrows(IllegalArgumentException.class, () -> GithubRepoUrlParser.parse("OWNER"));
        assertThrows(IllegalArgumentException.class, () -> GithubRepoUrlParser.parse("OWNER/REPO/EXTRA"));
    }

    @Test
    void reject_non_github_url() {
        assertThrows(IllegalArgumentException.class, () -> GithubRepoUrlParser.parse("https://gitlab.com/OWNER/REPO"));
    }
}