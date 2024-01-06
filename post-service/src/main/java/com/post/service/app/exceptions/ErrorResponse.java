package com.post.service.app.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private Integer httpStatus;
    private LocalDateTime timestamp;
    private String errorCode;
    private String message;
    private String description;

}
