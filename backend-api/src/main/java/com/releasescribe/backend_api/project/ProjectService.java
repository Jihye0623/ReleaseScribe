package com.releasescribe.backend_api.project;

import com.releasescribe.backend_api.crypto.CryptoService;
import com.releasescribe.backend_api.github.*;
import com.releasescribe.backend_api.project.dto.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final GithubClient githubClient;
    private final CryptoService cryptoService;

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {

        OwnerRepo or = GithubRepoUrlParser.parse(request.repoUrl());

        if (repository.existsByOwnerAndRepo(or.owner(), or.repo())) {
            throw new IllegalArgumentException("Project already registered");
        }

        // GitHub API 검증
        GithubRepoResponse repoInfo =
                githubClient.getRepo(or.owner(), or.repo(), request.token());

        // 토큰 암호화
        String encrypted = cryptoService.encryptToBase64(request.token());

        Project project = new Project(
                or.owner(),
                or.repo(),
                request.repoUrl(),
                encrypted
        );

        repository.save(project);

        return new ProjectResponse(
                project.getId(),
                project.getOwner(),
                project.getRepo(),
                project.getRepoUrl(),
                CryptoService.tokenHint(request.token()),
                project.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(p -> new ProjectResponse(
                        p.getId(),
                        p.getOwner(),
                        p.getRepo(),
                        p.getRepoUrl(),
                        "****", // 절대 토큰 반환 금지
                        p.getCreatedAt()
                ))
                .toList();
    }
}