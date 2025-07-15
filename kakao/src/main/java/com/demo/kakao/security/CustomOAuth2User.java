package com.demo.kakao.security;

import com.demo.kakao.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
// OAuth2User 인터페이스 구현
// Spring Security가 요구하는 로그인 사용자 정보 형태
// DB에서 꺼낸 User 객체를 Spring Security가 처리할 수 있도록 감싸주는 어댑터 역할
public class CustomOAuth2User implements OAuth2User {

    // user: DB에서 꺼낸 내 서비스만의 User 객체
    // attributes: 카카오에서 받아온 JSON형태의 원본 사용자 정보
    private final User user;
    private final Map<String, Object> attributes;

    // 생성자
    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 카카오에서 받아온 원본 데이터를 그대로 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // Spring Security에서 권한을 처리할 때, GrantedAuthority를 참조
    // singleton은 권한 하나만 넘길 때 쓰는 Java 유틸리티
    // ENUM 타입의 Role에 "ROLE_"을 붙이는 이유: Spring Security는 기본적으로 저 prefix가 붙은 문자열을 권한으로 인식
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // 사용자를 대표하는 고유 식별자 역할(여기서 kakaoId)
    @Override
    public String getName() {
        return user.getKakaoId();
    }
}