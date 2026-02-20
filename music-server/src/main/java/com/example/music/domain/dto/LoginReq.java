package com.example.music.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//dto登录
@Data
public class LoginReq {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
