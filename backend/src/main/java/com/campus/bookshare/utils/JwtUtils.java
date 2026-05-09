package com.campus.bookshare.utils;

import com.campus.bookshare.exception.UnauthorizedException;
import com.campus.bookshare.vo.LoginUserVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expireSeconds;

    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireSeconds * 1000);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public LoginUserVO parseToken(String token) {
        try {
            String realToken = token.replace("Bearer ", "").trim();
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(realToken).getBody();
            LoginUserVO user = new LoginUserVO();
            user.setId(Long.valueOf(claims.getSubject()));
            user.setUsername(String.valueOf(claims.get("username")));
            user.setRole(String.valueOf(claims.get("role")));
            return user;
        } catch (Exception e) {
            throw new UnauthorizedException("登录已失效，请重新登录");
        }
    }
}
