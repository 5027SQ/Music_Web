package com.example.music.common.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class MinioPresignUtil {

    private MinioPresignUtil() {}

    /** 从 http://localhost:9000/music/audio/xxx.mp3 提取 objectKey=audio/xxx.mp3 */
    public static String extractObjectKeyFromUrl(String url, String bucket) {
        if (url == null || url.isBlank()) return null;

        // 兼容两种形式：
        // 1) .../music/audio/xxx.mp3
        // 2) .../music/audio%2Fxxx.mp3
        String marker = "/" + bucket + "/";
        int idx = url.indexOf(marker);
        if (idx < 0) return null;
        String tail = url.substring(idx + marker.length()); // audio/xxx 或 audio%2Fxxx

        // URL decode 后把 %2F 还原为 /
        String decoded = URLDecoder.decode(tail, StandardCharsets.UTF_8);
        // 避免开头多一个 /
        if (decoded.startsWith("/")) decoded = decoded.substring(1);
        return decoded;
    }

    public static String presignGet(MinioClient minioClient, String bucket, String objectKey, int expireSeconds) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(expireSeconds)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("presign failed: " + e.getMessage(), e);
        }
    }
}
