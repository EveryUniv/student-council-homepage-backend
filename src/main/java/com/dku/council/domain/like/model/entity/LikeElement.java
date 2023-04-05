package com.dku.council.domain.like.model.entity;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(indexes = {
        @Index(name = "idx_like_element_id", columnList = "elementId")
},
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_like_element", columnNames = {"user_id", "elementId", "target"})
        }
)
public class LikeElement extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long elementId;

    @Enumerated(STRING)
    private LikeTarget target;

    public LikeElement(User user, Long elementId, LikeTarget target) {
        this.user = user;
        this.elementId = elementId;
        this.target = target;
    }
}
