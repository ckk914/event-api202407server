package com.study.event.api.event.controller;

import com.study.event.api.event.dto.request.EventSaveDto;
import com.study.event.api.event.dto.response.EventOneDto;
import com.study.event.api.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin
public class EventController {

    private final EventService eventService;

    // 전체 조회 요청
    @GetMapping("/page/{pageNo}")
    public ResponseEntity<?> getList(

            //토큰 파싱 결과로 로그인에 성공한 회원의ㅏ pk
            @AuthenticationPrincipal String userId,
            @RequestParam(required = false) String sort,
            @PathVariable int pageNo
    ) throws InterruptedException {

        log.info("👽token user id: {}",userId);

        if (sort == null) {
            return ResponseEntity.badRequest().body("sort 파라미터가 없습니다.");
        }

        Map<String, Object> events = eventService.getEvents(pageNo, sort,userId);

        // 의도적으로 2초간의 로딩을 설정
        Thread.sleep(2000);

        return ResponseEntity.ok().body(events);
    }

    // 등록 요청
    @PostMapping
    public ResponseEntity<?> register(
            //JWTAuthFiltyer에서 시큐리티에 등록한 아이디
            @AuthenticationPrincipal String userId,
            @RequestBody EventSaveDto dto) {
        eventService.saveEvent(dto,userId);
        return ResponseEntity.ok().body("event saved!");
    }

    // 단일 조회 요청
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