package com.dku.council.domain.statistic.model.entity;

import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class PetitionStatistic extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "petition_agree_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Petition petition;

    private String department;

    @Builder
    private PetitionStatistic(@NonNull User user,
                              @NonNull Petition petition) {
        this.user = user;
        this.petition =  petition;
        this.department = user.getMajor().getDepartment();
    }
}
