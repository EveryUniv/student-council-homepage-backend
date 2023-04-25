package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.RentalItemPageDto;
import com.dku.council.domain.admin.dto.RentalPageDto;
import com.dku.council.domain.rental.exception.RentalItemNotFoundException;
import com.dku.council.domain.rental.exception.RentalNotFoundException;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
import com.dku.council.domain.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalPageService {
    private final RentalRepository rentalRepository;
    private final RentalItemRepository rentalItemRepository;

    public Page<RentalPageDto> rentalList(Pageable pageable){
        return rentalRepository.findAll(pageable).map(RentalPageDto::new);
    }

    public List<RentalItemPageDto> rentalItemList(){
        return rentalItemRepository.findAll().stream().map(RentalItemPageDto::new).collect(Collectors.toList());
    }

    public Rental findRental(Long rentalId){
        return rentalRepository.findById(rentalId).orElseThrow(RentalNotFoundException::new);
    }
    public RentalItem findRentalItem(Long itemId){
        return rentalItemRepository.findById(itemId).orElseThrow(RentalItemNotFoundException::new);
    }

    public void returnRental(Long rentalId){
        Rental rental = findRental(rentalId);
        RentalItem item = rental.getItem();
        rental.markAsDeleted();
        item.increaseRemaining();
    }

    public void createItem(String name, Integer count){
        RentalItem rentalItem = new RentalItem(name, count);
        rentalItemRepository.save(rentalItem);
    }

    public void deleteItem(Long itemId){
        RentalItem rentalItem = findRentalItem(itemId);
        rentalItem.markAsDeleted();
    }
    public void renameItem(Long itemId, String name){
        RentalItem rentalItem = findRentalItem(itemId);
        rentalItem.updateItemName(name);
    }

    public void updateItem(Long itemId, Integer count){
        RentalItem rentalItem = findRentalItem(itemId);
        rentalItem.updateAvailable(count);
    }
}
