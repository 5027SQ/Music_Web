package com.example.music.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserMeVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String role;
}
