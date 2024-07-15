package com.study.event.api.event.repository;

import com.study.event.api.event.entity.EmailVerification;
import com.study.event.api.event.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
    //유저 정보를 통해 인증 코드 정보를 탐색
    //🫡🌟findByEventUser(필드명) 가져올 필드명 EventUser : 알아서 꺼내서 탐색함~!
    // 잘 안되면 쿼리 dsl로 해도 됨!
    Optional<EmailVerification> findByEventUser(EventUser eventUser);
}
