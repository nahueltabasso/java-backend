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
@Table(name = "auth_password_reset")
public class PasswordReset extends CommonEntity {

    public static final int EXPIRATION = 60 * 24;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.creationUser = "admin";
            this.creationDateTime = LocalDateTime.now();
            this.modificationUser = "admin";
            this.modificationDateTime = LocalDateTime.now();
            this.version = 0;
            this.deleted = false;
        } else {
            this.creationUser = "admin";
            this.creationDateTime = this.creationDateTime;
            this.modificationUser = "admin";
            this.modificationDateTime = LocalDateTime.now();
            this.version = this.version++;
            this.deleted = this.deleted;
        }
    }
}
