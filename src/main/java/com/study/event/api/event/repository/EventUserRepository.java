package com.study.event.api.event.repository;

import com.study.event.api.event.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventUserRepository extends JpaRepository<EventUser,String> {

    //query method로 jpql 생성
    boolean existsByEmail(String email);   //existBy필드명

    //널 방지!~
    Optional<EventUser> findByEmail(String email);
}
