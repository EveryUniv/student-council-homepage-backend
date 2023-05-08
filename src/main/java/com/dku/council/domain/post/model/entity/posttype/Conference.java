package com.dku.council.domain.post.model.entity.posttype;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Transient;
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

    /**
     * 회의록 개최 일자
     */
    private LocalDate date;

    @Builder
    private Conference(@NonNull User user,
                       @NonNull String title,
                       @NonNull String body,
                       @NonNull LocalDate date,
                       int views, int round) {

        super(user, title, body, views);
        this.round = round;
        this.date = date;
    }

    @Override
    @Transient
    public String getDisplayingUsername() {
        return User.ANONYMITY;
    }
}
