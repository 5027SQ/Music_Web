package com.example.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.music.common.exception.BizException;
import com.example.music.config.AuthProperties;
import com.example.music.domain.dto.LoginReq;
import com.example.music.domain.dto.RegisterReq;
import com.example.music.domain.entity.User;
import com.example.music.domain.vo.LoginVO;
import com.example.music.domain.vo.UserMeVO;
import com.example.music.domain.vo.UserVO;
import com.example.music.mapper.UserMapper;
import com.example.music.security.AuthContext;
import com.example.music.security.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements com.example.music.service.UserService {

    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;
    private final AuthProperties props;

    public UserServiceImpl(UserMapper userMapper, StringRedisTemplate redisTemplate, AuthProperties props) {
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.props = props;
    }

    private boolean isAdmin() {
        String role = AuthContext.getRole();
        return role != null && role.equalsIgnoreCase("ROLE_ADMIN");
    }

    private void requireAdmin() {
        if (!isAdmin()) throw new BizException("no permission");
    }

    @Override
    public void register(RegisterReq req) {
        User exist = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername())
                .last("limit 1"));
        if (exist != null) throw new BizException("username already exists");

        String saltHash = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(saltHash);
        u.setNickname(req.getNickname() == null || req.getNickname().isBlank() ? req.getUsername() : req.getNickname());
        u.setAvatarUrl(null);
        u.setRole("ROLE_USER");
        u.setStatus(1);

        userMapper.insert(u);
    }

    @Override
    public LoginVO login(LoginReq req) {
        User u = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername())
                .last("limit 1"));

        if (u == null) throw new BizException("invalid username or password");
        if (u.getStatus() == null || u.getStatus() != 1) throw new BizException("user disabled");

        boolean ok = BCrypt.checkpw(req.getPassword(), u.getPassword());
        if (!ok) throw new BizException("invalid username or password");

        Key key = JwtUtil.keyFromSecret(props.getJwt().getSecret());
        long exp = props.getJwt().getExpireSeconds();

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", u.getId());
        claims.put("role", u.getRole());
        claims.put("username", u.getUsername());

        String token = JwtUtil.generate(key, exp, claims);

        // Redis 记录 token（用于登出/失效）
        String redisKey = "auth:token:" + token;
        redisTemplate.opsForValue().set(redisKey, "1", Duration.ofSeconds(exp));

        return new LoginVO(token);
    }

    @Override
    public UserMeVO me() {
        Long uid = AuthContext.getUserId();
        if (uid == null) throw new BizException("not logged in");

        User u = userMapper.selectById(uid);
        if (u == null) throw new BizException("user not found");

        return new UserMeVO(u.getId(), u.getUsername(), u.getNickname(), u.getAvatarUrl(), u.getRole());
    }

    @Override
    public void logout(String token) {
        if (token == null || token.isBlank()) return;
        redisTemplate.delete("auth:token:" + token);
    }

    // ================= 用户管理 =================

    @Override
    public IPage<UserVO> page(int page, int size, String keyword) {
        requireAdmin();

        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedAt);

        if (keyword != null && !keyword.isBlank()) {
            qw.and(w -> w.like(User::getUsername, keyword)
                    .or()
                    .like(User::getNickname, keyword));
        }

        IPage<User> res = userMapper.selectPage(p, qw);

        return res.convert(u -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(u, vo);
            return vo;
        });
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        requireAdmin();

        User u = userMapper.selectById(id);
        if (u == null) throw new BizException("user not found");

        u.setStatus(status);
        userMapper.updateById(u);
    }

    @Override
    public void remove(Long id) {
        requireAdmin();

        User u = userMapper.selectById(id);
        if (u == null) throw new BizException("user not found");

        userMapper.deleteById(id);
    }
}