package com.dku.council.domain.rental.service;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.rental.exception.RentalItemNotFoundException;
import com.dku.council.domain.rental.model.dto.RentalItemDto;
import com.dku.council.domain.rental.model.dto.request.RequestRentalItemDto;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
import com.dku.council.domain.rental.repository.spec.RentalSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalItemService {

    private final RentalItemRepository rentalItemRepository;


    @Transactional(readOnly = true)
    public ResponsePage<RentalItemDto> list(Specification<RentalItem> spec, Pageable pageable) {
        spec = RentalSpec.withRentalItemActive().and(spec);
        Page<RentalItemDto> page = rentalItemRepository.findAll(spec, pageable)
                .map(RentalItemDto::new);
        return new ResponsePage<>(page);
    }

    public Long create(RequestRentalItemDto dto) {
        RentalItem item = new RentalItem(dto.getItemName(), dto.getAvailable());
        item = rentalItemRepository.save(item);
        return item.getId();
    }

    public void delete(Long id) {
        RentalItem item = rentalItemRepository.findById(id).orElseThrow(RentalItemNotFoundException::new);
        item.markAsDeleted();
    }

    public void patch(Long id, RequestRentalItemDto dto) {
        RentalItem item = rentalItemRepository.findById(id).orElseThrow(RentalItemNotFoundException::new);
        if (dto.getItemName() != null) {
            item.updateItemName(dto.getItemName());
        }
        if (dto.getAvailable() != null) {
            item.updateAvailable(dto.getAvailable());
        }
    }
}
