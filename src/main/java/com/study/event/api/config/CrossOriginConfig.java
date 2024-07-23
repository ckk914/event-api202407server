package com.study.event.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 전역 크로스 오리진 설정: 어떤 클라이언트를 허용할 것인지
//여기서 처리하면 다른 곳에서
//@CrossOrigin <== 이거 다 지우기!⭐️
@Configuration
public class CrossOriginConfig implements WebMvcConfigurer {

    private String[] urls = {
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002",
            "http://localhost:3003",
            "http://mywebsite090909.s3-website.ap-northeast-2.amazonaws.com"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry
                .addMapping("/**") // 어떤 유알엘 요청에서
                .allowedOrigins(urls) //어떤 클라이언트를 urls 스트링배열로 넣음~⭐️
                .allowedMethods("*") // 어떤 방식에서 "GET","POST"
                .allowedHeaders("*") // 어떤 헤더를 허용할지
                .allowCredentials(true) // 쿠키 전송을 허용할지
        ;

    }
}