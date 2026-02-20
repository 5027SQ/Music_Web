package com.example.music.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongVO {
    private Long id;
    private String title;
    private String artist;
    private Long artistId;
    private String album;
    private String coverUrl;
    private String audioUrl;
    private Integer durationSec;
    private Long uploaderId;
    private Long playCount;
    private Integer status;
    private LocalDateTime createdAt;
}