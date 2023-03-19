package com.auth.service.app.models.entity;

import annotations.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.entity.CommonEntity;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auth_users")
public class User extends CommonEntity {

    @Column(name = "username", unique = true)
    @NotNull
    @NotEmpty
    private String username;
    @Column(name = "password")
    @Password
    private String password;
    @Column(name = "email", unique = true)
    @NotEmpty
    @Email
    private String email;
    @Column(name = "google_user")
    @NotNull
    private Boolean googleUser;
    @Column(name = "facebook_user")
    @NotNull
    private Boolean facebookUser;
    @Column(name = "apple_user")
    @NotNull
    private Boolean appleUser;
    @Column(name = "first_login")
    private Boolean firstLogin;
    @Column(name = "user_locked")
    private Boolean userLocked;
    @Column(name = "fails_attemps")
    private int failsAttemps;
    @Transient
    private List<UserRole> userRoles;
}
