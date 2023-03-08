package com.dku.council.domain.rental.service;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.rental.model.dto.RentalItemDto;
import com.dku.council.domain.rental.model.dto.request.RequestRentalItemDto;
import com.dku.council.domain.rental.model.entity.RentalItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RentalItemService {
    public ResponsePage<RentalItemDto> list(Specification<RentalItem> spec, Pageable pageable) {
        return null;
    }

    public Long create(RequestRentalItemDto dto) {
        return null;
    }

    public void delete(Long id) {

    }

    public void patch(Long id, RequestRentalItemDto dto) {

    }
}
