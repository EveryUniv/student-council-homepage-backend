package com.dku.council.domain.rental.repository.spec;

import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import org.springframework.data.jpa.domain.Specification;

public class RentalSpec {

    public static Specification<Rental> withUser(Long userId) {
        if (userId == null) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Rental> withUsername(String username) {
        if (username == null) {
            return Specification.where(null);
        }

        String pattern = "%" + username + "%";
        return (root, query, builder) ->
                builder.like(root.get("user").get("name"), pattern);
    }

    public static Specification<Rental> withTitleOrBody(String keyword) {
        if (keyword == null) {
            return Specification.where(null);
        }

        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.or(
                        builder.like(root.get("title"), pattern),
                        builder.like(root.get("body"), pattern)
                );
    }

    public static Specification<Rental> withItemName(String name) {
        if (name == null) {
            return Specification.where(null);
        }

        String pattern = "%" + name + "%";
        return (root, query, builder) ->
                builder.like(root.get("item").get("name"), pattern);
    }

    public static Specification<Rental> withRentalActive() {
        return (root, query, builder) ->
                builder.equal(root.get("isActive"), true);
    }

    public static Specification<RentalItem> withName(String name) {
        if (name == null) {
            return Specification.where(null);
        }

        String pattern = "%" + name + "%";
        return (root, query, builder) ->
                builder.like(root.get("name"), pattern);
    }

    public static Specification<RentalItem> withRentalItemActive() {
        return (root, query, builder) ->
                builder.equal(root.get("isActive"), true);
    }
}
