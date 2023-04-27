package com.dku.council.mock;

import com.dku.council.domain.ticket.model.entity.Ticket;

public class TicketMock {

    public static Ticket createDummy() {
        return new Ticket(
                UserMock.createDummyMajor(),
                TicketEventMock.createDummy(),
                10
        );
    }

    public static Ticket createDummyIssuable() {
        Ticket ticket = new Ticket(
                UserMock.createDummyMajor(),
                TicketEventMock.createDummy(),
                10
        );
        ticket.markAsIssuable();
        return ticket;
    }
}
