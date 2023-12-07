package com.post.service.app.services.impl;

import com.post.service.app.services.FileService;
import helpers.CloudinaryHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private CloudinaryHelper cloudinaryHelper;

    @Override
    public Mono<String> saveImageInDirectory(FilePart file, String path) {
        log.info("Enter to saveImageInDirectory()");
        log.info("Filename -> " + file.filename());
        String uniqueFilename = UUID.randomUUID().toString().concat(".jpg");
        path = path.concat(File.separator).concat(uniqueFilename);
        File newFile = new File(path);
        return file.transferTo(newFile)
                .then(Mono.just(path));
    }

    @Override
    public InputStream mergeInputStreams(InputStream inputStream1, InputStream inputStream2) {
        return new SequenceInputStream(inputStream1, inputStream2);
    }

    @Override
    public Mono<String> uploadFile(FilePart filePart) {
        log.info("Enter to uploadFile()");
        return filePart.content()
                .map(DataBuffer::asInputStream)
                .reduce(this::mergeInputStreams)
                .map(inputStream -> {
                    try {
                        byte[] fileContent = inputStream.readAllBytes();
                        String result = this.cloudinaryHelper.uploadImage(fileContent);
                        return result;
                    } catch (IOException e) {
                        throw new RuntimeException("Error to loading file", e);
                    }
                });
    }

    @Override
    public void deleteImageFromCloudinary(String cloudinaryPath) {
        String result = this.cloudinaryHelper.destroyImage(cloudinaryPath);
        if (!result.equalsIgnoreCase("200")) {
            throw new RuntimeException("Failed to delete image!");
        }
    }

    @Override
    public void deleteFileByPath(String path) {
        log.info("Enter to deleteFileByPath");

        File file = new File(path);
        if (file.delete())
            throw new RuntimeException("Failed to delete image!");
        log.info("File " + path + " deleted successfull!");
    }

}
