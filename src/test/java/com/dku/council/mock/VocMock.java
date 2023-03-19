package com.dku.council.mock;

import com.dku.council.domain.post.model.VocStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.EntityUtil;

public class VocMock {
    public static Voc create(User user, String title, String body) {
        return create(user, title, body, RandomGen.nextLong());
    }

    public static Voc create(User user, String title, String body, Long id) {
        Voc voc = Voc.builder()
                .user(user)
                .title(title)
                .body(body)
                .extraStatus(VocStatus.WAITING)
                .build();
        EntityUtil.injectId(Post.class, voc, id);
        return voc;
    }
}
