package com.dku.council.domain.post.model.entity;

import com.dku.council.domain.comment.model.CommentStatus;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.report.model.entity.Report;
import com.dku.council.domain.tag.model.entity.PostTag;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.InheritanceType.SINGLE_TABLE;
import static lombok.AccessLevel.PROTECTED;

/**
 * Post BaseEntity.
 * 이 클래스를 상속받으면 회칙, 회의록 등과 같은 새로운 타입을 만들 수 있다.
 * 상속받고 필요한대로 확장해서 사용하면 된다.
 */
@Entity
@Getter
@DynamicUpdate
@Inheritance(strategy = SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@NoArgsConstructor(access = PROTECTED)
public abstract class Post extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<PostTag> postTags = new ArrayList<>();

    private String title;

    @Lob
    private String body;

    @Enumerated(STRING)
    private PostStatus status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<PostFile> files = new ArrayList<>();

    @Where(clause = "status = '" + CommentStatus.ACTIVE_NAME + "'")
    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    private int views;


    protected Post(User user, String title, String body, int views) {
        this.user = user;
        this.title = title;
        this.body = body;
        this.views = views;
        this.status = PostStatus.ACTIVE;
    }

    public void increaseViewCount() {
        views++;
    }

    public void markAsDeleted(boolean byAdmin) {
        this.status = byAdmin ? PostStatus.DELETED_BY_ADMIN : PostStatus.DELETED;
    }

    public void blind() {
        this.status = PostStatus.BLINDED;
    }

    public void unblind() {
        this.status = PostStatus.ACTIVE;
    }

    public abstract String getDisplayingUsername();

    public int getReportedCount() {
        return reports.size();
    }

    public boolean isBlinded() {
        return status == PostStatus.BLINDED;
    }

    public void changeUser(User user) {
        this.user = user;
    }
}