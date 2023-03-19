package com.auth.service.app.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.entity.CommonEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auth_roles")
public class Role extends CommonEntity {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SUPERADMIN = "ROLE_SUPERADMIN";

    @Column(name = "role_name", unique = true)
    private String roleName;
}
