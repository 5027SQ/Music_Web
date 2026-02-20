package com.example.music.config;

import com.example.music.security.JwtUtil;
import com.example.music.security.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Key;

@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class WebMvcConfig implements WebMvcConfigurer {

    private final StringRedisTemplate redisTemplate;
    private final AuthProperties props;

    public WebMvcConfig(StringRedisTemplate redisTemplate, AuthProperties props) {
        this.redisTemplate = redisTemplate;
        this.props = props;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Key key = JwtUtil.keyFromSecret(props.getJwt().getSecret());
        registry.addInterceptor(new LoginInterceptor(
                redisTemplate,
                key,
                props.getAuth().getWhitelist(),
                props.getAuth().getPublicGet()
                )).addPathPatterns("/**");
    }
}
