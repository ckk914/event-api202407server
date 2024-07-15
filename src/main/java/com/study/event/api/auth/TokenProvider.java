package com.study.event.api.auth;

import com.study.event.api.event.entity.EventUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component  //등록 ! -> 이거 후  서비스에서 주입 가능!
@Slf4j
//토큰을 생성하여 발급하고, 서명 위조를 검사하는 객체
public class TokenProvider {

    //서명에 사용할   512 비트의 랜덤 문자열 비밀키
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    /**
     * JWT를 생성하는 메서드
     * @param eventUser - 토큰에 포함될 로그인한 유저의 정보
     * @return - 생성된 JWT의 암호화된 문자열
     */
    public String createToken(EventUser eventUser){

               /*
            토큰의 형태
            {
                "iss": "뽀로로월드",
                "exp": "2024-07-18",
                "iat": "2024-07-15",       //발행
                ...
                "email": "로그인한 사람 이메일",
                "role": "ADMIN"
                ...
                ===
                서명
            }
         */

        // 토큰에 들어갈 커스텀 데이터
        // (추가 클레임)(=내용들)
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", eventUser.getEmail());
        claims.put("role",eventUser.getRole().toString());

        return Jwts.builder()    //생성용 빌더 ⭐️
                //token에 들어갈 서명
                .signWith(
                        Keys.hmacShaKeyFor(/*"서명에 사용할 키"*/
                                SECRET_KEY.getBytes())
                        , SignatureAlgorithm.HS512
                )
                //페이로드에 들어갈 클레임 설정
                .setClaims(claims) //추가 클레임은 항상 가장 먼저 설정! ⭐️
                .setIssuer("메롱메롱") //발급자 정보
                .setIssuedAt(new Date())  // 발급 시간
                .setExpiration(Date.from(
                        Instant.now().plus(1, ChronoUnit.DAYS)
                        //만료 시간 1일로 설정ㄴ
                        ))  //토큰 만료 시간
                .setSubject(eventUser.getId()) //토큰을 식별할 수 있는 유일한 값
                .compact();
    }

    /*
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 서명 위조 여부를 확인
     * 그리고 토큰을 JSON으로 파싱하여 안에 들어있는 클레임(토큰 정보)을 리턴
     * @param token - 클라이언트가 보낸 토큰
     * @return - 토큰에 들어있는 인증 정보(이메일, 권한...)들을 리턴 - 회원 식별 ID
     */
    public String validateAndGetTokenInfo(String token) {
        Claims claims = Jwts.parserBuilder() // 해체시 사용 빌더 ⭐️
                //토큰 발급자의 발급 당시 서명을 넣음
                .setSigningKey(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes())
                )
                //서명 위조 검사 진행:
                // 위조된 경우 exception 발생
                // 정상인 경우 클레임 리턴
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.info("claims: {}",claims);
        //토큰에 인증된 회원의 pK
        return claims.getSubject();
    }
}
