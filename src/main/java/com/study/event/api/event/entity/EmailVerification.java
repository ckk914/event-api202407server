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
@Table(name = "tbl_email_verification")  //테이블명
public class EmailVerification {

    @Id
    @GenericGenerator(strategy = "uuid2", name = "uuid-generator")  //uuid2 전략 사용
    @GeneratedValue(generator = "uuid-generator")
    @Column(name = "verification_id")
    private String id;

    @Column(nullable = false)      //낫널
    private String verificationCode; // 인증 코드

    @Column(nullable = false)     //낫널
    private LocalDateTime expiryDate; // 인증 만료시간


    //fk 가 저쪿 pk와 다를 경우 referencedColumnName = "ev_user_id")
    @OneToOne    //일대일 관계 (회원 - 인증번호)
    @JoinColumn(name = "event_user_id", referencedColumnName = "ev_user_id")
    private EventUser eventUser;

    /*
        ALTER TABLE tbl_email_verification
        ADD CONSTRAINT fk_dfsd_dfsdsf
        FOREIGN KEY (event_user_id)
        REFERENCES tbl_event_user (ev_user_id)
     */

}
