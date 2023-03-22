package com.dku.council.domain.post.model.entity.posttype;

import com.dku.council.domain.post.model.VocStatus;
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
 * VOC게시판 Entity.
 */
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Voc extends Post {

    @Enumerated(STRING)
    private VocStatus extraStatus;

    @Lob
    private String answer;

    @Builder
    private Voc(@NonNull User user,
                @NonNull String title,
                @NonNull String body,
                int views, VocStatus extraStatus, String answer) {
        super(user, title, body, views);
        this.extraStatus = extraStatus;
        this.answer = answer;
    }

    public static VocBuilder builder() {
        return new VocBuilder().extraStatus(VocStatus.WAITING);
    }

    public void replyAnswer(String answer) {
        this.answer = answer;
    }

    public void updateVocStatus(VocStatus status) {
        this.extraStatus = status;
    }

    @Override
    public String getDisplayingUsername() {
        return getUser().getNickname().charAt(0) + "*******";
    }
}
