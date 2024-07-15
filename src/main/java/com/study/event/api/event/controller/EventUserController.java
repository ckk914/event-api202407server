package com.study.event.api.event.controller;

import com.study.event.api.event.dto.request.EventUserSaveDto;
import com.study.event.api.event.service.EventUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController  //리액트면 무조건~!
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class EventUserController {
    private final EventUserService eventUserService;

    //이메일 중복 확인 api
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(String email){
        boolean isDuplicate = eventUserService.checkEmailDuplicate(email);
//중복된 이메일이 아니면 인증코드메일 전송

        return ResponseEntity.ok().body(isDuplicate);
    }

    //인증코드 검증 API
    @GetMapping("/code")
    public ResponseEntity<?> verifyCode(String email, String code){
        log.info("{}'s verify code is [{}]",email,code);
        //코드 맞는지 여부 체크
        boolean isMatch = eventUserService.isMatchCode(email,code);

        return ResponseEntity.ok().body(isMatch);
    }

    //회원 가입 마무리 처리
    //@RequestBody EventUserSaveDto dto 로 데이터 받기~@!
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody EventUserSaveDto dto){

        log.info("save User Info - {}", dto);
        try {
            eventUserService.confirmSignUp(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());//메시지 400번으로 쏘기!
        }
        return ResponseEntity.ok().body("saved success"); //문자 리턴보단 이런 리턴을 많이 함
    }
}
