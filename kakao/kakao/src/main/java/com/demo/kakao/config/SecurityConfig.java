// src/main/java/com/demo/kakao/config/SecurityConfig.java
package com.demo.kakao.config;

import com.demo.kakao.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CSRF 비활성화 (H2 콘솔 등 개발 편의용)
            .csrf(csrf -> csrf.disable())

            // ✅ H2 콘솔 접근 허용 (frameOptions만 disable)
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )

            // ✅ 경로별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 접근 허용
                .requestMatchers("/api/**").authenticated()     // /api는 인증 필요
                .anyRequest().permitAll()                       // 그 외는 모두 허용
            )

            // ✅ OAuth2 로그인 설정
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .failureHandler((request, response, exception) -> {
                    exception.printStackTrace();
                    response.sendRedirect("/login?error");
                })
            );

        return http.build();
    }
}