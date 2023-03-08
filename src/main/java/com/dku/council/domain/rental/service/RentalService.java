package com.dku.council.domain.rental.service;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.rental.model.dto.RentalDto;
import com.dku.council.domain.rental.model.dto.SummarizedRentalDto;
import com.dku.council.domain.rental.model.dto.request.RequestCreateRentalDto;
import com.dku.council.domain.rental.model.entity.Rental;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class RentalService {
    public ResponsePage<SummarizedRentalDto> list(Specification<Rental> spec, Pageable pageable) {
        return null;
    }

    public RentalDto findOne(Long id, boolean admin) {
        return null;
    }

    public Long create(Long userId, RequestCreateRentalDto dto) {
        return null;
    }
}
