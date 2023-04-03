package com.dku.council.mock;

import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import com.dku.council.util.EntityUtil;
import com.dku.council.util.FieldReflector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GeneralForumMock {
    public static GeneralForum create(User user) {
        return create(user, null);
    }

    public static GeneralForum create(User user, Long id) {
        GeneralForum post = GeneralForum.builder()
                .user(user)
                .title("title")
                .body("body")
                .build();

        if (id != null) {
            EntityUtil.injectId(Post.class, post, id);
        }

        return post;
    }

    public static List<GeneralForum> createListDummy(String prefix, int size) {
        return createList(prefix, UserMock.createDummyMajor(), size, true);
    }

    public static List<GeneralForum> createList(String prefix, User user, int size, boolean enabled) {
        List<GeneralForum> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            GeneralForum forum = GeneralForum.builder()
                    .user(user)
                    .title(prefix + i)
                    .body(Integer.toString(i))
                    .build();
            if (!enabled) {
                FieldReflector.inject(Post.class, forum, "status", PostStatus.DELETED);
            }
            FieldReflector.inject(BaseEntity.class, forum, "createdAt", LocalDateTime.of(2022, 3, 3, 3, 3));
            result.add(forum);
        }
        return result;
    }
}
