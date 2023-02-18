package com.dku.council.domain.post.entity.posttype;

import com.dku.council.domain.category.Category;
import com.dku.council.domain.post.entity.Post;
import com.dku.council.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

import static lombok.AccessLevel.PROTECTED;

/**
 * 회칙 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Rule extends Post {

    @Builder
    private Rule(User user, String title, String body, Category category, int views) {
        super(user, title, body, category, views);
    }
}
