package com.dku.council.mock;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.FieldInjector;

import java.util.ArrayList;
import java.util.List;

public class NewsMock {
    public static List<News> createList(String prefix, int size) {
        return createList(prefix, UserMock.create(), size);
    }

    public static List<News> createList(String prefix, User user, int size) {
        List<News> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            News news = News.builder()
                    .user(user)
                    .title(prefix + i)
                    .body("")
                    .build();
            result.add(news);
        }

        return result;
    }

    public static News create() {
        return create(UserMock.create(), RandomGen.nextLong());
    }

    public static News create(Long newsId) {
        return create(UserMock.create(), newsId);
    }

    public static News create(User user, Long newsId) {
        News news = News.builder()
                .user(user)
                .title("")
                .body("")
                .build();
        FieldInjector.injectId(Post.class, news, newsId);
        return news;
    }
}
