package com.auth.service.app.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.entity.CommonEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auth_user_role")
public class UserRole extends CommonEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void prePersist() {
        this.creationUser = "admin";
        this.creationDateTime = LocalDateTime.now();
        this.modificationUser = "admin";
        this.modificationDateTime = LocalDateTime.now();
        this.version = 0;
        this.deleted = false;
    }
}
