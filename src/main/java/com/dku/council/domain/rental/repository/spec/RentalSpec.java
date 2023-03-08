package com.dku.council.domain.rental.repository.spec;

import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import org.springframework.data.jpa.domain.Specification;

public class RentalSpec {

    public static Specification<Rental> withUser(Long userId) {
        return (root, query, builder) ->
                builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Rental> withUsername(String username) {
        String pattern = "%" + username + "%";
        return (root, query, builder) ->
                builder.like(root.get("user").get("name"), pattern);
    }

    public static Specification<Rental> withTitleOrBody(String keyword) {
        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.or(
                        builder.like(root.get("title"), pattern),
                        builder.like(root.get("body"), pattern)
                );
    }

    public static Specification<Rental> withItemName(String name) {
        String pattern = "%" + name + "%";
        return (root, query, builder) ->
                builder.like(root.get("item").get("name"), pattern);
    }

    public static Specification<RentalItem> withName(String name) {
        String pattern = "%" + name + "%";
        return (root, query, builder) ->
                builder.like(root.get("name"), pattern);
    }
}
