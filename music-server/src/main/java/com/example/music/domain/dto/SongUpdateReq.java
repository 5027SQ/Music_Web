package com.example.music.domain.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SongUpdateReq {
    @Size(max = 200)
    private String title;

    @Size(max = 200)
    private String artist;

    @Positive(message = "artistId 必须大于 0")
    private Long artistId;

    @Size(max = 200)
    private String album;

    @Size(max = 500)
    private String coverUrl;

    @Size(max = 500)
    private String audioUrl;

    private Integer durationSec;

    private Integer status; // 1=正常 0=下架（可选）
}