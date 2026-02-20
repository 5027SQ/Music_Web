package com.example.music.security;

import com.example.music.common.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

// 鉴权拦截器（JWT + Redis 校验 token）
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    private final Key key;
    private final List<String> whitelist;
    private final List<String> publicGet;

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper om = new ObjectMapper();

    public LoginInterceptor(StringRedisTemplate redisTemplate,
                            Key key,
                            List<String> whitelist,
                            List<String> publicGet) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.whitelist = whitelist;
        this.publicGet = publicGet;
    }

    private boolean match(List<String> patterns, String path) {
        if (patterns == null) return false;
        for (String p : patterns) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    private void writeJson(HttpServletResponse resp, int httpCode, Result<?> body) throws IOException {
        resp.setStatus(httpCode);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json");
        resp.getWriter().write(om.writeValueAsString(body));
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        String path = req.getRequestURI();

        // 1️⃣ 预检请求直接放行（CORS 必须）
        if (HttpMethod.OPTIONS.matches(req.getMethod())) return true;

        // 2️⃣ 普通白名单
        if (match(whitelist, path)) return true;

        // ⭐ 3️⃣ 游客 GET 放行（重点）
        if (HttpMethod.GET.matches(req.getMethod()) && match(publicGet, path)) {
            return true;
        }

        // 4️⃣ 开始 JWT 校验
        String auth = req.getHeader("Authorization");

        if (auth == null || !auth.startsWith("Bearer ")) {
            writeJson(resp, 401, Result.fail("missing token"));
            return false;
        }

        String token = auth.substring("Bearer ".length()).trim();

        if (token.isEmpty()) {
            writeJson(resp, 401, Result.fail("missing token"));
            return false;
        }

        // Redis 校验 token 是否仍有效
        String redisKey = "auth:token:" + token;
        Boolean exists = redisTemplate.hasKey(redisKey);

        if (exists == null || !exists) {
            writeJson(resp, 401, Result.fail("token expired or logged out"));
            return false;
        }

        try {
            Claims claims = JwtUtil.parse(key, token);
            AuthContext.set(claims);
            return true;
        } catch (Exception e) {
            writeJson(resp, 401, Result.fail("invalid token"));
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object handler, Exception ex) {
        AuthContext.clear();
    }
}
