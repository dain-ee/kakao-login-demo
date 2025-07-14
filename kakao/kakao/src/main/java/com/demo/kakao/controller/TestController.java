// com.demo.kakao.controller.TestController.java

package com.demo.kakao.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "<a href=\"/oauth2/authorization/kakao\">🔐 카카오 로그인</a>";
    }

    @GetMapping("/api/user-info")
    public String getUserInfo(Authentication authentication) {
        return "🧑 로그인된 사용자: " + authentication.getName();
    }
}