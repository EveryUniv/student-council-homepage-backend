package com.dku.council.domain.user;

import com.dku.council.domain.UserRole;
import com.dku.council.global.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "DKU_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Builder
    private User(UserRole role) {
        this.userRole = role;
    }
}
