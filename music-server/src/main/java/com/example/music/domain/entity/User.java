package com.example.music.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    private String password;

    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    private String role;
    private Integer status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
