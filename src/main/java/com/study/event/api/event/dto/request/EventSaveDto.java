package com.study.event.api.event.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.event.api.event.entity.Event;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSaveDto {

    private String title;
    private String desc;
    private String imageUrl;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;

    // 엔터티로 변경하는 유틸 메서드
    public Event toEntity() {

        return Event.builder()
                .title(this.title)
                .description(this.desc)
                .image(this.imageUrl)
                .date(this.beginDate)
                .build();
    }
}
