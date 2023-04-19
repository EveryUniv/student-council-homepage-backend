package com.dku.council.domain.ticket.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private int totalTickets;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    public TicketEvent(String name, LocalDateTime begin, LocalDateTime end, int totalTickets) {
        this.name = name;
        this.begin = begin;
        this.end = end;
        this.totalTickets = totalTickets;
    }
}
