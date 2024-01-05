package com.post.service.app.models.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("reactions")
public class Reaction {

    @Id
    private String id;
    private String postId;
    private Long userProfileId;
    private Long userId;
    private LocalDateTime createAt;
}
