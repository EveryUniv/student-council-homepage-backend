package com.dku.council.domain.rental.service;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.service.DummyPage;
import com.dku.council.domain.rental.exception.RentalItemNotFoundException;
import com.dku.council.domain.rental.model.dto.RentalItemDto;
import com.dku.council.domain.rental.model.dto.request.RequestRentalItemDto;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
import com.dku.council.mock.RentalItemMock;
import com.dku.council.mock.RentalMock;
import com.dku.council.util.FieldInjector;
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
class RentalItemServiceTest {

    @Mock
    private RentalItemRepository rentalItemRepository;

    @InjectMocks
    private RentalItemService service;


    @Test
    @DisplayName("list가 잘 동작하는가")
    void list() {
        // given
        List<RentalItem> itemList = RentalItemMock.createList(15);
        Page<RentalItem> items = new DummyPage<>(itemList);
        when(rentalItemRepository.findAll((Specification<RentalItem>) any(), (Pageable) any()))
                .thenReturn(items);

        // when
        ResponsePage<RentalItemDto> dto = service.list(null, Pageable.unpaged());

        // then
        assertThat(dto.getTotalElements()).isEqualTo(itemList.size());
    }

    @Test
    @DisplayName("잘 추가되는가")
    void create() {
        // given
        RequestRentalItemDto dto = new RequestRentalItemDto("myitem", 15);
        RentalItem saved = RentalItemMock.create(11L, dto.getItemName(), dto.getAvailable());
        when(rentalItemRepository.save(any())).thenReturn(saved);

        // when
        Long id = service.create(dto);

        // then
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getName()).isEqualTo("myitem");
        assertThat(saved.getRemaining()).isEqualTo(15);
    }

    @Test
    @DisplayName("잘 삭제되는가")
    void delete() {
        // given
        RentalItem item = RentalItemMock.create(11L, "name", 15);
        List<Rental> rentals = RentalMock.createList(item, 10);
        FieldInjector.inject(RentalItem.class, item, "rentals", rentals);

        when(rentalItemRepository.findById(11L)).thenReturn(Optional.of(item));

        // when
        service.delete(11L);

        // then
        assertThat(item.isActive()).isFalse();
        for (Rental rental : rentals) {
            assertThat(rental.isActive()).isFalse();
        }
    }

    @Test
    @DisplayName("잘 삭제되는가 - 못 찾으면 오류")
    void failedDeleteByNotFound() {
        // when & then
        assertThrows(RentalItemNotFoundException.class, () ->
                service.delete(11L));
    }

    @Test
    @DisplayName("잘 수정되는가 - 이름만 수정")
    void patchName() {
        // given
        RequestRentalItemDto dto = new RequestRentalItemDto("Haha", null);
        RentalItem saved = RentalItemMock.create(11L, "name", 15);
        when(rentalItemRepository.findById(11L)).thenReturn(Optional.of(saved));

        // when
        service.patch(11L, dto);

        // then
        assertThat(saved.getName()).isEqualTo("Haha");
        assertThat(saved.getRemaining()).isEqualTo(15);
    }

    @Test
    @DisplayName("잘 수정되는가 - 잔여 개수만 수정")
    void patchAvailable() {
        // given
        RequestRentalItemDto dto = new RequestRentalItemDto(null, 20);
        RentalItem saved = RentalItemMock.create(11L, "name", 15);
        when(rentalItemRepository.findById(11L)).thenReturn(Optional.of(saved));

        // when
        service.patch(11L, dto);

        // then
        assertThat(saved.getName()).isEqualTo("name");
        assertThat(saved.getRemaining()).isEqualTo(20);
    }

    @Test
    @DisplayName("잘 수정되는가 - 못 찾으면 오류")
    void failedPatchByNotFound() {
        // given
        RequestRentalItemDto dto = new RequestRentalItemDto(null, null);

        // when & then
        assertThrows(RentalItemNotFoundException.class, () ->
                service.patch(11L, dto));
    }
}