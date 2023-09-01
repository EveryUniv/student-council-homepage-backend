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
public class CheonanMajorFilter {

    @Id
    @GeneratedValue
    @Column(name = "major_id")
    private Long id;

    @Column(unique = true)
    private String filter;

    public CheonanMajorFilter(String filter) {
        this.filter = filter;
    }
}
