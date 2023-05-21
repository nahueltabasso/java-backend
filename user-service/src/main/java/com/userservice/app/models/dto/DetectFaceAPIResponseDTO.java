package com.userservice.app.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetectFaceAPIResponseDTO {

    @JsonProperty("status")
    private int status;
    @JsonProperty("detected_face")
    private boolean detectedFace;
    @JsonProperty("bbox_faces")
    private int[][] bboxFaces;
    @JsonProperty("number_of_detected_faces")
    private int numberOfDetectedFaces;
    @JsonProperty("error_code")
    private String errorCode;
}
