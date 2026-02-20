package com.example.music.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(MinioProperties props) {
        return MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials(props.getAccessKey(), props.getSecretKey())
                .build();
    }
}
