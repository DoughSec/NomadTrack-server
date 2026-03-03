package com.nomadtrack.nomadtrackserver.controller;

import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private final S3Presigner presigner;

    public UploadController(S3Presigner presigner) {
        this.presigner = presigner;
    }

    @PostMapping("/presign")
    public Map<String, String> generatePresignedUrl(
            @RequestBody Map<String, String> body
    ) {

        String fileName = body.get("fileName");

        String key = "listings/" + UUID.randomUUID() + "-" + fileName;

        String bucketName = "nomadtrack-images";
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(objectRequest)
                        .build();

        var presignedRequest = presigner.presignPutObject(presignRequest);

        String uploadUrl = presignedRequest.url().toString();
        String fileUrl = "https://" + bucketName + ".s3.us-east-2.amazonaws.com/" + key;

        return Map.of(
                "uploadUrl", uploadUrl,
                "fileUrl", fileUrl
        );
    }
}