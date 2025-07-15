package com.demo.kakao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// SpringBootApplication = Configuration + EnableAutoConfiguration + ComponentScan
// Configuratoin: 이 클래스가 설정 파일 역할을 한다는 뜻
// Spring Boot가 자동으로 설정(예: Tomcat, Security)
// com.demo.kakao 패키지 이하의 클래스를 자동으로 스캔해서 Bean 등록  (즉, @Component, @Service, @Repository 같은 애들 자동 등록)
@SpringBootApplication
public class KakaoApplication {

	// SpringApplication.run(...)을 호출하면'
	// 내장 Tomcat 실행, 설정 파일 읽기(application.yml), 스프링 빈 등록 및 초기화, HTTP 요청 대기 시작
	public static void main(String[] args) {
		SpringApplication.run(KakaoApplication.class, args);
	}

}
