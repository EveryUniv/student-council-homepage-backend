package com.dku.council.domain.user.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Major {

    @Id
    @GeneratedValue
    @Column(name = "major_id")
    private Long id;

    private String name;

    private String department;

    private boolean isActive;

    public Major(String name, String department) {
        this.name = name;
        this.department = department;
        this.isActive = true;
    }
}
