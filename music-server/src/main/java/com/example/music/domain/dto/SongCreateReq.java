package com.example.music.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SongCreateReq {
    @NotBlank(message = "title 不能为空")
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

    @NotBlank(message = "audioUrl 不能为空")
    @Size(max = 500)
    private String audioUrl;

    @NotNull(message = "durationSec 不能为空")
    @Positive(message = "durationSec 必须大于 0")
    private Integer durationSec;
}