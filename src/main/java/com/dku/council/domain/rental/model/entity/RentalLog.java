package com.dku.council.domain.rental.model.entity;

import com.dku.council.domain.rental.model.RentalUserClass;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RentalLog extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "rental_log_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String itemName;

    @Enumerated(STRING)
    private RentalUserClass userClass;

    private LocalDateTime rentalStart;

    private LocalDateTime rentalEnd;

    private String title;

    @Lob
    private String body;


    @Builder
    private RentalLog(User user, String itemName, RentalUserClass userClass, LocalDateTime rentalStart, LocalDateTime rentalEnd, String title, String body) {
        this.user = user;
        this.itemName = itemName;
        this.userClass = userClass;
        this.rentalStart = rentalStart;
        this.rentalEnd = rentalEnd;
        this.title = title;
        this.body = body;
    }
}
