package com.dku.council.domain.post.repository.spec;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.tag.model.entity.PostTag;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.util.List;


public class PostSpec {

    public static <T extends Post> Specification<T> withAuthor(Long userId) {
        if (userId == null) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("user").get("id"), userId);
    }

    public static <T extends Post> Specification<T> withPetitionStatus(PetitionStatus status) {
        if (status == null) {
            return Specification.where(null);
        }

        return (root, query, builder) ->
                builder.equal(root.get("extraStatus"), status);
    }

    public static <T extends Post> Specification<T> withTitleOrBody(String keyword) {
        if (keyword == null || keyword.equals("null")) {
            return Specification.where(null);
        }

        String pattern = "%" + keyword + "%";
        return (root, query, builder) ->
                builder.or(
                        builder.like(root.get("title"), pattern),
                        builder.like(root.get("body"), pattern)
                );
    }

    public static <T extends Post> Specification<T> withActive() {
        return (root, query, builder) ->
                builder.equal(root.get("status"), PostStatus.ACTIVE);
    }

    public static <T extends Post> Specification<T> withStatus(String status) {
        if(status == null || status.equals("null")) {
            return Specification.where(null);
        }

        PostStatus postStatus = PostStatus.valueOf(status);
        return (root, query, builder) ->
                builder.equal(root.get("status"), postStatus);
    }

    public static <T extends Post> Specification<T> withTag(Long tagId) {
        if (tagId == null) {
            return Specification.where(null);
        }

        return (root, query, builder) -> {
            Join<PostTag, Post> postTags = root.join("postTags");
            return builder.equal(postTags.get("tag").get("id"), tagId);
        };
    }

    public static <T extends Post> Specification<T> withTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Specification.where(null);
        }

        Specification<T> spec = withTag(tagIds.get(0));
        for (int i = 1; i < tagIds.size(); i++) {
            spec = spec.or(withTag(tagIds.get(i)));
        }
        return spec;
    }


}
