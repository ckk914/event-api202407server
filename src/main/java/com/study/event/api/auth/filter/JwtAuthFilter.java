package com.study.event.api.auth.filter;

import com.study.event.api.auth.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

//클라이언트가 요청에 포함한 토큰 정보를 검사하는 필터
//컨트롤러 보다 앞선 디스패치 보다 빠르게 검사
//OncePerRequestFilter : 요청 마다 검사
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
   //토큰 만든 객체
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            //요청 메시지에서 토큰을 파싱
            //토큰 정보는 요청 헤더에 포함되어 전송됨!
            //토큰 가져옴
        String token = parseBearerToken(request);

        if(token != null){
            log.info("토큰 위조 검사 필터 작동!");
            //토큰 위조 검사
            String userId = tokenProvider.validateAndGetTokenInfo(token);

            // 인증 완료 처리
                /*
                     스프링 시큐리티에게 인증완료 상황을 전달하여
                     403 상태코드 대신 정상적인 흐름을 이어갈 수 있도록
                 */
            AbstractAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId,  //인증 완료 후 컨트롤러에서 사용할 정보
                            null,  //인증된 사용자의 패스워드 - 보통 null로 해둠
                            new ArrayList<>()  //인가 정보(권한) 리스트

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

        //필터 체인에 내가 만든 커스텀 필터를 실행하도록 명령
        //필터 체인 : 필터는 여러개임. 우리가 체인에 걸어놓은
        // 필터를 실행 명령⭐️ / 등록은 시큐리티 컨피그에서
        filterChain.doFilter(request,response);
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
        //토큰에 붙어있는 bearer 라는 문자열 제거
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7); //7번부터 끝까지
        }
        return null;
    }
}
