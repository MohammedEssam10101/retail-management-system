package com.retail.management.security;

import com.retail.management.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders; // NEW import for Base64 decoding
import io.jsonwebtoken.security.SignatureException; // This is the correct exception for signature issues
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct; // Keep this, but we might need to add a dependency
import javax.crypto.SecretKey; // NEW import for SecretKey
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;
    private SecretKey key; // Changed Key to SecretKey

    @Autowired
    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @PostConstruct
    public void init() {
        // Decode the Base64 secret from jwtConfig and build the SecretKey
        // The secret should be a Base64 encoded string for newer JJWT versions
        this.key = Jwts.SIG.HS512.key().build(); // Creates a new random HS512 key
        // OR if your secret from jwtConfig is a Base64 encoded string:
        // this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
        // For HS512, your secret should be at least 64 bytes (512 bits) long after decoding.
        // If your secret in application.properties is just a string, it's better to ensure it's long enough
        // and ideally Base64 encoded for production. For simple string, Keys.hmacShaKeyFor is still valid
        // but it's important to use a strong secret.
        // Let's assume for now your secret is a raw string and we'll use Jwts.SIG.HS512.key().build()
        // or a safe way to convert your secret string to bytes.
        // A common and robust way for a secret string from config:
        // this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        // This is still valid if your secret is just a string.

        // To be compliant with the latest recommendations, the secret should be sufficiently long and
        // ideally derived from a Base64 encoded string from properties.
        // Let's use the most robust approach for a string secret from config:
        this.key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfig.getSecret()));
        // Make sure your jwt.secret in application.properties is a Base64 encoded string!
        // E.g., generate a key:
        // String secret = io.jsonwebtoken.security.Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512).getEncoded();
        // Base64.getEncoder().encodeToString(secret);
        // And put that Base64 string in your application.properties
    }

    /**
     * Generate JWT token from authentication
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(username) // Use subject() instead of setSubject()
                .claim("roles", roles)
                .issuedAt(now)     // Use issuedAt() instead of setIssuedAt()
                .expiration(expiryDate) // Use expiration() instead of setExpiration()
                .signWith(key)     // Algorithm inferred from key (HS512)
                .compact();
    }

    /**
     * Generate JWT token from username and roles
     */
    public String generateTokenFromUsername(String username, String roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * Get username from JWT token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser() // Use Jwts.parser()
                .verifyWith(key)     // Use verifyWith() instead of setSigningKey()
                .build()
                .parseSignedClaims(token) // Use parseSignedClaims() instead of parseClaimsJws()
                .getPayload(); // Use getPayload() instead of getBody()

        return claims.getSubject();
    }

    /**
     * Get roles from JWT token
     */
    public String getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", String.class);
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (SignatureException ex) { // Use io.jsonwebtoken.security.SignatureException
            log.error("JWT signature validation failed: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Get expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}