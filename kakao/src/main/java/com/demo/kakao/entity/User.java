package com.demo.kakao.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kakaoId;

    @Column(nullable = true) // email은 null 가능
    private String email;

    private String nickname;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private Role role;
}