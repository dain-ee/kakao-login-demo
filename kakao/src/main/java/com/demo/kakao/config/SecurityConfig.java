// src/main/java/com/demo/kakao/config/SecurityConfig.java
package com.demo.kakao.config;

import com.demo.kakao.security.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// 이 클래스는 Spring 설정 클래스임을 의미. Spring이 실행될 때 이 클래스를 읽고 설정 정보 등록
// 이 설정 정보 읽고 Secure FilterChain을 적용
@Configuration
// final로 선언된 변수를 자동주입하기 위한 Lombok Annotation
// 즉, 이 클래스는 실행 시점에 CustomOAuth2UserService 객체를 알아서 받음
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    // securityFilterChain 메서드는 Spring Security에서 필수인 SecurityFilterChain을 정의
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ✅ CSRF 비활성화 (H2 콘솔 등 개발 편의용)
            // 기본적으로 CSRF(Cross Site Request Forgery) 보호를 확성화하지만, 지금 H2 콘솔과 같은 개발환경에서 비활성화
            .csrf(csrf -> csrf.disable())

            // ✅ H2 콘솔 접근 허용 (frameOptions만 disable)
            // H2 콘솔은 iframe을 사용하기 때문에 비활성화
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )

            // ✅ 경로별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 누구나 접근 가능
                .requestMatchers("/api/**").authenticated()     // /api는 로그인한 사용자만 접근 가능
                .anyRequest().permitAll()                       // 그 외는 모두 허용
            )

            // ✅ OAuth2 로그인 설정
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService) // 사용자 정보 가져오는 클래스 등록
                )
                .defaultSuccessUrl("/api/user-info", true) // 로그인 성공 시 이동할 URL 지정
                .failureHandler((request, response, exception) -> {
                    exception.printStackTrace();
                    response.sendRedirect("/login?error");
                })
            )
            .logout(logout -> logout
                    .logoutUrl("/logout") // 기본 로그아웃 경로
                    .logoutSuccessUrl("/") // 로그아웃 후 이동할 URL
                    .invalidateHttpSession(true) // 세션 무효화
                    .deleteCookies("JSESSIONID") // 쿠키 삭제
            );

        return http.build();
    }
}