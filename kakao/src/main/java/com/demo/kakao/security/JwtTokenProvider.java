package com.demo.kakao.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

// 스프링이 빈 객체로 등록해 어디서나 주입할 수 있게 함
// 해당 클래스에서 JWT 생성하고 검증하는 로직을 담았음
// 즉, JWT 공장과 같다. @Componenet를 붙여서 Spring이 미리 만들어 놓고, 다른 클래스들이 꺼내쓸 수 있게 함.
@Component
public class JwtTokenProvider {

    // ✅ 대칭키 (보안 위해 실제 서비스에서는 환경변수로 관리 권장)
    // 대칭키: HS256 서명에 쓸 비밀키
    // JWT는 서명을 붙여서 위조를 방지한다. 그때 서명할 때 사용하는 비밀키를 만드는 것.
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰 수명(밀리초)
    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60;        // 1시간
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7일

    // ✅ Access Token 생성 함수
    public String generateAccessToken(String kakaoId, String role) {
        return Jwts.builder()
                // kakaoId로 subject 저장
                .setSubject(kakaoId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(key)
                // 문자열(String)으로 만들어서 응답을 줄 수 있게 함.
                .compact();
    }

    // ✅ Refresh Token 발급
    // refresh token은 유저 정보 없이 만들 수 있다.
    // access token이 만료되었을 때, 이걸로 다시 발금할 수 있게 해줌.
    public String generateRefreshToken() {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(key)
                .compact();
    }

    // ✅ 요청 헤더에서 토큰 추출
    // 클라이언트가 서버에서 요청을 보낼 때, Authorization 헤더에 토큰을 담아서 보냄.
    // 그걸 꺼내서 실제 토큰만 반환하는 함수
    public String resolveToken(HttpServletRequest request) {
        // HTTP 요청 헤더 중 Authorization 값을 통째로 꺼냄.
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            // "Bearer " 잘라내고 나머지 반환
            return bearer.substring(7);
        }
        return null;
    }

    // ✅ 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ 토큰에서 카카오 ID(subject) 추출
    public String getKakaoIdFromToken(String token) {
        // parser(파서) 객체를 만드는 빌더
        return Jwts.parserBuilder()
                // 우리(서버)가 알고 있는 비밀키 주입
                .setSigningKey(key)
                // 파서 완성
                .build()
                // 토큰 문자열을 실제로 해석
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ 토큰에서 role 클레임 추출
    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // ✅ Role을 기반으로 Spring Security 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
    }
}