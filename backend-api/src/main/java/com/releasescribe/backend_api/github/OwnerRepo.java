package com.releasescribe.backend_api.github;
import java.util.Objects;

public record OwnerRepo (String owner, String repo){
    public OwnerRepo {
        if (owner == null || owner.isBlank()) throw new IllegalArgumentException("owner is blank");
        if (repo == null || repo.isBlank()) throw new IllegalArgumentException("repo is blank");
    }

    public String fullName() {
        return owner + "/" + repo;
    }

    @Override
    public String toString() {
        return fullName();
    }
}
