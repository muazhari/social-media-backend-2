package com.muazhari.socialmediabackend2.outers.repositories;

import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class FileRepository {
    final String bucketName = "social-media-backend/post";

    @Autowired
    private MinioClient minioClient;

    public void uploadFile(String objectName, MultipartFile file) {
        try {
            boolean isExist = minioClient
                    .bucketExists(
                            BucketExistsArgs
                                    .builder()
                                    .bucket(bucketName)
                                    .build()
                    );
            if (!isExist) {
                minioClient
                        .makeBucket(
                                MakeBucketArgs
                                        .builder()
                                        .bucket(bucketName)
                                        .build()
                        );
            }

            minioClient
                    .putObject(
                            PutObjectArgs
                                    .builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .stream(file.getInputStream(), file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build()
                    );
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Minio", e);
        }
    }

    public String getFileUrl(String objectName) {
        try {
            return minioClient
                    .getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs
                                    .builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .expiry(0)
                                    .build()
                    );
        } catch (Exception e) {
            throw new RuntimeException("Error getting file URL from Minio", e);
        }
    }
}
