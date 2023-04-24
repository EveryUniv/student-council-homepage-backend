package com.dku.council.domain.user.repository.spec;

import com.dku.council.domain.user.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpec {

    public static Specification<User> withUsernameOrNickname(String name) {
        if (name == null) {
            return Specification.where(null);
        }

        String pattern = "%" + name + "%";
        return (root, query, builder) ->
                builder.or(
                        builder.like(root.get("name"), pattern),
                        builder.like(root.get("nickname"), pattern)
                );
    }
}
