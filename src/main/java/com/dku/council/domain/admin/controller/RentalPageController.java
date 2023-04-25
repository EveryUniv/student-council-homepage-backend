package com.dku.council.domain.admin.controller;

import com.dku.council.domain.admin.dto.RentalItemPageDto;
import com.dku.council.domain.admin.dto.RentalPageDto;
import com.dku.council.domain.admin.service.RentalPageService;
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

import static com.dku.council.domain.admin.util.PageConstants.DEFAULT_MAX_PAGE;
import static com.dku.council.domain.admin.util.PageConstants.DEFAULT_PAGE_SIZE;

@Controller
@RequestMapping("/manage/rentals")
@RequiredArgsConstructor
public class RentalPageController {
    private final RentalPageService service;

    @GetMapping
    public String rentals(Model model, @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<RentalPageDto> all = service.rentalList(pageable);
        model.addAttribute("rentals", all);
        model.addAttribute("maxPage", DEFAULT_MAX_PAGE);
        return "page/rental/rental";
    }

    @PostMapping("/{rentalId}/return")
    public String returnRental(HttpServletRequest request, @PathVariable Long rentalId) {
        service.returnRental(rentalId);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/items")
    public String rentalItems(Model model) {
        List<RentalItemPageDto> all = service.rentalItemList();
        model.addAttribute("rentalItems", all);
        return "page/rental/items";
    }

    @PostMapping("/item")
    public String createItem(HttpServletRequest request, String name, Integer count) {
        service.createItem(name, count);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/items/{itemId}/delete")
    public String itemDelete(HttpServletRequest request, @PathVariable Long itemId) {
        service.deleteItem(itemId);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/items/{itemId}/rename")
    public String itemRename(HttpServletRequest request, @PathVariable Long itemId, String name) {
        service.renameItem(itemId, name);
        return "redirect:" + request.getHeader("Referer");
    }

    @PostMapping("/items/{itemId}/update")
    public String rentalUpdate(HttpServletRequest request, @PathVariable Long itemId, Integer count) {
        service.updateItem(itemId, count);
        return "redirect:" + request.getHeader("Referer");
    }


}
