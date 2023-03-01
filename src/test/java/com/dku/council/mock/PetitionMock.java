package com.dku.council.mock;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.util.FieldInjector;

public class PetitionMock {
    public static Petition create(User user, String title, String body) {
        return create(user, title, body, RandomGen.nextLong());
    }

    public static Petition create(User user, String title, String body, Long id) {
        Petition petition = Petition.builder()
                .user(user)
                .title(title)
                .body(body)
                .petitionStatus(PetitionStatus.ACTIVE)
                .build();
        FieldInjector.injectId(Post.class, petition, id);
        return petition;
    }
}
