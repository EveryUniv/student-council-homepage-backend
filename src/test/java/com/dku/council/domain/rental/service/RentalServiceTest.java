package com.dku.council.domain.rental.service;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.service.DummyPage;
import com.dku.council.domain.rental.exception.AlreadyRentalException;
import com.dku.council.domain.rental.exception.NotAvailableItemException;
import com.dku.council.domain.rental.exception.RentalNotFoundException;
import com.dku.council.domain.rental.model.dto.RentalDto;
import com.dku.council.domain.rental.model.dto.SummarizedRentalDto;
import com.dku.council.domain.rental.model.dto.request.RequestCreateRentalDto;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.mock.RentalItemMock;
import com.dku.council.mock.RentalMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalItemService rentalItemService;

    @InjectMocks
    private RentalService service;

    private User user;
    private Rental rental;
    private RentalItem rentalItem;
    private RequestCreateRentalDto createDto;

    @BeforeEach
    public void setup() {
        user = UserMock.create(19L);
        rentalItem = RentalItemMock.create(9L, "testItem", 19);
        rental = RentalMock.create(17L, user, rentalItem);
        createDto = new RequestCreateRentalDto(rentalItem.getId(), rental.getUserClass(),
                rental.getRentalStart(), rental.getRentalEnd(), rental.getTitle(), rental.getBody());
    }

    @Test
    @DisplayName("list가 잘 동작하는가")
    void list() {
        // given
        RentalItem item = RentalItemMock.create(5L, "item", 10);
        List<Rental> itemList = RentalMock.createList(item, 15);
        itemList.addAll(RentalMock.createDisabledList(item, 5));

        Page<Rental> rentals = new DummyPage<>(itemList);
        when(rentalRepository.findAll((Specification<Rental>) any(), (Pageable) any()))
                .thenReturn(rentals);

        // when
        ResponsePage<SummarizedRentalDto> dto = service.list(null, Pageable.unpaged());

        // then
        assertThat(dto.getTotalElements()).isEqualTo(20);
    }

    @Test
    @DisplayName("단건 조회가 잘 동작하는가")
    void findOne() {
        // given
        when(rentalRepository.findById(17L)).thenReturn(Optional.of(rental));

        // when
        RentalDto dto = service.findOne(17L, 19L, false);

        // then
        assertThat(dto.getId()).isEqualTo(17L);
        assertThat(dto.getLender()).isEqualTo(UserMock.NAME);
    }

    @Test
    @DisplayName("단건 조회 실패 - 없는 대여 현황이면 오류")
    void failedFindOneByNotFound() {
        // given
        when(rentalRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(RentalNotFoundException.class, () ->
                service.findOne(1L, 1L, false));
    }

    @Test
    @DisplayName("단건 조회 실패 - 내꺼 아닌 게시글 조회시 오류")
    void failedFindOneByNotMine() {
        // given
        when(rentalRepository.findById(17L)).thenReturn(Optional.of(rental));

        // when & then
        assertThrows(RentalNotFoundException.class, () ->
                service.findOne(17L, 11L, false));
    }

    @Test
    @DisplayName("단건 조회 - 운영자는 본인 게시글이 아니어도 조회")
    void findOneByAdmin() {
        // given
        when(rentalRepository.findById(17L)).thenReturn(Optional.of(rental));

        // when
        RentalDto dto = service.findOne(17L, 11L, true);

        // then
        assertThat(dto.getId()).isEqualTo(17L);
        assertThat(dto.getLender()).isEqualTo(UserMock.NAME);
    }

    @Test
    @DisplayName("대여 신청이 잘 동작하는가")
    void create() {
        // given
        when(rentalRepository.save(any())).thenReturn(rental);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rentalItemService.findRentalItem(rentalItem.getId())).thenReturn(rentalItem);

        // when
        int prevAvailable = rentalItem.getRemaining();
        Long id = service.create(user.getId(), createDto);

        // then
        assertThat(id).isEqualTo(rental.getId());
        assertThat(rentalItem.getRemaining()).isEqualTo(prevAvailable - 1);
    }

    @Test
    @DisplayName("대여 신청 - 서로 다른 물품으로 2회 대여")
    void createWithOtherProducts() {
        // given
        when(rentalRepository.save(any())).thenReturn(rental);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rentalItemService.findRentalItem(rentalItem.getId())).thenReturn(rentalItem);
        when(rentalRepository.findByUserAndItem(user, rentalItem)).thenReturn(Optional.empty());

        // when
        int prevAvailable = rentalItem.getRemaining();
        service.create(user.getId(), createDto);
        Long id = service.create(user.getId(), createDto);

        // then
        assertThat(id).isEqualTo(rental.getId());
        assertThat(rentalItem.getRemaining()).isEqualTo(prevAvailable - 2);
    }

    @Test
    @DisplayName("대여 신청 실패 - 찾을 수 없는 유저인 경우")
    void failedCreateByNotFoundUser() {
        // when & then
        assertThrows(UserNotFoundException.class, () ->
                service.create(10L, createDto));
    }

    @Test
    @DisplayName("대여 신청 실패 - 대여할 수 있는 물품이 없는 경우")
    void failedCreateByNotAvailable() {
        // given
        RentalItem item = new RentalItem("not-available", 0);
        when(rentalItemService.findRentalItem(rentalItem.getId())).thenReturn(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when & then
        assertThrows(NotAvailableItemException.class, () ->
                service.create(user.getId(), createDto));
    }

    @Test
    @DisplayName("대여 신청 실패 - 같은 물품을 이미 대여한 경우")
    void failedCreateByAlreadyRental() {
        // given
        RentalItem item = new RentalItem("not-available", 0);
        when(rentalItemService.findRentalItem(rentalItem.getId())).thenReturn(item);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rentalRepository.findByUserAndItem(user, item)).thenReturn(Optional.of(rental));

        // when & then
        assertThrows(AlreadyRentalException.class, () ->
                service.create(user.getId(), createDto));
    }

    @Test
    @DisplayName("대여 물품 반환이 잘 동작하는가")
    void returnItem() {
        // given
        when(rentalRepository.findById(5L)).thenReturn(Optional.of(rental));

        // when
        int prevRemaining = rentalItem.getRemaining();
        service.returnItem(5L);

        // then
        assertThat(rental.isActive()).isFalse();
        assertThat(rentalItem.getRemaining()).isEqualTo(prevRemaining + 1);
    }
}