package com.releasescribe.backend_api.github;

import java.net.URI;
import java.net.URISyntaxException;

public final class GithubRepoUrlParser {

    private GithubRepoUrlParser() {}

    /**
     * 지원 입력:
     * - https://github.com/OWNER/REPO
     * - https://github.com/OWNER/REPO.git
     * - git@github.com:OWNER/REPO.git
     * - OWNER/REPO
     */
    public static OwnerRepo parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("repoUrl is blank");
        }

        String s = input.trim();

        // 1) SSH: git@github.com:OWNER/REPO(.git)
        //    또는 github.com:OWNER/REPO(.git) 형태를 처리
        if (s.startsWith("git@github.com:") || s.startsWith("github.com:")) {
            String path = s.substring(s.indexOf(':') + 1);
            return parseOwnerRepoPath(path);
        }

        // 2) URL: http(s)://github.com/OWNER/REPO(.git)
        if (s.startsWith("http://") || s.startsWith("https://")) {
            return parseFromHttpUrl(s);
        }

        // 3) OWNER/REPO
        //    혹시 "github.com/OWNER/REPO" 처럼 프로토콜 없는 입력도 들어올 수 있어서 보정
        if (s.startsWith("github.com/")) {
            s = "https://" + s;
            return parseFromHttpUrl(s);
        }

        return parseOwnerRepoPath(s);
    }

    private static OwnerRepo parseFromHttpUrl(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null || !host.equalsIgnoreCase("github.com")) {
                throw new IllegalArgumentException("Not a github.com url: " + url);
            }

            String path = uri.getPath(); // e.g. /OWNER/REPO or /OWNER/REPO/
            if (path == null) {
                throw new IllegalArgumentException("Invalid github url path: " + url);
            }

            // leading "/" 제거
            if (path.startsWith("/")) path = path.substring(1);

            // "OWNER/REPO/..." 혹시 더 붙으면 앞의 2세그먼트만 사용 (issues 등)
            // 하지만 최소 구현에선 2세그먼트만 허용해도 됨. 여기선 유연하게.
            String[] parts = path.split("/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid github repo url (need owner/repo): " + url);
            }

            String owner = parts[0];
            String repo = stripDotGit(parts[1]);
            return new OwnerRepo(owner, repo);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid url: " + url, e);
        }
    }

    private static OwnerRepo parseOwnerRepoPath(String path) {
        if (path == null) throw new IllegalArgumentException("path is null");

        String p = path.trim();

        // 혹시 /OWNER/REPO 로 들어오면 앞의 "/" 제거
        while (p.startsWith("/")) p = p.substring(1);

        // query/fragments 제거 (혹시 붙는 경우)
        int q = p.indexOf('?');
        if (q >= 0) p = p.substring(0, q);
        int hash = p.indexOf('#');
        if (hash >= 0) p = p.substring(0, hash);

        // 뒤에 "/"가 붙으면 제거
        while (p.endsWith("/")) p = p.substring(0, p.length() - 1);

        String[] parts = p.split("/");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected OWNER/REPO format, got: " + path);
        }

        String owner = parts[0];
        String repo = stripDotGit(parts[1]);
        return new OwnerRepo(owner, repo);
    }

    private static String stripDotGit(String repo) {
        String r = repo.trim();
        if (r.endsWith(".git")) {
            r = r.substring(0, r.length() - 4);
        }
        return r;
    }
}