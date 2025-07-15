// JwtAuthenticationFilter.java
package com.demo.kakao.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// final로 선언된 jwtTokenProvider 생성자를 주입받음
@RequiredArgsConstructor
// 모든 요청마다 한 번만 실행되는 필터를 의미함.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT를 추출,검증,파싱하는 유틸 클래스
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    // 이 메서드에서 1) 토큰을 꺼내고 2) 검증하고 3) 인증 컨텍스트로 넣고 4) 다음 필터로 넘겨줌
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 JWT 토큰 꺼냄
        String token = jwtTokenProvider.resolveToken(request);

        // 토큰 존재 여부와 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 존재하고 유효하다면, kakaoId와 role 가져오기
            String kakaoId = jwtTokenProvider.getKakaoIdFromToken(token);
            String role     = jwtTokenProvider.getRoleFromToken(token);
            // principal=kakaoId, credentials=null, authorities=role 넣어서 인증 객체 생성
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            kakaoId, null, jwtTokenProvider.getAuthorities(role));
            // 이 정보를 Spring Security에 등록, SecurityContext에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        // 필터링 끝나고 나면, 다음 필터나 컨트롤러로 요청을 넘김
        filterChain.doFilter(request, response);
    }
}