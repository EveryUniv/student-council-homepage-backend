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
public class Rental extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "rental_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private RentalItem item;

    @Enumerated(STRING)
    private RentalUserClass userClass;

    private LocalDateTime rentalStart;

    private LocalDateTime rentalEnd;

    private String title;

    private String body;

    private boolean isActive;


    @Builder
    private Rental(User user, RentalItem item, RentalUserClass userClass,
                   LocalDateTime rentalStart, LocalDateTime rentalEnd, String title, String body) {
        this.user = user;
        this.item = item;
        this.userClass = userClass;
        this.rentalStart = rentalStart;
        this.rentalEnd = rentalEnd;
        this.title = title;
        this.body = body;
        this.isActive = true;
    }

    public void changeItem(RentalItem item) {
        if (this.item != null) {
            this.item.getRentals().remove(this);
        }

        this.item = item;
        this.item.getRentals().add(this);
    }

    public void markAsDeleted() {
        this.isActive = false;
    }
}
