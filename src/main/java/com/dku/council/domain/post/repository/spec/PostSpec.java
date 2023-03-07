package com.dku.council.domain.post.repository.spec;

import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.tag.model.entity.PostTag;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.List;

// TODO QueryDSL로 변경
public class PostSpec {

    public static <T extends Post> Specification<T> createPostCondition() {
        return withActive();
    }

    public static <T extends Post> Specification<T> genericPostCondition(String keyword, List<Long> tagIds) {
        Specification<T> spec = createPostCondition();
        if (keyword != null) spec = spec.and(withTitleOrBody(keyword));
        if (tagIds != null && !tagIds.isEmpty()) {
            Specification<T> orSpec = withTag(tagIds.get(0));
            for (int i = 1; i < tagIds.size(); i++) {
                orSpec = orSpec.or(withTag(tagIds.get(i)));
            }
            spec = spec.and(orSpec);
        }
        return spec;
    }

    public static <T extends Post> Specification<T> withAuthor(Long userId) {
        return (root, query, builder) ->
                builder.equal(root.get("user").get("id"), userId);
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

    private static <T extends Post> Specification<T> withTag(Long tagId) {
        return (root, query, builder) -> {
            Join<PostTag, Post> postTags = root.join("postTags");
            return builder.equal(postTags.get("tag").get("id"), tagId);
        };
    }
}
