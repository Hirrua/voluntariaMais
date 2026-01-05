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

    @Value("${cloudflare.r2.account-id:}")
    private String accountId;

    @Value("${cloudflare.r2.public-url:}")
    private String publicUrl;

    @Value("${base.path}")
    private String BASE_PATH;

    private final S3Client s3Client;

    public StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    private String buildKey(StorageFolder folder, Long entityId, String fileName) {
        return buildPrefix(folder, entityId) + fileName;
    }

    private String buildPrefix(StorageFolder folder, Long entityId) {
        String basePath = normalizeBasePath(BASE_PATH);
        if (basePath.isEmpty()) {
            return String.format("%s/%d/", folder.getPath(), entityId);
        }

        return String.format("%s/%s/%d/", basePath, folder.getPath(), entityId);
    }

    private String normalizeBasePath(String basePath) {
        if (basePath == null) {
            return "";
        }

        String trimmed = basePath.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        int start = 0;
        int end = trimmed.length();
        while (start < end && trimmed.charAt(start) == '/') {
            start++;
        }
        while (end > start && trimmed.charAt(end - 1) == '/') {
            end--;
        }

        return trimmed.substring(start, end);
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

    public String buildPublicUrl(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }

        String trimmedKey = key.trim();
        if (trimmedKey.startsWith("http://") || trimmedKey.startsWith("https://")) {
            return trimmedKey;
        }

        String baseUrl = resolvePublicBaseUrl();
        if (baseUrl.isEmpty()) {
            return trimmedKey;
        }

        return baseUrl + "/" + normalizeKey(trimmedKey);
    }

    public List<String> listFiles(StorageFolder folder, Long entityId) {
        String prefix = buildPrefix(folder, entityId);

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

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return "";
        }

        String trimmed = baseUrl.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        int end = trimmed.length();
        while (end > 0 && trimmed.charAt(end - 1) == '/') {
            end--;
        }

        return trimmed.substring(0, end);
    }

    private String resolvePublicBaseUrl() {
        String explicitBaseUrl = normalizeBaseUrl(publicUrl);
        if (!explicitBaseUrl.isEmpty()) {
            return explicitBaseUrl;
        }

        String account = normalizeSegment(accountId);
        String bucketName = normalizeSegment(bucket);
        if (account.isEmpty() || bucketName.isEmpty()) {
            return "";
        }

        return "https://" + account + ".r2.cloudflarestorage.com/" + bucketName;
    }

    private String normalizeKey(String key) {
        int start = 0;
        int length = key.length();
        while (start < length && key.charAt(start) == '/') {
            start++;
        }

        return key.substring(start);
    }

    private String normalizeSegment(String value) {
        if (value == null) {
            return "";
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        int start = 0;
        int end = trimmed.length();
        while (start < end && trimmed.charAt(start) == '/') {
            start++;
        }
        while (end > start && trimmed.charAt(end - 1) == '/') {
            end--;
        }

        return trimmed.substring(start, end);
    }
}
