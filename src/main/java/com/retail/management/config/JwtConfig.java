package com.retail.management.config;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtConfig {
    private String secret;
    private long expiration;
    private long refreshExpiration;
    private String tokenPrefix = "Bearer ";
    private String headerString = "Authorization";
}
