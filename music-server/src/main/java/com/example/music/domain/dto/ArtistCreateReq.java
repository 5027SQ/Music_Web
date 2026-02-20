package com.example.music.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArtistCreateReq {

    @NotBlank(message = "name 不能为空")
    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String avatarUrl;

    @Size(max = 2000)
    private String intro;
}