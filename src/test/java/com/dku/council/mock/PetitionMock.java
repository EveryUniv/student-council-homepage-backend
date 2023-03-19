package com.dku.council.mock;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import com.dku.council.util.EntityUtil;
import com.dku.council.util.FieldReflector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PetitionMock {
    public static Petition createWithDummy() {
        return create(UserMock.createDummyMajor(), "Title", "Body", RandomGen.nextLong());
    }

    public static Petition create(User user, LocalDateTime createdAt) {
        Petition petition = create(user, "Title", "Body", RandomGen.nextLong());
        FieldReflector.inject(BaseEntity.class, petition, "createdAt", createdAt);
        return petition;
    }

    public static Petition create(User user, String title, String body) {
        return create(user, title, body, RandomGen.nextLong());
    }

    public static Petition create(User user, String title, String body, Long id) {
        Petition petition = Petition.builder()
                .user(user)
                .title(title)
                .body(body)
                .answer("Answer")
                .views(200)
                .extraStatus(PetitionStatus.ACTIVE)
                .build();
        EntityUtil.injectId(Post.class, petition, id);
        FieldReflector.inject(BaseEntity.class, petition, "createdAt", LocalDateTime.of(2022, 3, 3, 12, 0));
        return petition;
    }

    public static List<Petition> createListDummy(String prefix, int size) {
        List<Petition> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(create(UserMock.createDummyMajor(), prefix + i, prefix + i));
        }

        return result;
    }
}
