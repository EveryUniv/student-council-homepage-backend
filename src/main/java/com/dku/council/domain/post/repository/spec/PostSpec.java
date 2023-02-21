package com.dku.council.domain.post.repository.spec;

import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.jpa.domain.Specification;

// TODO Test 추가
public class PostSpec {
    public static <T extends Post> Specification<T> withTitleOrBody(String keyword) {
        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.or(
                        builder.like(root.get("title"), pattern),
                        builder.like(root.get("body"), pattern)
                );
    }
}
