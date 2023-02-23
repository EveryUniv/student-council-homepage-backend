package com.dku.council.domain.post.repository.spec;

import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;

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

    public static <T extends Post> Specification<T> isActive(){
        return (root, query, builder) ->
                builder.equal(root.get("status"), PostStatus.ACTIVE.name());
    }

    public static <T extends Post> Specification<T> withCategory(String category){
        return (root, query, builder) ->
                builder.equal(root.get("category"), category);
    }

    public static <T extends Post> Specification<T> condition(String keyword, String category) {
        Specification<T> active = isActive();
        if(keyword != null) active.and(withTitleOrBody(keyword));
        if(category != null) active.and(withCategory(category));
        return active;
    }


}
