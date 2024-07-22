package com.study.event.api.event.controller;

import com.study.event.api.event.dto.request.EventUserSaveDto;
import com.study.event.api.event.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService uploadService;

    // 파일 업로드 처리
    @PostMapping("/file/upload")
    public ResponseEntity<?> upload(
            @RequestPart(value = "userData", required = false) EventUserSaveDto dto,
            @RequestPart(value = "profileImage") MultipartFile uploadFile
    ) {

        log.info("userData: {}", dto);
        log.info("profileImage: {}", uploadFile.getOriginalFilename());

        // 파일을 업로드
        String fileUrl = "";
        try {
            fileUrl = uploadService.uploadProfileImage(uploadFile);
        } catch (IOException e){
            log.warn("파일 업로드에 실패했습니다.");
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body(fileUrl);
    }
}
