package com.dku.council.domain.user.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class NicknameFilter {

    @Id
    @GeneratedValue
    @Column(name = "filter_id")
    private Long id;

    @Column(unique = true)
    private String word;

    public NicknameFilter(String word) {
        this.word = word;
    }
}
