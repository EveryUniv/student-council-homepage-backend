package com.dku.council.mock;

import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.FieldInjector;

import java.util.ArrayList;
import java.util.List;

public class CommentMock {

    public static List<Comment> createList(Post post, List<User> user, int size) {
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            comments.add(create(null, post, user.get(i)));
        }
        return comments;
    }

    public static Comment createWithId(User user) {
        return createWithId(NewsMock.create(), user);
    }

    public static Comment createWithId(Post post, User user) {
        return create(RandomGen.nextLong(), post, user);
    }

    public static Comment create(Post post, User user) {
        return create(null, post, user);
    }

    public static Comment create(Long id, Post post, User user) {
        Comment comment = Comment.builder()
                .user(user)
                .text(RandomGen.nextUUID())
                .build();
        comment.changePost(post);
        if (id != null) {
            FieldInjector.injectId(Comment.class, comment, id);
        }
        return comment;
    }
}
