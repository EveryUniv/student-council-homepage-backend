package com.dku.council.mock;

import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import com.dku.council.util.EntityUtil;
import com.dku.council.util.FieldReflector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsMock {
    public static List<News> createListDummy(String prefix, int size) {
        return createList(prefix, UserMock.createDummyMajor(), size, true);
    }

    public static List<News> createList(String prefix, User user, int size) {
        return createList(prefix, user, size, true);
    }

    public static List<News> createList(String prefix, User user, int size, boolean enabled) {
        List<News> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            News news = News.builder()
                    .user(user)
                    .title(prefix + i)
                    .body(Integer.toString(i))
                    .build();
            if (!enabled) {
                FieldReflector.inject(Post.class, news, "status", PostStatus.DELETED);
            }
            FieldReflector.inject(BaseEntity.class, news, "createdAt", LocalDateTime.of(2022, 3, 3, 3, 3));
            result.add(news);
        }

        return result;
    }

    public static News createDummy() {
        return create(UserMock.createDummyMajor(), RandomGen.nextLong());
    }

    public static News createDummy(Long newsId) {
        return create(UserMock.createDummyMajor(), newsId);
    }

    public static News create(User user) {
        return create(user, null);
    }

    public static News create(User user, Long newsId) {
        News news = News.builder()
                .user(user)
                .title("")
                .body("")
                .build();
        if (newsId != null) {
            EntityUtil.injectId(Post.class, news, newsId);
        }
        return news;
    }
}
