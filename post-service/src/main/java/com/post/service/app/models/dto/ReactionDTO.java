package com.post.service.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactionDTO {

    private String id;
    private String postId;
    private Long userProfileId;
    private Long userId;
    private LocalDateTime createAt;
}
