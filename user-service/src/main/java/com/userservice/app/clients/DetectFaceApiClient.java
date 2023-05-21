package com.userservice.app.clients;

import com.userservice.app.models.dto.DetectFaceAPIResponseDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "detect-face-api", url = "${detectFaceApi.uri:8000}")
public interface DetectFaceApiClient {

    @PostMapping(value = "/faceDetectAPI/detect_face",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    public DetectFaceAPIResponseDTO detectFaces(@RequestHeader("api_key") String apiKey,
                                                @RequestPart("image") MultipartFile image);

}
