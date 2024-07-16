package com.study.event.api.event.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.event.api.event.entity.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.study.event.api.event.entity.QEvent.event;
import static com.study.event.api.event.entity.QEventUser.eventUser;


@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public Page<Event> findEvents(Pageable pageable, String sort, String userId) {

        // 페이징을 통한 조회
        List<Event> eventList = factory
                .selectFrom(event)
                .where(event.eventUser.id.eq(userId))
                .orderBy(specifier(sort))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 데이터 수 조회
        Long count = factory
                .select(event.count())
                .where(event.eventUser.id.eq(userId))
                .from(event)
                .fetchOne();

        return new PageImpl<>(eventList, pageable, count);
    }

    // 정렬 조건을 처리하는 메서드
    private OrderSpecifier<?> specifier(String sort) {
        switch (sort) {
            case "date":
                return event.date.desc();
            case "title":
                return event.title.asc();
            default:
                return null;
        }
    }
}