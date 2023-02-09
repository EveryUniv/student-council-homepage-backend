package com.dku.council.global.auth.jwt;

import com.dku.council.domain.UserRole;
import com.dku.council.domain.user.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationTokenProvider implements AuthenticationTokenProvider {
    @Value("${jwt.access-expiration}")
    private long accessExpiration;
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public AuthenticationToken getTokenFromHeader(HttpServletRequest request) {
        return JwtAuthenticationToken.builder()
                .accessToken(request.getHeader("DKU-AUTH-TOKEN"))
                .refreshToken(request.getHeader("DKU-REFRESH-TOKEN"))
                .build();
    }

    @Override
    public JwtAuthentication getAuthentication(String accessToken) {
        Jws<Claims> claimsJws = validateAccessToken(accessToken);

        Claims body = claimsJws.getBody();
        String userId = (String) body.get("userId");
        String userRole = (String) body.get("Role");

        return new JwtAuthentication(userId, userRole);
    }


    public AuthenticationToken issue(User user) {
        return JwtAuthenticationToken.builder()
                .accessToken(createAccessToken(user.getId().toString(), user.getUserRole()))
                .refreshToken(createRefreshToken())
                .build();
    }

    public AuthenticationToken reIssue(AuthenticationToken authenticationToken){
        String refreshToken = authenticationToken.getRefreshToken();
        //만료되면 새로운 refreshToken 반환.
        String validateRefreshToken = validateRefreshToken(refreshToken);
        String accessToken = refreshAccessToken(authenticationToken.getAccessToken());

        return JwtAuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(validateRefreshToken)
                .build();

    }

    private String refreshAccessToken(String accessToken) {
        String userId;
        UserRole role;
        try{
            Jws<Claims> claimsJws = validateAccessToken(accessToken);
            Claims body = claimsJws.getBody();
            userId = (String) body.get("userId");
            role = UserRole.valueOfName((String) body.get("Role"));
        }catch (ExpiredJwtException e){
            userId = (String) e.getClaims().get("userId");
            role = UserRole.valueOfName((String) e.getClaims().get("Role"));
        }
        return createAccessToken(userId, role);
    }

    private String createAccessToken(String userId, UserRole role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(accessExpiration, ChronoUnit.HOURS);
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("userId", userId);
        payloads.put("Role", role.getRole());
        return Jwts.builder()
                .setSubject("UserInfo") //"sub":"userId"
                .setClaims(payloads)
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    private String createRefreshToken() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validity = now.plus(refreshExpiration, ChronoUnit.DAYS);
        return Jwts.builder()
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(validity.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }
    private Jws<Claims> validateAccessToken(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 토큰입니다.");
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }


    private String validateRefreshToken(String refreshToken) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(refreshToken);
            return refreshToken;
        } catch (ExpiredJwtException e) {
            return createRefreshToken();
        } catch (JwtException e) {
            throw new RuntimeException("유효하지 않은 토큰입니다. 로그인을 다시 해주세요:)");
        }
    }


}
