package com.study.event.api.event.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.event.api.event.entity.Event;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDetailDto {
    private String id;
    private String title;
    @JsonFormat(pattern = "yyyy년 MM월 dd일")
    private LocalDate startDate;
    @JsonProperty(value="img-url")
    private String imgUrl;

    public  EventDetailDto(Event event){
        this.id = event.getId().toString();
        this.title = event.getTitle();
        this.startDate = event.getDate();
        this.imgUrl= event.getImage();
    }


}
