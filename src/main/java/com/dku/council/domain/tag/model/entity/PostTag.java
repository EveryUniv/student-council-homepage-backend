package com.dku.council.domain.tag.model.entity;

import com.dku.council.domain.post.model.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class PostTag {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public PostTag(Tag tag) {
        this.tag = tag;
    }

    public void changePost(Post post) {
        if (this.post != null) {
            this.post.getPostTags().remove(this);
        }

        this.post = post;
        this.post.getPostTags().add(this);
    }
}