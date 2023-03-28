package com.dku.council.domain.timetable.repository.spec;

import com.dku.council.domain.timetable.model.entity.Lecture;
import org.springframework.data.jpa.domain.Specification;

public class LectureSpec {
    public static <T extends Lecture> Specification<T> withTitle(String keyword) {
        if (keyword == null) {
            return Specification.where(null);
        }

        String pattern = "%" + keyword + "%";
        return (root, query, builder) -> builder.like(root.get("name"), pattern);
    }
}
