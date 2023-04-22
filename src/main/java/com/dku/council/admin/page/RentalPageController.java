package com.dku.council.admin.page;

import com.dku.council.domain.rental.exception.RentalNotFoundException;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
import com.dku.council.domain.rental.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/manage/rentals")
@RequiredArgsConstructor
public class RentalPageController {
    private final int DEFAULT_PAGE_SIZE = 15;
    private final int DEFAULT_MAX_PAGE = 5;
    private final RentalRepository rentalRepository;
    private final RentalItemRepository rentalItemRepository;

    @GetMapping
    public String rentals(Model model, @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable){
        Page<Rental> all = rentalRepository.findAll(pageable);
        model.addAttribute("rentals", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        return "rental/rental";
    }

    @PostMapping("/{rentalId}/return")
    public String returnRental(HttpServletRequest request, @PathVariable Long rentalId){
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(RentalNotFoundException::new);
        RentalItem item = rental.getItem();
        rental.markAsDeleted();
        item.increaseRemaining();
        rentalRepository.save(rental);
        rentalItemRepository.save(item);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/items")
    public String rentalItems(Model model){
        List<RentalItem> all = rentalItemRepository.findAll();
        model.addAttribute("rentalItems", all);
        return "rental/items";
    }

    @PostMapping("/item")
    public String createItem(HttpServletRequest request, String name, Integer count){
        RentalItem rentalItem = new RentalItem(name, count);
        rentalItemRepository.save(rentalItem);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/items/{itemId}/delete")
    public String itemDelete(HttpServletRequest request, @PathVariable Long itemId){
        RentalItem rentalItem = rentalItemRepository.findById(itemId).get();
        rentalItem.markAsDeleted();
        rentalItemRepository.save(rentalItem);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/items/{itemId}/rename")
    public String itemRename(HttpServletRequest request, @PathVariable Long itemId, String name){
        RentalItem rentalItem = rentalItemRepository.findById(itemId).get();
        rentalItem.updateItemName(name);
        rentalItemRepository.save(rentalItem);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/items/{itemId}/update")
    public String rentalUpdate(HttpServletRequest request, @PathVariable Long itemId, Integer count){
        RentalItem rentalItem = rentalItemRepository.findById(itemId).get();
        rentalItem.updateAvailable(count);
        rentalItemRepository.save(rentalItem);
        return "redirect:" + request.getHeader("Referer");
    }


}
