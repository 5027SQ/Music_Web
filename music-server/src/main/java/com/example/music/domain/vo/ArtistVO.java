package com.example.music.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArtistVO {
    private Long id;
    private String name;
    private String avatarUrl;
    private String intro;
    private Integer status;
    private LocalDateTime createdAt;
}