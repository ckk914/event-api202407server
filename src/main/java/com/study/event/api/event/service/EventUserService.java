package com.study.event.api.event.service;

import com.study.event.api.auth.TokenProvider;
import com.study.event.api.event.dto.request.EventUserSaveDto;
import com.study.event.api.event.dto.request.LoginRequestDto;
import com.study.event.api.event.dto.response.LoginResponseDto;
import com.study.event.api.event.entity.EmailVerification;
import com.study.event.api.event.entity.EventUser;
import com.study.event.api.event.repository.EmailVerificationRepository;
import com.study.event.api.event.repository.EventUserRepository;
import com.study.event.api.exception.LoginFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional                      //데이터베이스 처리나 뭐 하나라도 잘못되면 롤백!
public class EventUserService {

    @Value("${study.mail.host}")
    private String mailHost;

    private final EventUserRepository eventUserRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    // 이메일 전송 객체
    private final JavaMailSender mailSender;

    // 패스워드 암호화 객체
    private final PasswordEncoder encoder;

    //토큰 생성 객체
    private final TokenProvider tokenProvider;

    // 이메일 중복확인 처리
    public boolean checkEmailDuplicate(String email) {

        boolean exists = eventUserRepository.existsByEmail(email);
        log.info("Checking email {} is duplicate : {}", email, exists);

        //중복인데 회원 가입이 마무리 되지 않은 회원은 중복이 아니라고 판단
        if(exists && notFinish(email)){
            //인증 메일 재발송

            return false;
        }

        // 일련의 데이터베이스 처리 (후속 처리) (데이터베이스 처리, 이메일 보내는 것...)
        if (!exists) processSignUp(email);

        return exists;
    }

    private boolean notFinish(String email) {
        EventUser eventUser = eventUserRepository.findByEmail(email).orElseThrow();

        //인증이 안되었고, 패스워드가 없는 경우
        if(!eventUser.isEmailVerified() || eventUser.getPassword() == null ){
       //기존 인증코드가 있는 경우 삭제
            EmailVerification ev = emailVerificationRepository.findByEventUser(eventUser).orElse(null);

            //기존꺼 있는 경우 삭제
            if(ev != null)  emailVerificationRepository.delete(ev);


            //인증코드 재발송
        generateAndSendCode(email, eventUser);
        return true;
        }
        return false;
    }

    //일련의 후속 처리 진행~!
    public void processSignUp(String email) {

        // 1. 임시 회원가입
        //이벤트 유저 생성! (이메일 정보 넣음!)
        EventUser newEventUser = EventUser
                .builder()
                .email(email)
                .build();

        //저장된 아이디
        EventUser savedUser = eventUserRepository.save(newEventUser);  //save해야 아이디  생김!

        //이메일 코드 전송 & . 인증 코드 정보를 데이터베이스에 저장
        generateAndSendCode(email, savedUser);

    }

    //이메일 코드 전송 & . 인증 코드 정보를 데이터베이스에 저장
    private void generateAndSendCode(String email, EventUser eventUser) {
        // 2. 이메일 인증 코드 발송 (검증코드생성 포함)
        String code = sendVerificationEmail(email);

        // 3. 인증 코드 정보를 데이터베이스에 저장
        EmailVerification verification = EmailVerification.builder()
                .verificationCode(code) // 인증 코드
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // 만료 시간 (5분 뒤)
                .eventUser(eventUser) // FK : 객체로 주면 알아서 빼서 읽음
                .build();

        emailVerificationRepository.save(verification); //데이터 베이스에 저장
    }

    // 이메일 인증 코드 보내기
    public String sendVerificationEmail(String email) {

        // 검증 코드 생성하기
        String code = generateVerificationCode();

        // 이메일을 전송할 객체 생성
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 누구에게 이메일을 보낼 것인지
            messageHelper.setTo(email);
            // 이메일 제목 설정
            messageHelper.setSubject("[인증메일] 중앙정보스터디 가입 인증 메일입니다.");
            // 이메일 내용 설정
            messageHelper.setText(
                    "인증 코드: <b style=\"font-weight: 700; letter-spacing: 5px; font-size: 30px;\">" + code + "</b>"
                    , true
            );

            // 전송자의 이메일 주소
            messageHelper.setFrom(mailHost);

            // 이메일 보내기 🌟
            mailSender.send(mimeMessage);

            log.info("{} 님에게 이메일 전송!", email);

            return code;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 검증 코드 생성 로직 1000~9999 사이의 4자리 숫자
    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }

