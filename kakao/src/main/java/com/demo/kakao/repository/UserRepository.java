package com.demo.kakao.repository;

import com.demo.kakao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data JPA의 핵심 인터페이스인 JPARepository
// 이를 사용하면, SQL 없이도 CRUD기능을 자동으로 사용할 수 있다.
// findAll(), save(user), deleteById(id)와 같은 메서드가 자동으로 생성
public interface UserRepository extends JpaRepository<User, Long> {

    // 사용자 정의 쿼리 메서드
    // kakaoId라는 필드값으로 User를 찾아오는 쿼리를 자동으로 만들어준다.
    Optional<User> findByKakaoId(String kakaoId);

    // 여기서 findByRole, existsByKakaoId와 같은 메서드를 만들어서 Spring이 알아서 쿼리를 만들 수 있음.

}