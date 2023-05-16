package com.dku.council.domain.ticket.service;

import com.dku.council.domain.ticket.exception.NoTicketException;
import com.dku.council.domain.ticket.model.dto.response.ResponseManagerTicketDto;
import com.dku.council.domain.ticket.model.dto.response.ResponseTicketDto;
import com.dku.council.domain.ticket.model.entity.Ticket;
import com.dku.council.domain.ticket.repository.TicketRepository;
import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.domain.user.service.UserInfoService;
import com.dku.council.infra.nhn.service.SMSService;
import com.dku.council.mock.TicketMock;
import com.dku.council.mock.UserInfoMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Optional;

import static com.dku.council.domain.ticket.model.TicketStatus.ISSUABLE;
import static com.dku.council.domain.ticket.model.TicketStatus.ISSUED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketVerifyServiceTest {

    @Mock
    private TicketRepository persistenceRepository;

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private SMSService smsService;

    @Mock
    private MessageSource messageSource;

    private static final int DIGIT_COUNT = 6;
    private TicketVerifyService service;


    @BeforeEach
    void beforeEach() {
        service = new TicketVerifyService(persistenceRepository, userInfoService,
                smsService, messageSource, DIGIT_COUNT, true);
    }


    @Test
    @DisplayName("내 티켓 조회")
    void myTicket() {
        // given
        Ticket ticket = TicketMock.createDummyIssuable();
        UserInfo info = UserInfoMock.create();
        when(persistenceRepository.findByUserIdAndEventId(1L, 1L))
                .thenReturn(Optional.of(ticket));
        when(userInfoService.getUserInfo(1L))
                .thenReturn(info);

        // when
        ResponseTicketDto dto = service.myTicket(1L, 1L);

        // then
        assertThat(dto.getName()).isEqualTo(info.getName());
        assertThat(dto.getId()).isEqualTo(ticket.getId());
        assertThat(dto.getMajor()).isEqualTo(info.getMajor().getName());
        assertThat(dto.getStudentId()).isEqualTo(info.getStudentId());
        assertThat(dto.getTurn()).isEqualTo(ticket.getTurn());
        assertThat(dto.isIssued()).isFalse();
    }

    @Test
    @DisplayName("내 티켓 조회 - 티켓 발급이 안된 경우")
    void myTicketNoTicket() {
        // given
        Ticket ticket = TicketMock.createDummy();
        when(persistenceRepository.findByUserIdAndEventId(1L, 1L))
                .thenReturn(Optional.of(ticket));

        // when & then
        assertThrows(NoTicketException.class,
                () -> service.myTicket(1L, 1L));
    }

    @Test
    @DisplayName("내 티켓 조회 - DB에 없는 경우")
    void myTicketNotInDB() {
        // given
        when(persistenceRepository.findByUserIdAndEventId(1L, 1L))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(NoTicketException.class,
                () -> service.myTicket(1L, 1L));
    }

    @Test
    @DisplayName("관리자 티켓 정보 조회 - 발급 전인 경우")
    void getTicketInfoIssuable() {
        // given
        UserInfo info = UserInfoMock.create();
        Ticket ticket = TicketMock.createDummyIssuable();

        when(userInfoService.getUserInfo(ticket.getUser().getId())).thenReturn(info);
        when(persistenceRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(messageSource.getMessage(eq("sms.ticket-auth-message"), Mockito.any(), Mockito.any()))
                .thenReturn("test");

        // when
        ResponseManagerTicketDto dto = service.getTicketInfo(1L);

        // then
        assertThat(dto.getCode()).hasSize(DIGIT_COUNT);
        assertThat(dto.getName()).isEqualTo(info.getName());
        assertThat(dto.getId()).isEqualTo(ticket.getId());
        assertThat(dto.getMajor()).isEqualTo(info.getMajor().getName());
        assertThat(dto.getStudentId()).isEqualTo(info.getStudentId());
        assertThat(dto.getTurn()).isEqualTo(ticket.getTurn());
        assertThat(dto.isIssued()).isFalse();
        assertThat(dto.getEventId()).isEqualTo(ticket.getId());
        verify(smsService).sendSMS(info.getPhone(), "test");
    }

    @Test
    @DisplayName("관리자 티켓 정보 조회 - 이미 발급된 경우")
    void getTicketInfoIssued() {
        // given
        UserInfo info = UserInfoMock.create();
        Ticket ticket = TicketMock.createDummyIssuable();
        ticket.markAsIssued();

        when(userInfoService.getUserInfo(ticket.getUser().getId())).thenReturn(info);
        when(persistenceRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // when
        ResponseManagerTicketDto dto = service.getTicketInfo(1L);

        // then
        assertThat(dto.getCode()).hasSize(0);
        assertThat(dto.getName()).isEqualTo(info.getName());
        assertThat(dto.getId()).isEqualTo(ticket.getId());
        assertThat(dto.getMajor()).isEqualTo(info.getMajor().getName());
        assertThat(dto.getStudentId()).isEqualTo(info.getStudentId());
        assertThat(dto.getTurn()).isEqualTo(ticket.getTurn());
        assertThat(dto.isIssued()).isTrue();
        verify(smsService, never()).sendSMS(any(), any());
    }

    @Test
    @DisplayName("SMS 재전송")
    void resendSms() {
        // given
        Ticket ticket = TicketMock.createDummyIssuable();
        UserInfo info = UserInfoMock.create();

        when(persistenceRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(userInfoService.getUserInfo(ticket.getUser().getId())).thenReturn(info);
        when(messageSource.getMessage(eq("sms.auth-message"), Mockito.any(), Mockito.any()))
                .thenReturn("test");

        // when
        String code = service.resendSms(1L);

        // then
        assertThat(code).hasSize(DIGIT_COUNT);
        verify(smsService).sendSMS(info.getPhone(), "test");
    }

    @Test
    @DisplayName("티켓 발급 처리")
    void setToIssued() {
        // given
        Ticket ticket = TicketMock.createDummyIssuable();
        when(persistenceRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // when
        service.setToIssued(1L);

        // then
        assertThat(ticket.getStatus()).isEqualTo(ISSUED);
    }

    @Test
    @DisplayName("티켓 미발급 처리")
    void setToUnissued() {
        // given
        Ticket ticket = TicketMock.createDummyIssuable();
        ticket.markAsIssuable();
        when(persistenceRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // when
        service.setToUnissued(1L);

        // then
        assertThat(ticket.getStatus()).isEqualTo(ISSUABLE);
    }
}
