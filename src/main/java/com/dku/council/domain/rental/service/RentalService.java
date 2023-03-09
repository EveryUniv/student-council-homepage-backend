package com.dku.council.domain.rental.service;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.rental.exception.RentalNotFoundException;
import com.dku.council.domain.rental.model.dto.RentalDto;
import com.dku.council.domain.rental.model.dto.SummarizedRentalDto;
import com.dku.council.domain.rental.model.dto.request.RequestCreateRentalDto;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalRepository;
import com.dku.council.domain.rental.repository.spec.RentalSpec;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalItemService rentalItemService;


    // TODO 이 작업은 repository에서 하는 것이 적합
    public Rental findRental(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(RentalNotFoundException::new);
        if (!rental.isActive()) {
            throw new RentalNotFoundException();
        }
        return rental;
    }

    public ResponsePage<SummarizedRentalDto> list(Specification<Rental> spec, Pageable pageable) {
        spec = RentalSpec.withRentalActive().and(spec);
        Page<SummarizedRentalDto> page = rentalRepository.findAll(spec, pageable)
                .map(SummarizedRentalDto::new);
        return new ResponsePage<>(page);
    }

    public RentalDto findOne(Long id, Long userId, boolean admin) {
        Rental rental = findRental(id);

        if (!admin && !rental.getUser().getId().equals(userId)) {
            throw new RentalNotFoundException();
        }

        return new RentalDto(rental);
    }

    @Transactional
    public Long create(Long userId, RequestCreateRentalDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        RentalItem item = rentalItemService.findRentalItem(dto.getItemId());

        Rental rental = Rental.builder()
                .user(user)
                .item(item)
                .userClass(dto.getUserClass())
                .rentalStart(dto.getRentalStart())
                .rentalEnd(dto.getRentalEnd())
                .title(dto.getTitle())
                .body(dto.getBody())
                .build();

        rental = rentalRepository.save(rental);
        return rental.getId();
    }

    @Transactional
    public void returnItem(Long id) {
        Rental rental = findRental(id);
        RentalItem item = rental.getItem();
        rental.markAsDeleted();
        item.increaseRemaining();
    }
}
