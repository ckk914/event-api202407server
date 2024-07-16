package com.study.event.api.config;

import com.study.event.api.auth.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter; //⭐️org 스프링꺼

// 스프링 시큐리티 설정 파일
// 인터셉터, 필터 처리
// 세션인증, 토큰인증
// 권한처리
// OAuth2 - SNS로그인
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    // 비밀번호 암호화 객체 컨테이너에 등록 (스프링에게 주입받는 설정)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 설정 (스프링 부트 2.7버전 이전 인터페이스를 통해 오버라이딩)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {

        http
                .cors()
                .and()
                .csrf().disable()               //필터 설정 off
                .httpBasic().disable()      // 베이직  인증 off(시큐리티가 제공하는거)
                .formLogin().disable()    // 로그인 창 off
                // 세션 인증은 더 이상 사용하지 않도록 함  > 토큰 인증 시 꺼둬야함!🌟
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //상태관리를 세션으로 안함
                .and()      //여기까지 세션 설정
                .authorizeRequests()    //요청별로 인가 설정 (url 허락 하는거 )
                .antMatchers("/","/auth/**").permitAll()     //인가 설정 on

                // 나머지 요청은 전부 인증(로그인) 후 진행해라.!
                .anyRequest().authenticated() //인가 설정 on
                                                                      // anyRequest().permitAll()로 하면
                                                                     // 로그인 없이 가능!
//              서버-> 토큰 발급
//             클라이언트는 계속적으로  검증을 위해 들고 와야한다~! 토큰



//                .antMatchers("/**").permitAll()     //모든 요청 허가~!
        ;

        //토큰 위조 검사 커스텀 필터 필터체인에 연결
        //필터 순서 안해주면 에러
        // CorsFilter (spring 의 필터 ) 뒤에 커스텀 필터를 연결
        http.addFilterAfter(jwtAuthFilter, CorsFilter.class);

        return http.build();
    }



}
