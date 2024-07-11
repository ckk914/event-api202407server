package com.study.event.api.event.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name="tbl_event_user")
public class EventUser {
    @Id
    @GenericGenerator(strategy = "uuid2",name = "uuid_generator") //uuid2 전략 적용!
    @GeneratedValue(generator = "uuid-generator")
    @Column(name="ev_user_id")
    private String id;  //회원 계쩡 아니고 랜덤 pk

    //실제 사용자가 쓰는 것은 email + password~!🌟
    //이름, 낫널, 유니크
    @Column(name="ev_user_email", nullable = false, unique = true)
    private String email; //회원 계정

    //낫널로 하지 않는 이유 : 1. sns로그인한 회원
    //                                  2. 인증번호만 받고 회원가입을 완료하지 않은 사람 처리
    @Column(length = 500)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.COMMON;  //권한

    private LocalDateTime createAt;  //회원가입 시간- 마지막에 완료 할때 추가@

    //이메일 인증을 완료 했는지 여부
    //엔터티에 boolean 타입을 사용하면 실제 DB에는 0, 1 로 저장됨에 주의⭐️
    @Setter
    @Column(nullable = false)
    private boolean emailVerified;

    public void confirm(String password) {

            this.password = password;
            this.createAt = LocalDateTime.now();

    }
}
