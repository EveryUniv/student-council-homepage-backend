package com.dku.council.domain.ticket.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TicketEvent extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "event_id")
    private Long id;

    private String name;

    private LocalDateTime begin;

    private LocalDateTime end;

    private int available;

    public TicketEvent(String name, LocalDateTime begin, LocalDateTime end, int available) {
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.available = available;
    }
}
