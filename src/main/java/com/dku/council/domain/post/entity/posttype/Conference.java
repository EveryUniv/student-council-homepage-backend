package com.dku.council.domain.post.entity.posttype;

import com.dku.council.domain.category.Category;
import com.dku.council.domain.post.entity.Post;
import com.dku.council.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDate;

import static lombok.AccessLevel.PROTECTED;

/**
 * 회의록 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Conference extends Post {

    /**
     * 회의록 회차
     */
    private int round;

    private LocalDate date;

    @Builder
    private Conference(User user, String title, String body, Category category, int views, int round, LocalDate date) {
        super(user, title, body, category, views);
        this.round = round;
        this.date = date;
    }
}
