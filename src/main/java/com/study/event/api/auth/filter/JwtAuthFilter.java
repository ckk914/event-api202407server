package com.study.event.api.auth.filter;

import com.study.event.api.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.study.event.api.auth.TokenProvider.*;

// 클라이언트가 요청에 포함한 토큰정보를 검사하는 필터
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // 요청 메세지에서 토큰을 파싱
            // 토큰정보는 요청헤더에 포함되어 전송됨
            String token = parseBearerToken(request);

            log.info("토큰 위조 검사 필터 작동!");
            if (token != null) {
                // 토큰 위조 검사
                TokenUserInfo tokenInfo = tokenProvider.validateAndGetTokenInfo(token);

                // 인증 완료 처리
                /*
                     스프링 시큐리티에게 인증완료 상황을 전달하여
                     403 상태코드 대신 정상적인 흐름을 이어갈 수 있도록
                 */


                // 인가처리를 위한 권한 리스트
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(tokenInfo.getRole().toString()));

                AbstractAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(
                        tokenInfo, // 인증 완료 후 컨트롤러에서 사용할 정보
                        null, // 인증된 사용자의 패스워드 - 보통 null로 둠
                        authorities  // 인가정보(권한) 리스트
                );

                // 인증 완료시 클라이언트의 요청 정보들을 세팅
                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 스프링 시큐리티에게 인증이 끝났다는 사실을 전달
                SecurityContextHolder.getContext().setAuthentication(auth);

            }

        } catch (Exception e) {
            log.warn("토큰이 위조되었습니다.");
            e.printStackTrace();
        }


        // 필터체인에 내가 만든 커스텀필터를 실행하도록 명령
        // 필터체인: 필터는 여러개임 우리가 체인에 걸어 놓은 필터를
        // 실행명령
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        /*
             1. 요청 헤더에서 토큰을 가져오기

             -- request header

             {
                'Authorization' : 'Bearer dhfdlksfd67fdskjfhskfhkssadfasf',
                'Content-type' : 'application/json'
             }
         */

        String bearerToken = request.getHeader("Authorization");

        // 토큰에 붙어있는 Bearer라는 문자열을 제거
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}