package com.example.music.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.music.common.result.Result;
import com.example.music.domain.dto.UserStatusUpdateReq;
import com.example.music.domain.vo.UserVO;
import com.example.music.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "AdminUser", description = "用户管理接口（仅管理员）")
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/page")
    public Result<IPage<UserVO>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String keyword) {
        return Result.ok(userService.page(page, size, keyword));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody UserStatusUpdateReq req) {
        userService.updateStatus(id, req.getStatus());
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        userService.remove(id);
        return Result.ok(null);
    }
}