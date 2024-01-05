package com.post.service.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    private String id;
    private String description;
    private List<String> imagesPaths;
    private List<String> publicsIds;
    private LocalDateTime createAt;
    private Long userProfileId;
    private Long userId;
    private UserDTO userDTO;
}
