package com.dku.council.mock;

import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.FieldInjector;

public class CommentMock {

    public static Comment create(User user) {
        return create(NewsMock.create(), user);
    }

    public static Comment create(Post post, User user) {
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .text(RandomGen.nextUUID())
                .build();
        FieldInjector.injectId(Comment.class, comment, RandomGen.nextLong());
        return comment;
    }
}
