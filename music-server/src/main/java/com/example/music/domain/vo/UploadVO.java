package com.example.music.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadVO {
    private String url;
    private String objectKey;
}
