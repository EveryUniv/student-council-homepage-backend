package com.dku.council.domain.ticket.model;

public enum TicketStatus {
    /**
     * 티켓 발행 전 상태입니다.
     * 선정자가 아닌 경우에는 이 상태로 남게 됩니다.
     */
    WAITING,

    /**
     * 선정되어 티켓이 발행되었고, 실물 티켓을 발급받기 전입니다.
     */
    ISSUABLE,

    /**
     * 실물 티켓까지 발급받은 상태입니다.
     */
    ISSUED;

    /**
     * 대상자로 선정되지 않았는지 확인합니다.
     *
     * @return 티켓 발급 대상자가 아닌 경우 true를 반환합니다.
     */
    public boolean hasNoTicket() {
        return this != ISSUABLE && this != ISSUED;
    }
}
