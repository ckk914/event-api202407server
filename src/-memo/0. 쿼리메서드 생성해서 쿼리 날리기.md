package com.study.event.api.event.repository;

import com.study.event.api.event.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventUserRepository extends JpaRepository<EventUser,String> {

    //query method로 jpql 생성
    boolean existsByEmail(String email);   //existBy필드명
}
