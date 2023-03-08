package com.dku.council.domain.rental.repository.spec;

import com.dku.council.domain.rental.model.RentalUserClass;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.RentalItemRepository;
import com.dku.council.domain.rental.repository.RentalRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
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
    private UserRepository userRepository;

    private User user1;

    @BeforeEach
    void setup() {
        user1 = UserMock.createWithName("user1");
        user1 = userRepository.save(user1);

        User user2 = UserMock.createWithName("mamamam");
        user2 = userRepository.save(user2);

        List<RentalItem> items = createItems("Item", 7);
        List<RentalItem> items2 = createItems("RentalIT", 8);

        createRentals(user2, items.get(0), "target", "body", 7);
        createRentals(user1, items2.get(0), "none", "target", 8);
        createRentals(user2, items.get(0), "none", "none", 9);
    }

    private List<RentalItem> createItems(String prefix, int size) {
        List<RentalItem> items = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            items.add(new RentalItem(prefix + i, 20 + i));
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
                    .rentalStart(LocalDateTime.MIN)
                    .rentalEnd(LocalDateTime.MAX)
                    .userClass(RentalUserClass.INDIVIDUAL)
                    .build();
            items.add(rental);
        }
        rentalRepository.saveAll(items);
    }

    @Test
    void withUser() {
        // given
        Specification<Rental> spec = RentalSpec.withUser(user1.getId());

        // when
        List<Rental> all = rentalRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(8);
    }

    @Test
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
    void withTitleOrBody() {
        // given
        Specification<Rental> spec = RentalSpec.withTitleOrBody("target");

        // when
        List<Rental> all = rentalRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(15);
    }

    @Test
    void withItemName() {
        // given
        Specification<Rental> spec = RentalSpec.withItemName("tem");

        // when
        List<Rental> all = rentalRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(16);
    }

    @Test
    void withName() {
        // given
        Specification<RentalItem> spec = RentalSpec.withName("entalI");

        // when
        List<RentalItem> all = rentalItemRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(8);
    }
}