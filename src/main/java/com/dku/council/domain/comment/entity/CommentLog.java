package com.dku.council.domain.comment.entity;

import com.dku.council.domain.post.entity.Post;
import com.dku.council.domain.user.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.dku.council.domain.comment.entity.Comment.COMMENT_MAX_LENGTH;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class CommentLog extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "comment_log_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = COMMENT_MAX_LENGTH)
    private String text;


    @Builder
    private CommentLog(Post post, User user, String text) {
        this.post = post;
        this.user = user;
        this.text = text;
    }
}
