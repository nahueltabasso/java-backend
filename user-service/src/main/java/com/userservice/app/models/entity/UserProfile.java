package com.userservice.app.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.entity.CommonEntity;
import nrt.common.microservice.security.session.AppSessionUser;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_users_profiles")
public class UserProfile extends CommonEntity {

    @Column(name = "first_name")
    @NotBlank
    private String firstName;
    @Column(name = "last_name")
    @NotBlank
    private String lastName;
    @Column(name = "email")
    @Email
    @NotBlank
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "profile_photo")
    @NotBlank
    private String profilePhoto;
    @Column(name = "birth_date")
    @NotNull
    private LocalDateTime birthDate;
    @Column(name = "verified_profile")
    @NotNull
    private Boolean verifiedProfile;
    @Column(name = "personal_status")
    private String personalStatus;
    @Column(name = "studies")
    private String studies;
    @Column(name = "biography")
    private String biography;
    @Column(name = "user_id")
    @NotNull
    private Long userId;
    @Column(name = "active_profile")
    private Boolean activeProfile;

    @PrePersist
    public void prePersist() {
        // First time to persist in database (create operation)
        this.creationDateTime = LocalDateTime.now();
        this.creationUser = "admin";
        this.modificationDateTime = LocalDateTime.now();
        this.modificationUser = "admin";
        this.version = 0;
        this.deleted = false;
    }

    @PreUpdate
    public void preUpdate() {
        // Update operation
        this.creationDateTime = this.creationDateTime;
        this.creationUser = this.creationUser;
        this.modificationDateTime = LocalDateTime.now();
        this.modificationUser = AppSessionUser.getCurrentAppUser().getUsername();
        this.version = this.version + 1;
        this.deleted = this.deleted;
    }
}
