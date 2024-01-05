package com.post.service.app.models.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("posts")
public class Post {

    @Id
    private String id;
    @NotEmpty
    private String description;
    private List<String> imagesPaths;
    private List<String> publicsIds;
    @CreatedDate
    private LocalDateTime createAt;
    @NotNull
    private Long userProfileId;
    @NotNull
    private Long userId;

}
