package com.releasescribe.backend_api.project;

import com.releasescribe.backend_api.project.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public ProjectResponse create(@RequestBody CreateProjectRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ProjectResponse> list() {
        return service.findAll();
    }
}