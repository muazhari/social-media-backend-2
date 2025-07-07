package com.muazhari.socialmediabackend2.outers.repositories;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class FileRepository {

    @Autowired
    private MinioClient minioClient;

    public void upload(String bucketName, String objectName, MultipartFile file, String contentType) {
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
                                    .contentType(contentType)
                                    .build()
                    );
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to Minio", e);
        }
    }

    public String getUrl(String bucketName, String objectName) {
        try {
            return minioClient
                    .getPresignedObjectUrl(
                            GetPresignedObjectUrlArgs
                                    .builder()
                                    .method(Method.GET)
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .expiry(60 * 60)
                                    .build()
                    );
        } catch (Exception e) {
            throw new RuntimeException("Error getting file URL from Minio", e);
        }
    }
}
