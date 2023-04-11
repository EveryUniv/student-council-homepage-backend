package com.dku.council.domain.rental.service;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.rental.exception.AlreadyRentalException;
import com.dku.council.domain.rental.exception.NotAvailableItemException;
import com.dku.council.domain.rental.exception.RentalItemNotFoundException;
import com.dku.council.domain.rental.exception.RentalNotFoundException;
import com.dku.council.domain.rental.model.dto.RentalDto;
import com.dku.council.domain.rental.model.dto.request.RequestCreateRentalDto;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final RentalItemRepository rentalItemRepository;


    public ResponsePage<RentalDto> list(Specification<Rental> spec, Pageable pageable) {
        spec = RentalSpec.withRentalActive().and(spec);
        Page<RentalDto> page = rentalRepository.findAll(spec, pageable)
                .map(RentalDto::new);
        return new ResponsePage<>(page);
    }

    public ResponsePage<RentalDto> list(Long productId, Pageable pageable) {
        Page<RentalDto> page = rentalRepository.findAllByItemId(productId, pageable)
                .map(RentalDto::new);
        return new ResponsePage<>(page);
    }

    public RentalDto findOne(Long id, Long userId, boolean admin) {
        Rental rental = rentalRepository.findById(id).orElseThrow(RentalNotFoundException::new);

        if (!admin && !rental.getUser().getId().equals(userId)) {
            throw new RentalNotFoundException();
        }

        return new RentalDto(rental);
    }

    @Transactional
    public Long create(Long userId, RequestCreateRentalDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        RentalItem item = rentalItemRepository.findById(dto.getItemId()).orElseThrow(RentalItemNotFoundException::new);

        checkAlreadyRental(user, item);

        if (item.getRemaining() == 0) {
            throw new NotAvailableItemException();
        }

        Rental rental = Rental.builder()
                .user(user)
                .item(item)
                .userClass(dto.getUserClass())
                .rentalStart(dto.getRentalStart())
                .rentalEnd(dto.getRentalEnd())
                .title(dto.getTitle())
                .body(dto.getBody())
                .build();

        rental.changeItem(item);
        rental = rentalRepository.save(rental);
        item.decreaseRemaining();
        return rental.getId();
    }

    private void checkAlreadyRental(User user, RentalItem item) {
        Optional<Rental> alreadyRental = rentalRepository.findByUserAndItem(user, item);
        if (alreadyRental.isPresent()) {
            throw new AlreadyRentalException();
        }
    }

    @Transactional
    public void returnItem(Long id) {
        Rental rental = rentalRepository.findById(id).orElseThrow(RentalNotFoundException::new);
        RentalItem item = rental.getItem();
        rental.markAsDeleted();
        item.increaseRemaining();
    }
}
