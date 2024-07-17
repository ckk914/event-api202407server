package com.study.event.api.event.controller;

import com.study.event.api.auth.TokenProvider;
import com.study.event.api.event.dto.request.EventSaveDto;
import com.study.event.api.event.dto.response.EventOneDto;
import com.study.event.api.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.study.event.api.auth.TokenProvider.*;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    // 전체 조회 요청
    @GetMapping("/page/{pageNo}")
    public ResponseEntity<?> getList(
            // 토큰파싱 결과로 로그인에 성공한 회원의 PK
            @AuthenticationPrincipal TokenUserInfo tokenInfo,
            @RequestParam(required = false) String sort,
            @PathVariable int pageNo
    ) throws InterruptedException {

        log.info("token info : {}", tokenInfo);

        if (sort == null) {
            return ResponseEntity.badRequest().body("sort 파라미터가 없습니다.");
        }

        Map<String, Object> events = eventService.getEvents(pageNo, sort, tokenInfo.getUserId());

        // 의도적으로 2초간의 로딩을 설정
//        Thread.sleep(2000);

        return ResponseEntity.ok().body(events);
    }

    // 등록 요청
    @PostMapping
    public ResponseEntity<?> register(
            // JwtAuthFilter에서 시큐리티에 등록한 데이터
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody EventSaveDto dto
    ) {

        try {
            eventService.saveEvent(dto, userInfo.getUserId());
            return ResponseEntity.ok().body("event saved!");
        } catch (IllegalStateException e) {
            log.warn(e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 단일 조회 요청
    @PreAuthorize("hasAuthority('PREMIUM') or hasAuthority('ADMIN')")   //or has     붙여서 이어붙일 수 있음!
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable Long eventId) {

        if (eventId == null || eventId < 1) {
            String errorMessage = "eventId가 정확하지 않습니다.";
            log.warn(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }

        EventOneDto eventDetail = eventService.getEventDetail(eventId);

        return ResponseEntity.ok().body(eventDetail);
    }

    // 삭제 요청
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> delete(@PathVariable Long eventId) {

        eventService.deleteEvent(eventId);

        return ResponseEntity.ok().body("event deleted!");
    }

    // 수정 요청
    @PatchMapping("/{eventId}")
    public ResponseEntity<?> modify(
            @RequestBody EventSaveDto dto,
            @PathVariable Long eventId
    ) {
        eventService.modifyEvent(dto, eventId);

        return ResponseEntity.ok().body("event modified!!");
    }


}