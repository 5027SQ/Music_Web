package com.example.music.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_song")
public class Song {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String artist;

    @TableField("artist_id")
    private Long artistId;

    private String album;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("audio_url")
    private String audioUrl;

    @TableField("duration_sec")
    private Integer durationSec;

    @TableField("uploader_id")
    private Long uploaderId;

    @TableField("play_count")
    private Long playCount;

    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

}