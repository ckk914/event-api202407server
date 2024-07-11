package com.study.event.api.event.repository;

import com.study.event.api.event.entity.EmailVerification;
import com.study.event.api.event.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
    //유저 정보를 통해 인증 코드 정보를 탐색
    Optional<EmailVerification> findByEventUser(EventUser eventUser);  //findByEventUser(객체명)
}
