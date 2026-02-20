package com.example.music.controller;

import com.example.music.common.result.Result;
import com.example.music.domain.dto.LoginReq;
import com.example.music.domain.dto.RegisterReq;
import com.example.music.domain.vo.LoginVO;
import com.example.music.domain.vo.UserMeVO;
import com.example.music.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "注册/登录/用户信息")
@RestController
@RequestMapping("/user")
public class UserAuthController {

    private final UserService userService;

    public UserAuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterReq req) {
        userService.register(req);
        return Result.ok(null);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginReq req) {
        return Result.ok(userService.login(req));
    }

    @GetMapping("/me")
    public Result<UserMeVO> me() {
        return Result.ok(userService.me());
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring("Bearer ".length()).trim();
            userService.logout(token);
        }
        return Result.ok(null);
    }
}
