package com.onelab.users_service.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    @PrePersist
    private void prePersist() {
        setCreatedAt(LocalDateTime.now());
        setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PreUpdate
    private void preUpdate() {
        setUpdatedAt(LocalDateTime.now());
        setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
