package com.userservice.app.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nrt.common.microservice.models.entity.CommonEntity;
import nrt.common.microservice.security.session.AppSessionUser;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_friend_requests")
public class FriendRequest extends CommonEntity {

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private UserProfile fromUser;
    @Column(name = "from_email")
    private String fromEmail;
    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private UserProfile toUser;
    @Column(name = "to_email")
    private String toEmail;
    @Column(name = "status")
    private Boolean status;

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
