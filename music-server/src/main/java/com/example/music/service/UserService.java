package com.example.music.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.music.domain.dto.LoginReq;
import com.example.music.domain.dto.RegisterReq;
import com.example.music.domain.vo.LoginVO;
import com.example.music.domain.vo.UserMeVO;
import com.example.music.domain.vo.UserVO;

public interface UserService {
    void register(RegisterReq req);
    LoginVO login(LoginReq req);
    UserMeVO me();
    void logout(String token);

    // ✅ 用户管理
    IPage<UserVO> page(int page, int size, String keyword);
    void updateStatus(Long id, Integer status);
    void remove(Long id);
}