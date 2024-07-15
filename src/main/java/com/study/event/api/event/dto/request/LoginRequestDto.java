package com.study.event.api.event.dto.request;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    // 검증을 꼭 넣어야함 백엔드에서!
    // 브라우저에서 자바스크립트를 끌 수 있기 때문에!@
    private String email;
    private String password;

    //자동 로그인 여부 ...

}
