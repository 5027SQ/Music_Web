package com.example.music.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArtistUpdateReq {

    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String avatarUrl;

    @Size(max = 2000)
    private String intro;

    private Integer status; // 1=正常 0=下架
}