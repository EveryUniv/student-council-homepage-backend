package com.dku.council.domain.post.model.entity.posttype;

import com.dku.council.domain.category.model.entity.Category;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;

import static lombok.AccessLevel.PROTECTED;

/**
 * 총학소식 Entity
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class News extends Post {

    @Builder
    private News(@NonNull User user,
                 @NonNull String title,
                 @NonNull String body,
                 Category category, int views) {
        super(user, title, body, category, views);
    }
}
