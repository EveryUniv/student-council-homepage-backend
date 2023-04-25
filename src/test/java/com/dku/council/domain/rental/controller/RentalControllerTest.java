package com.dku.council.domain.rental.controller;

import com.dku.council.domain.rental.model.RentalUserClass;
import com.dku.council.domain.rental.model.dto.request.RequestCreateRentalDto;
import com.dku.council.domain.rental.model.dto.request.RequestRentalItemDto;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
import com.dku.council.domain.rental.repository.RentalRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.model.dto.ResponseIdDto;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.RentalItemMock;
import com.dku.council.mock.RentalMock;
import com.dku.council.mock.UserMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.EntityUtil;
import com.dku.council.util.MvcMockResponse;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@FullIntegrationTest
class RentalControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalItemRepository rentalItemRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private List<RentalItem> rentalItems;
    private List<Rental> rentals;
    private List<Rental> targetRentals;
    private User user;

    @BeforeEach
    void setupUser() {
        Major major = majorRepository.save(MajorMock.create());

        user = UserMock.create(0L, major);
        user = userRepository.save(user);
        UserAuth.withUser(user.getId());

        User user2 = UserMock.create(0L, major);
        user2 = userRepository.save(user2);

        rentalItems = RentalItemMock.createList(10);
        rentalItems = rentalItemRepository.saveAll(rentalItems);
        rentalItemRepository.saveAll(RentalItemMock.createDisabledList(5));

        rentals = new ArrayList<>();
        targetRentals = RentalMock.createList(rentalItems.get(0), user, 3);
        rentals.addAll(targetRentals);
        for (int i = 1; i < rentalItems.size(); i++) {
            rentals.add(RentalMock.create(user2, rentalItems.get(i)));
        }
        rentals = rentalRepository.saveAll(rentals);

        rentalRepository.saveAll(RentalMock.createDisabledList(rentalItems.get(0), user, 5));
    }

    @Test
    @DisplayName("대여 신청 현황 조회")
    void list() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());

        // when
        ResultActions result = mvc.perform(get("/rental"));

        // then
        Integer[] ids = EntityUtil.getIdArray(rentals);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(ids)));
    }

    @Test
    @DisplayName("대여 품목 조회")
    void listItem() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/rental/item"));

        // then
        Integer[] ids = EntityUtil.getIdArray(rentalItems);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(ids)));
    }

    @Test
    @DisplayName("내가 대여한 품목들 조회")
    void myList() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/rental/my"));

        // then
        Integer[] ids = EntityUtil.getIdArray(targetRentals);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(ids)));
    }

    @Test
    @DisplayName("대여 신청")
    void create() throws Exception {
        // given
        rentalRepository.deleteAll();
        RentalItem item = rentalItems.get(0);
        RequestCreateRentalDto dto = new RequestCreateRentalDto(item.getId(), RentalUserClass.INDIVIDUAL,
                RentalMock.RENTAL_START, RentalMock.RENTAL_END, "title", "body");

        // when
        ResultActions result = mvc.perform(post("/rental")
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        MvcResult response = result.andExpect(status().isOk())
                .andReturn();
        ResponseIdDto responseDto = MvcMockResponse.getResponse(objectMapper, response, ResponseIdDto.class);
        Rental actual = rentalRepository.findById(responseDto.getId()).orElseThrow();

        assertThat(actual.getTitle()).isEqualTo("title");
        assertThat(actual.getBody()).isEqualTo("body");
        assertThat(actual.getRentalStart()).isEqualTo(RentalMock.RENTAL_START);
        assertThat(actual.getRentalEnd()).isEqualTo(RentalMock.RENTAL_END);
        assertThat(actual.getUserClass()).isEqualTo(RentalUserClass.INDIVIDUAL);
    }

    @Test
    @DisplayName("대여 반납 처리")
    void returnItem() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        Rental rental = rentals.get(0);

        // when
        int prevRemaining = rental.getItem().getRemaining();
        ResultActions result = mvc.perform(post("/rental/return/" + rental.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(rental.getId().intValue())));

        assertThat(rental.getItem().getRemaining()).isEqualTo(prevRemaining + 1);
        assertThat(rental.isActive()).isEqualTo(false);
    }

    @Test
    @DisplayName("대여 품목 추가")
    void addItem() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        RequestRentalItemDto dto = new RequestRentalItemDto("createTest", 11);

        // when
        ResultActions result = mvc.perform(post("/rental/item")
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        MvcResult response = result.andExpect(status().isOk())
                .andReturn();
        ResponseIdDto responseDto = MvcMockResponse.getResponse(objectMapper, response, ResponseIdDto.class);
        RentalItem actual = rentalItemRepository.findById(responseDto.getId()).orElseThrow();

        assertThat(actual.getName()).isEqualTo("createTest");
        assertThat(actual.getRemaining()).isEqualTo(11);
    }

    @Test
    @DisplayName("대여 품목 삭제")
    void deleteItem() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        RentalItem item = rentalItems.get(0);

        // when
        ResultActions result = mvc.perform(delete("/rental/item/" + item.getId()));

        // then
        result.andExpect(status().isOk());

        assertThat(item.isActive()).isFalse();
        targetRentals.forEach(e -> assertThat(e.isActive()).isFalse());
    }

    @Test
    @DisplayName("대여 품목 변경")
    void patchItem() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        RequestRentalItemDto dto = new RequestRentalItemDto(null, 5);
        RentalItem item = rentalItems.get(0);

        // when
        String prevName = item.getName();
        ResultActions result = mvc.perform(patch("/rental/item/" + item.getId())
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
        assertThat(item.getName()).isEqualTo(prevName);
        assertThat(item.getRemaining()).isEqualTo(5);
    }
}
