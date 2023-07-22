package com.dku.council.domain.homebus.model.entity;

import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.base.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class HomeBusTicket extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "ticket_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bus_id")
    private HomeBus bus;

    @Enumerated(STRING)
    private HomeBusStatus status;
    private String approvalName;

    @Builder
    private HomeBusTicket(User user, HomeBus bus, HomeBusStatus status) {
        this.user = user;
        this.bus = bus;
        this.status = status;
    }

    public void issue(String adminName) {
        this.status = HomeBusStatus.ISSUED;
        this.approvalName = adminName;
    }

    public void cancel(String adminName){
        this.status = HomeBusStatus.CANCELLED;
        this.approvalName = adminName;
    }

    public void requestCancel(){
        this.status = HomeBusStatus.NEED_CANCEL_APPROVAL;
    }
}
