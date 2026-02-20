package com.example.music.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SongHotVO extends SongVO {
    private Double hotScore; // Redis ZSET score
}
