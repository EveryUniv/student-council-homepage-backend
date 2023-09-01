package com.dku.council.domain.homebus.model.entity;

import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class HomeBusCancelRequest extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "cancel_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    private HomeBusTicket ticket;

    private Long busId;

    private String depositor;

    private String accountNum;

    private String bankName;



    @Builder
    private HomeBusCancelRequest(HomeBusTicket ticket, Long busId, String depositor, String accountNum, String bankName) {
        this.ticket = ticket;
        this.busId = busId;
        this.depositor = depositor;
        this.accountNum = accountNum;
        this.bankName = bankName;
    }


    public void changeTicketToDummy() {
        this.ticket = null;
    }
}
