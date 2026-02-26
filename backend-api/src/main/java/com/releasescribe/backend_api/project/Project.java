package com.releasescribe.backend_api.project;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects",
        uniqueConstraints = @UniqueConstraint(columnNames = {"owner", "repo"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String repoUrl;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private String repo;

    @Column(nullable = false, columnDefinition = "text")
    private String tokenEnc;

    @Column(nullable = false)
    private String tokenHint;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    public Project(String owner, String repo, String repoUrl, String tokenEnc) {
        this.owner = owner;
        this.repo = repo;
        this.repoUrl = repoUrl;
        this.tokenEnc = tokenEnc;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getOwner() { return owner; }
    public String getRepo() { return repo; }
    public String getRepoUrl() { return repoUrl; }
    public String getTokenEnc() { return tokenEnc; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
