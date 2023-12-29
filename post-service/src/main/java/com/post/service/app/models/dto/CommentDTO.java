package com.post.service.app.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("posts")
public class CommentDTO {

    private String id;
    private String comment;
    private LocalDateTime createAt;
    private PostDTO postDTO;
    private Long userProfileId;
    private Long userId;
    private UserDTO userDTO;
}
