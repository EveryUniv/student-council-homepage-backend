package com.dku.council.domain.homebus.service;

import com.dku.council.domain.homebus.exception.AlreadyHomeBusCancelRequestException;
import com.dku.council.domain.homebus.exception.AlreadyHomeBusIssuedException;
import com.dku.council.domain.homebus.exception.FullSeatsException;
import com.dku.council.domain.homebus.model.HomeBusStatus;
import com.dku.council.domain.homebus.model.dto.HomeBusDto;
import com.dku.council.domain.homebus.model.dto.RequestCancelTicketDto;
import com.dku.council.domain.homebus.model.entity.HomeBus;
import com.dku.council.domain.homebus.model.entity.HomeBusCancelRequest;
import com.dku.council.domain.homebus.model.entity.HomeBusTicket;
import com.dku.council.domain.homebus.repository.HomeBusCancelRequestRepository;
import com.dku.council.domain.homebus.repository.HomeBusRepository;
import com.dku.council.domain.homebus.repository.HomeBusTicketRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.HomeBusMock;
import com.dku.council.mock.HomeBusTicketMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.dku.council.domain.homebus.model.HomeBusStatus.NEED_APPROVAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeBusUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HomeBusRepository busRepository;

    @Mock
    private HomeBusTicketRepository ticketRepository;

    @Mock
    private HomeBusCancelRequestRepository cancelRepository;

    @InjectMocks
    private HomeBusUserService service;


    @Test
    @DisplayName("버스 목록이 잘 반환되는지?")
    void listBus() {
        // given
        List<HomeBus> buses = List.of(
                HomeBusMock.createWithSeats(100),
                HomeBusMock.createWithSeats(150),
                HomeBusMock.createWithSeats(200)
        );
        List<HomeBusTicket> myTickets = List.of(
                HomeBusTicketMock.create(buses.get(0), HomeBusStatus.ISSUED)
        );

        when(ticketRepository.findAllByUserId(5L)).thenReturn(myTickets);
        when(ticketRepository.countRequestedSeats(any())).thenReturn(40L);
        when(busRepository.findAll()).thenReturn(buses);

        // when
        List<HomeBusDto> result = service.listBus(5L);

        // then
        List<HomeBusDto> expected = List.of(
                new HomeBusDto(buses.get(0), 60, HomeBusStatus.ISSUED),
                new HomeBusDto(buses.get(1), 110, HomeBusStatus.NONE),
                new HomeBusDto(buses.get(2), 160, HomeBusStatus.NONE)
        );
        assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    @DisplayName("티켓이 정상적으로 발권되는지?")
    void createTicket() {
        // given
        User user = UserMock.createDummyMajor();
        HomeBus bus = HomeBusMock.createWithSeats(31);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(busRepository.findById(bus.getId())).thenReturn(Optional.of(bus));
        when(ticketRepository.countRequestedSeats(bus.getId())).thenReturn(30L);
        when(ticketRepository.findAllByUserId(user.getId())).thenReturn(List.of());

        // when
        service.createTicket(user.getId(), bus.getId());

        // then
        verify(ticketRepository).save(argThat(ticket -> {
            assertThat(ticket.getStatus()).isEqualTo(NEED_APPROVAL);
            assertThat(ticket.getBus()).isEqualTo(bus);
            assertThat(ticket.getUser()).isEqualTo(user);
            return true;
        }));
    }

    @Test
    @DisplayName("죽전이 아닌 사람이 신청하면 오류")
    void failedCreateTicketByNotJuk() {
        // TODO 죽전 학생/대학원생만 신청할 수 있도록
    }

    @Test
    @DisplayName("중복 신청하면 오류")
    void failedCreateTicketByDuplicated() {
        // given
        User user = UserMock.createDummyMajor();
        HomeBus bus = HomeBusMock.createWithSeats(51);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(busRepository.findById(bus.getId())).thenReturn(Optional.of(bus));
        when(ticketRepository.countRequestedSeats(bus.getId())).thenReturn(50L);
        when(ticketRepository.findAllByUserId(user.getId())).thenReturn(List.of(
                HomeBusTicketMock.create(bus, HomeBusStatus.ISSUED)
        ));

        // when & then
        assertThrows(AlreadyHomeBusIssuedException.class, () ->
                service.createTicket(user.getId(), bus.getId()));
    }

    @Test
    @DisplayName("총 좌석 이상으로 신청하면 오류")
    void failedCreateTicketByFullSeats() {
        // given
        User user = UserMock.createDummyMajor();
        HomeBus bus = HomeBusMock.createWithSeats(50);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(busRepository.findById(bus.getId())).thenReturn(Optional.of(bus));
        when(ticketRepository.countRequestedSeats(bus.getId())).thenReturn(50L);
        when(ticketRepository.findAllByUserId(user.getId())).thenReturn(List.of());

        // when & then
        assertThrows(FullSeatsException.class, () ->
                service.createTicket(user.getId(), bus.getId()));
    }

    @Test
    @DisplayName("티켓 취소가 정상적으로 이루어지는지?")
    void deleteTicket() {
        // given
        User user = UserMock.createDummyMajor();
        HomeBus bus = HomeBusMock.createWithSeats(31);
        HomeBusTicket ticket = HomeBusTicketMock.create(bus, HomeBusStatus.ISSUED);
        RequestCancelTicketDto dto = new RequestCancelTicketDto("입금자", "계좌", "우리은행");

        when(ticketRepository.findByUserIdAndBusId(user.getId(), bus.getId()))
                .thenReturn(Optional.ofNullable(ticket));
        when(cancelRepository.findByTicket(ticket)).thenReturn(Optional.empty());

        // when
        service.deleteTicket(user.getId(), bus.getId(), dto);

        // then
        verify(cancelRepository).save(argThat(req -> {
            assertThat(req.getTicket()).isEqualTo(ticket);
            assertThat(req.getAccountNum()).isEqualTo(dto.getAccountNum());
            assertThat(req.getBankName()).isEqualTo(dto.getBankName());
            assertThat(req.getDepositor()).isEqualTo(dto.getDepositor());
            return true;
        }));
    }

    @Test
    @DisplayName("중복으로 같은 티켓 취소 신청하면 오류")
    void failedDeleteTicketByDuplicated() {
        // given
        User user = UserMock.createDummyMajor();
        HomeBus bus = HomeBusMock.createWithSeats(31);
        HomeBusTicket ticket = HomeBusTicketMock.create(bus, HomeBusStatus.ISSUED);
        HomeBusCancelRequest dummyReq = HomeBusCancelRequest.builder().build();
        RequestCancelTicketDto dto = new RequestCancelTicketDto("입금자", "계좌", "우리은행");

        when(ticketRepository.findByUserIdAndBusId(user.getId(), bus.getId()))
                .thenReturn(Optional.ofNullable(ticket));
        when(cancelRepository.findByTicket(ticket)).thenReturn(Optional.of(dummyReq));

        // when & then
        assertThrows(AlreadyHomeBusCancelRequestException.class, () ->
                service.deleteTicket(user.getId(), bus.getId(), dto));
    }
}