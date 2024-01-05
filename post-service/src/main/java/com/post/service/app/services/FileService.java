package com.post.service.app.services;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.InputStream;

public interface FileService {

    public Mono<String> saveImageInDirectory(FilePart file, String path);

    public InputStream mergeInputStreams(InputStream inputStream1, InputStream inputStream2);
    public Mono<String> uploadFile(FilePart filePart);
    public void deleteImageFromCloudinary(String cloudinaryPath);
    public void deleteFileByPath(String path);
}
