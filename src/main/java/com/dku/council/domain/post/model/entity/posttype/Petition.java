package com.dku.council.domain.post.model.entity.posttype;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.user.model.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Lob;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

/**
 * 청원게시판 Entity.
 * Blind는 {@link PostStatus}로 대신한다. {@link PostStatus#DELETED_BY_ADMIN}이 Blind 상태를 의미한다.
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Petition extends Post {

    @Enumerated(STRING)
    private PetitionStatus extraStatus;

    @Lob
    private String answer;

    @Builder
    private Petition(@NonNull User user,
                     @NonNull String title,
                     @NonNull String body,
                     int views,
                     PetitionStatus extraStatus,
                     String answer) {
        super(user, title, body, views);
        this.extraStatus = extraStatus;
        this.answer = answer;
    }

    public static PetitionBuilder builder() {
        return new PetitionBuilder().extraStatus(PetitionStatus.ACTIVE);
    }

    public void replyAnswer(String answer) {
        this.answer = answer;
    }

    public void updatePetitionStatus(PetitionStatus status) {
        this.extraStatus = status;
    }

    @Override
    public String getDisplayingUsername() {
        User user = getUser();
        if (!user.getStatus().isActive()) {
            return User.DELETED_USER;
        }
        return user.getMajor().getDepartment();
    }
}
