package com.demo.kakao.security;

import com.demo.kakao.entity.User;
import com.demo.kakao.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

// 이 클래스가 Spring이 자동으로 관리 Bean이 되게함.
// 즉, 다른 클래스에서 @Autowired 또는 생성자 주입으로 자동 주입이 가능
@Component
// final 붙은 필드를 가진 생성자를 자동으로 만들어줌
@RequiredArgsConstructor
// OAuth2 로그인 성공 후 자동으로 실행되는 후처리 클래스
// Spring Security가 로그인 성공 시 이 클래스의 onAuthenticationSuccess() 메서드를 호출
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    // 이 클래스에서는 JwtTokenProvider와 UserRepository를 생성자 주입
    // 그래서 new Oauth2SuccessHandler(jwt,repo)를 직접 안 써도 됨.
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 매개변수는 1) 클라이언트의 요청 정보 2) 클라이언트로 보낼 응답 구성 3) 인증객체
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // authentication.getPrincipal() = 로그인에 성공한 사용자의 정보(우리가 만든 CustomOAuth2User 객체)가 들어있음.
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String kakaoId = oAuth2User.getName(); // ID
        // 없으면 Optional.empty(), 있으면 Optional<User>
        Optional<User> user = userRepository.findByKakaoId(kakaoId);

        if (user.isPresent()) {
            // 사용자가 DB에 존재하면, Access Token(사용자 ID + 역할을 담아서 발급)
            String accessToken = jwtTokenProvider.generateAccessToken(kakaoId, user.get().getRole().name());
            // 사용자가 DB에 존재하면, Refresh Token(역할 정보는 없고, 유효기간 긴 토큰)
            String refreshToken = jwtTokenProvider.generateRefreshToken();
            // 응답 본문을 JSON 형식으로 만들어서 클라이언트에게 보냄
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\"}");
        } else {
            // 사용자가 DB에 없으면, 401 Unauthorized
            // SC_UNAUTHORIZED는 자바의 HttpServletResponse 클래스 안에 정의된 HTTP 상태 코드 상수
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}