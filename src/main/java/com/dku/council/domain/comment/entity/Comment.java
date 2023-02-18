package com.dku.council.domain.comment.entity;

import com.dku.council.domain.comment.CommentStatus;
import com.dku.council.domain.post.entity.Post;
import com.dku.council.domain.user.User;
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
    public static final int COMMENT_MAX_LENGTH = 100;

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

    @Column(length = COMMENT_MAX_LENGTH)
    private String text;


    @Builder
    private Comment(CommentStatus status, Post post, User user, String text) {
        this.status = status;
        this.post = post;
        this.user = user;
        this.text = text;
    }
}