    // 인증코드 체크
    // 인증코드가 있고 , 만료 시간이 지나지 않았고, 코드번호가 일치할 경우
    public boolean isMatchCode(String email, String code) {

        // 이메일을 통해 회원정보를 탐색
        EventUser eventUser = eventUserRepository.findByEmail(email)
                .orElse(null);

        if (eventUser != null) {
            // 인증코드가 있는지 탐색
            EmailVerification ev = emailVerificationRepository.findByEventUser(eventUser).orElse(null);

            // 인증코드가 있고
            //만료시간이 지나지 않았고
            // 코드번호가 일치할 경우⭐️
            if (
                    ev != null
                            && ev.getExpiryDate().isAfter(LocalDateTime.now())
                            && code.equals(ev.getVerificationCode())
            ) {
                // 이메일 인증여부 true로 수정
                eventUser.setEmailVerified(true);   // 인증 끝난걸로 처리
                eventUserRepository.save(eventUser); // UPDATE

                // 인증코드 데이터베이스에서 삭제
                emailVerificationRepository.delete(ev);   //인증코드 제거
                return true;
            } else {  // 인증코드가 틀렸거나 만료된 경우
                // 인증코드 재발송
                // 원래 인증 코드 삭제
                emailVerificationRepository.delete(ev);

                // 새인증코드 발급 이메일 재전송
                // 데이터베이스에 새 인증코드 저장
                generateAndSendCode(email, eventUser);
                return false;
            }

        }
        return false;
    }

    // 회원가입 마무리
    public void confirmSignUp(EventUserSaveDto dto) {

        // 기존 회원 정보 조회
        EventUser foundUser = eventUserRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(  //throw!@
                        () -> new RuntimeException("회원 정보가 존재하지 않습니다.")
                );

        // 데이터 반영 (패스워드, 가입시간)
        String password = dto.getPassword();
        String encodedPassword = encoder.encode(password); // 암호화

        //데이터 반영
        foundUser.confirm(encodedPassword);
        eventUserRepository.save(foundUser);
    }

    //회원 인증 처리 (login)
    //매개변수 final은 약간 전달받은 그대로 써라 느낌! (세이프티 코딩)
    public LoginResponseDto authenticate(final LoginRequestDto dto){

        //이메일을 통해 회원 정보 조회
        EventUser eventUser = eventUserRepository.findByEmail(dto.getEmail()).orElseThrow(
                ()-> new LoginFailException("가입된 회원이 아닙니다.")
        );

        //이메일 인증을 안했거나, 패스워드를 설정하지 않은 회원
        if(!eventUser.isEmailVerified() || eventUser.getPassword()==null){  //해당 조건을 db로 관리하면 더 편할 수 있음.!
            throw new LoginFailException("회원가입이 중단된 회원입니다. 다시 가입해주세요.");
        }
        //패스워드 검증
        String inputPassword = dto.getPassword();
        String encodedPassword = eventUser.getPassword();

        if(!encoder.matches(inputPassword, encodedPassword)){
            throw new LoginFailException("비밀번호가 틀렸습니다.");
        }

        //로그인 성공
        //인증 정보를 어떻게 관리할 것인가?⭐️
        //기존 => 세션 저장
        //현재는 쓸 수 없다. => //서버 클라이언트 위치가 다르다
        // 현재 : 포트  3000 거쳐서 8686 으로 들어오는 상태
        // 서버 1대의 좋은 서버 보다 100대의 안좋은 서버가 좋다.!
        // ㄴ 세션으로 하면 인증을 100군데서 하기는 말이 안됨
        // ㄴ 쿠키 : 브라우저 아니면 안됨
        // ㄴ 세션 : 서버간 공유가 힘들다 , 쿠키: 브라우저만 되니, 모바일에선 안된다...
        // ㄴ 토큰


        //로그인 성공
        //인증 정보를 클라이언트에게 전송
        //관리 방법 : 세션  || 쿠키 || 토큰
        //인증 정보 (이메일, 닉네임, 프사, 토큰 정보)를 클라이언트에게 전송

        //토큰 생성
        String token = tokenProvider.createToken(eventUser);

        return LoginResponseDto.builder()
                .email(eventUser.getEmail())
                .role(eventUser.getRole().toString())
                .token(token)  //토큰 포함
                .build();

    }
}
