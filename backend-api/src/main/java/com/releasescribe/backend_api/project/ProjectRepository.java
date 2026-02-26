package com.releasescribe.backend_api.project;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByOwnerAndRepo(String owner, String repo);
}