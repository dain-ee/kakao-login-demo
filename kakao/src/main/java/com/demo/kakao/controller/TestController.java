// com.demo.kakao.controller.TestController.java

package com.demo.kakao.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

// 실제 웹브라우저에서 호출할 수 있는 REST API 컨트롤러
// Spring Boot 애플리케이션이 실행될 때 HTTP 요청을 받을 수 있는 엔드포인트를 정의한 클래스
// @Controller + @ResponseBody 기능이 합쳐짐
@RestController
public class TestController {

    // 각 브라우저에서 http://localhost:8080/으로 접속하면 이 메서드가 실행
    // 카카오 로그인 링크를 HTML <a> 태그로 리턴
    // 클릭하면 /oauth/authorization/kakao로 이동해서 OAuth2 로그인 프로세스 시작
    @GetMapping("/")
    public String redirectToLogin() {
        return "<a href=\"/oauth2/authorization/kakao\">🔐 카카오 로그인</a>";
    }

    // 브라우저에서 /api/user-info 요청 시 실행
    // Spring Security가 자동으로 넘겨주는 Authentication 객체를 통해 로그인된 사용자 정보 가져옴
    // 인증 안 됐으면 접근이 막힘
    @GetMapping("/api/user-info")
    @ResponseBody  // 문자열 그대로 응답
    public String getUserInfo(Authentication authentication) {
        var oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauthUser.getAttributes();

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (String) properties.get("nickname");

        // ✅ 간단한 HTML 문자열로 응답 (타임리프 없이)
        return """
        <html>
        <body>
            <h2>👤 닉네임: %s</h2>
            <a href="/kakao-logout">
                <button type="button">🚪 카카오 완전 로그아웃</button>
            </a>
        </body>
        </html>
        """.formatted(nickname);
    }

    @GetMapping("/kakao-logout")
    public void kakaoFullLogout(HttpServletResponse response) throws IOException {
        String clientId = "6108d4696e9de6ee73209c208847609e";  // 카카오 앱 REST API 키
        String redirectUri = "http://localhost:8080";

        String logoutUrl = "https://kauth.kakao.com/oauth/logout"
                + "?client_id=" + clientId
                + "&logout_redirect_uri=" + redirectUri;

        response.sendRedirect(logoutUrl);
    }

    // 로직 이해하기
    // 1. 브라우저 클릭
    // 2. GET /oauth2/authorization/kakao << Spring Security 제공
    // 3. Redirect to GET https://kauth.kakao/com/oauth/authorize?
    // 4. 사용자 로그인 후
    // 5. Redirect to GET /login/oauth2/code/kakao << Spring Security가 처리
    // 6. AccessToken/Refresh Token 획득 후 세션 저장
    // 7. 로그인 완료
    // 8. 인증된 사용자만 GET /api/user-info
}