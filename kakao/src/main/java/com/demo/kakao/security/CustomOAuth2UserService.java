package com.demo.kakao.security;

import com.demo.kakao.entity.Role;
import com.demo.kakao.entity.User;
import com.demo.kakao.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
// 이 클래스가 서비스 역할을 한다는 것을 Spring에게 알려주기
@Service
// final로 선언된 필드를 자동으로 생성자 주입
@RequiredArgsConstructor
// Spring Security가 기본적으로 제공하는 OAuth2 처리 로직을 상속받아서 커스터마이징
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    // 사용자 정보를 DB에 저장하거나 조회하기 위해 JPA의 Repository를 가져옴
    private final UserRepository userRepository;

    @Override
    // loadUser 메서드는 카카오 로그인이 성공한 그 직후에 호출
    // 즉, 카카오에서 사용자 정보 JSON을 받아온 순간 호출
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        // 사용자 정보 변수로 저장
        String kakaoId = attributes.get("id").toString();
        String nickname = profile.get("nickname").toString();
        String profileImage = profile.get("profile_image_url").toString();

        // email은 nullable, 현재 카카오 동의항목에 이메일이 안되는 것으로 보임
        String email = kakaoAccount.get("email") != null ? kakaoAccount.get("email").toString() : null;

        // 기존 유저가 있으면 그대로, 없으면 새로 저장
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> userRepository.save(User.builder()
                        .kakaoId(kakaoId)
                        .email(email)
                        .nickname(nickname)
                        .profileImage(profileImage)
                        .role(Role.USER)
                        .build()));

        return new CustomOAuth2User(user, attributes);
    }
}