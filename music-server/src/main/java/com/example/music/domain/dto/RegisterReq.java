package com.example.music.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//DTO（注册）
@Data
public class RegisterReq {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 6, max = 50)
    private String password;

    @Size(max = 30)
    private String nickname;
}
