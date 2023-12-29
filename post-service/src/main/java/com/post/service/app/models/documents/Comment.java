package com.post.service.app.models.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("comments")
public class Comment {

    @Id
    private String id;
    private String comment;
    private LocalDateTime createAt;
    private Post post;
    private Long userProfileId;
    private Long userId;
}
