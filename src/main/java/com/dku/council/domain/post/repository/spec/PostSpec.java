package com.dku.council.domain.post.repository.spec;

import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.jpa.domain.Specification;

// TODO Test 추가
public class PostSpec {

    public static <T extends Post> Specification<T> createPostCondition() {
        return withActive();
    }

    public static <T extends Post> Specification<T> genericPostCondition(String keyword, Long categoryId) {
        Specification<T> spec = createPostCondition();
        if (keyword != null) spec.and(withTitleOrBody(keyword));
        if (categoryId != null) spec.and(withCategory(categoryId));
        return spec;
    }

    private static <T extends Post> Specification<T> withTitleOrBody(String keyword) {
        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.or(
                        builder.like(root.get("title"), pattern),
                        builder.like(root.get("body"), pattern)
                );
    }

    private static <T extends Post> Specification<T> withActive() {
        return (root, query, builder) ->
                builder.equal(root.get("status"), PostStatus.ACTIVE);
    }

    private static <T extends Post> Specification<T> withCategory(Long categoryId) {
        return (root, query, builder) ->
                builder.equal(root.get("category"), categoryId);
    }
}
