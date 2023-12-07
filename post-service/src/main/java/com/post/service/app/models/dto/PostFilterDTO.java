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
public class PostFilterDTO {

    private Long userProfileId;
    private List<Long> userProfileIds;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
}
