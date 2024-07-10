package com.study.event.api.event.controller;

import com.study.event.api.event.service.EventUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        if(!isDuplicate){
            eventUserService.sendVerificationEmail(email);
        }
        return ResponseEntity.ok().body(isDuplicate);
    }
}
