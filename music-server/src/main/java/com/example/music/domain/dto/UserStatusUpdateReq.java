package com.example.music.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateReq {
    @NotNull
    private Integer status; // 1=启用, 0=禁用
}