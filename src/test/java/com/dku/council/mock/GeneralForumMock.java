package com.dku.council.mock;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.EntityUtil;

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
}
