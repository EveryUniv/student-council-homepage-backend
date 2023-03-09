package com.dku.council.domain.rental.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class RentalItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int remaining;

    private boolean isActive;

    @OneToMany(mappedBy = "item")
    private List<Rental> rentals = new ArrayList<>();


    public RentalItem(String name, int remaining) {
        this.name = name;
        this.remaining = remaining;
        this.isActive = true;
    }

    public void markAsDeleted() {
        isActive = false;
        for (Rental rental : rentals) {
            rental.markAsDeleted();
        }
    }

    public void increaseRemaining() {
        this.remaining++;
    }

    public void decreaseRemaining() {
        this.remaining--;
    }

    public void updateItemName(String itemName) {
        this.name = itemName;
    }

    public void updateAvailable(int available) {
        this.remaining = available;
    }
}
