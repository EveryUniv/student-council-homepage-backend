package com.dku.council.domain.post.entity.posttype;

import com.dku.council.domain.category.Category;
import com.dku.council.domain.post.PetitionStatus;
import com.dku.council.domain.post.PostStatus;
import com.dku.council.domain.post.entity.Post;
import com.dku.council.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private PetitionStatus petitionStatus;

    @Lob
    private String answer;

    @Builder
    private Petition(User user, String title, String body, Category category, int views, PetitionStatus petitionStatus, String answer) {
        super(user, title, body, category, views);
        this.petitionStatus = petitionStatus;
        this.answer = answer;
    }
}
