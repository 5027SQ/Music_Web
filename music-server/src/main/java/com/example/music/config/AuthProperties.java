package com.example.music.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "app")
public class AuthProperties {
    private Jwt jwt = new Jwt();
    private Auth auth = new Auth();


    @Data
    public static class Jwt {
        private String secret;
        private long expireSeconds = 86400;
    }

    @Data
    public static class Auth {
        private List<String> whitelist = new ArrayList<>();

        /** ✅ 仅允许 GET 的游客接口 */
        private List<String> publicGet = new ArrayList<>();
    }
}
