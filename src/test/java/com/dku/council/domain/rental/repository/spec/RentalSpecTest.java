package com.dku.council.domain.rental.repository.spec;

import com.dku.council.domain.rental.model.RentalUserClass;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
import com.dku.council.domain.rental.repository.RentalRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.RentalMock;
import com.dku.council.mock.UserMock;
import com.dku.council.util.FieldInjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RentalSpecTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalItemRepository rentalItemRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setup() {
        Major major = majorRepository.save(MajorMock.create());

        user1 = UserMock.create("user1", major);
        user1 = userRepository.save(user1);

        User user2 = UserMock.create("mamamam", major);
        user2 = userRepository.save(user2);

        List<RentalItem> items = createItems("Item", 7, true);
        List<RentalItem> items2 = createItems("RentalIT", 8, true);
        createItems("RentalIT", 8, false);

        createRentals(user2, items.get(0), "target", "body", 7);
        createRentals(user1, items2.get(0), "none", "target", 8);
        createRentals(user2, items.get(0), "none", "none", 9);
    }

    private List<RentalItem> createItems(String prefix, int size, boolean isActive) {
        List<RentalItem> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            RentalItem item = new RentalItem(prefix + i, 20 + i);
            if (!isActive) {
                FieldInjector.inject(RentalItem.class, item, "isActive", false);
            }
            items.add(item);
        }
        return rentalItemRepository.saveAll(items);
    }

    private void createRentals(User user, RentalItem item,
                               String titlePrefix, String bodyPrefix, int size) {
        List<Rental> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Rental rental = Rental.builder()
                    .user(user)
                    .item(item)
                    .title(titlePrefix + i)
                    .body(bodyPrefix + i)
                    .rentalStart(RentalMock.RENTAL_START)
                    .rentalEnd(RentalMock.RENTAL_END)
                    .userClass(RentalUserClass.INDIVIDUAL)
                    .build();
            rental.changeItem(item);
            items.add(rental);
        }
        rentalRepository.saveAll(items);
    }

    @Test
    @DisplayName("userId로 rental 검색")
    void withUser() {
        // given
        Specification<Rental> spec = RentalSpec.withUser(user1.getId());

        // when
        List<Rental> all = rentalRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("username으로 rental 검색")
    void withUsername() {
        // given
        String name = user1.getName();
        name = name.substring(1, name.length() - 2);
        Specification<Rental> spec = RentalSpec.withUsername(name);

        // when
        List<Rental> all = rentalRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("keyword(title or body)로 rental 검색")
    void withTitleOrBody() {
        // given
        Specification<Rental> spec = RentalSpec.withTitleOrBody("target");

        // when
        List<Rental> all = rentalRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(15);
    }

    @Test
    @DisplayName("물품 이름으로 rental 검색")
    void withItemName() {
        // given
        Specification<Rental> spec = RentalSpec.withItemName("tem");

        // when
        List<Rental> all = rentalRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(16);
    }

    @Test
    @DisplayName("이름으로 rental item 검색")
    void withName() {
        // given
        Specification<RentalItem> spec = RentalSpec.withName("entalI");

        // when
        List<RentalItem> all = rentalItemRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(16);
    }

    @Test
    @DisplayName("활성화된 rental item 검색")
    void withActive() {
        // given
        Specification<RentalItem> spec = RentalSpec.withRentalItemActive();

        // when
        List<RentalItem> all = rentalItemRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(15);
    }
}