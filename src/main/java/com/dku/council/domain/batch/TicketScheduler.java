package com.dku.council.domain.batch;

import com.dku.council.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketScheduler {
    private final TicketService ticketService;

    @Scheduled(fixedDelayString = "${app.ticket.dump-delay}")
    public void dumpToDb() {
        ticketService.dumpToDb();
    }
}
