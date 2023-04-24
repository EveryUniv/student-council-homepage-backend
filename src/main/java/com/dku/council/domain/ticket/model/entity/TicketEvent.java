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

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private int totalTickets;

    @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    public TicketEvent(String name, LocalDateTime startAt, LocalDateTime endAt, int totalTickets) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.totalTickets = totalTickets;
    }
}
