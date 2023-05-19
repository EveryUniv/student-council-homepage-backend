package com.dku.council.domain.comment.model.entity;

import com.dku.council.domain.comment.model.CommentStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @Enumerated(STRING)
    private CommentStatus status;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    private String text;


    @Builder
    private Comment(User user, String text) {
        this.status = CommentStatus.ACTIVE;
        this.user = user;
        this.text = text;
    }

    public void updateText(String text) {
        this.text = text;
    }

    public void updateStatus(CommentStatus status) {
        this.status = status;
    }

    public void changePost(Post post) {
        if (this.post != null) {
            this.post.getComments().remove(this);
        }

        this.post = post;
        this.post.getComments().add(this);
    }

    public void changeUser(User user) {
        this.user = user;
    }
}
