package com.dku.council.domain.admin.dto;

import com.dku.council.domain.homebus.model.HomeBusStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CancelApprovalTicketsDto {
    private Long ticketId;

    private String userName;

    private String busLabel;

    private HomeBusStatus status;

    private String deposition;

    private String accountNum;

    private String bankName;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    public CancelApprovalTicketsDto(Long ticketId, String userName, String busLabel, HomeBusStatus status, String deposition, String accountNum, String bankName, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.ticketId = ticketId;
        this.userName = userName;
        this.busLabel = busLabel;
        this.status = status;
        this.deposition = deposition;
        this.accountNum = accountNum;
        this.bankName = bankName;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
