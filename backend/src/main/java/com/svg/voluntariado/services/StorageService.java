package com.svg.voluntariado.services;

import com.svg.voluntariado.domain.enums.StorageFolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Service
public class StorageService {

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${base.path}")
    private String BASE_PATH;

    private final S3Client s3Client;

    public StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    private String buildKey(StorageFolder folder, Long entityId, String fileName) {
        return String.format("%s/%s/%d/%s",
                BASE_PATH,
                folder.getPath(),
                entityId,
                fileName
        );
    }

    public String upload(StorageFolder folder, Long entityId, String fileName,
                         byte[] content, String contentType) {
        String key = buildKey(folder, entityId, fileName);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromBytes(content)
        );

        return key;
    }

    public List<String> listFiles(StorageFolder folder, Long entityId) {
        String prefix = String.format("%s/%s/%d/", BASE_PATH, folder.getPath(), entityId);

        ListObjectsV2Response response = s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .prefix(prefix)
                        .build()
        );

        return response.contents().stream()
                .map(S3Object::key)
                .toList();
    }

    public void delete(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );
    }

    public HeadObjectResponse getMetadata(String key) {
        return s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );
    }
}
