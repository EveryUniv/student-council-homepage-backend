package com.dku.council.domain.post.repository.spec;

import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import org.springframework.data.jpa.domain.Specification;

// TODO Test 추가
public class PostSpec {
    public static <T extends Post> Specification<T> withTitleOrBody(String keyword) {
        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.and(
                        builder.equal(root.get("status"), PostStatus.ACTIVE.name()),
                        builder.or(
                                builder.like(root.get("title"), pattern),
                                builder.like(root.get("body"), pattern)
                        )
                );
    }

    public static <T extends Post> Specification<T> withCategory(String category){
        return (root, query, builder) ->
                builder.and(
                        builder.equal(root.get("category"), category),
                        builder.equal(root.get("status"), PostStatus.ACTIVE.name())
                );
    }

    public static <T extends Post> Specification<T> withQueryAndCategory(String keyword, String category){
        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.and(
                        builder.equal(root.get("category"), category),
                        builder.equal(root.get("status"), PostStatus.ACTIVE.name()),
                        builder.or(
                                builder.like(root.get("title"), pattern),
                                builder.like(root.get("body"), pattern)
                        )
                );
    }

}
