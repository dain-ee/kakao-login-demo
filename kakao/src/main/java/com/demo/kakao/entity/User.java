package com.demo.kakao.entity;

import jakarta.persistence.*;
import lombok.*;

// JPA에서 DB테이블로 인식되는 객체라는 뜻
@Entity
// 모든 필드에 대해서 getter,setter 자동생성
@Getter
@Setter
// 실제 DB 테이블 이름 정하기
@Table(name = "users")
// 기본 생성자 생성
@NoArgsConstructor
// 모든 필드를 인자로 받는 생성자 생성
@AllArgsConstructor
// User.builder().. 의 방법으로 객체 생성 가능
@Builder
// 이 클래스는 DB 테이블과 1:1 매핑되는 자바 객체
public class User {

    // PK 필드
    @Id
    // DB에서 AUTO_INCREMENT처럼 자동 증가하는 숫자 ID 생성
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실제 로그인 시, 이 값을 기준으로 아까 DB에서 유저를 찾음
    // 카카오에서 받은 고유 사용자 ID
    private String kakaoId;

    @Column(nullable = true) // email은 null 가능, 카카오에서 현재 비즈앱 아니면 주지 않아서 설정
    private String email;

    // 카카오 프로필에서 갖고온 값들
    private String nickname;
    private String profileImage;

    // EnumType.String을 쓰면 숫자가 아닌, 문자로 저장되어 가독성이 좋으며 유지보수가 편함
    @Enumerated(EnumType.STRING)
    private Role role;
}