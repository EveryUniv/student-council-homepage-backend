package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.PostPageDto;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import com.dku.council.domain.post.repository.post.*;
import com.dku.council.domain.post.repository.spec.PostSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostPageService {
    private final GenericPostRepository<Post> genericPostRepository;
    private final PostRepository postRepository;
    private final VocRepository vocRepository;
    private final PetitionRepository petitionRepository;
    private final GeneralForumRepository generalForumRepository;

    // TODO 코드를 리팩토링하자..
    public Page<PostPageDto> list(String keyword, String type, String status, Pageable pageable) {
        if (type != null) {
            if (type.equalsIgnoreCase("Voc")) {
                Specification<Voc> spec = PostSpec.withTitleOrBody(keyword);
                spec = spec.and(PostSpec.withStatus(status));
                return vocRepository.findAll(spec, pageable).map(PostPageDto::new);
            } else if (type.equalsIgnoreCase("Petition")) {
                Specification<Petition> spec = PostSpec.withTitleOrBody(keyword);
                spec = spec.and(PostSpec.withStatus(status));
                return petitionRepository.findAll(spec, pageable).map(PostPageDto::new);
            } else if (type.equalsIgnoreCase("GeneralForum")) {
                Specification<GeneralForum> spec = PostSpec.withTitleOrBody(keyword);
                spec = spec.and(PostSpec.withStatus(status));
                return generalForumRepository.findAll(spec, pageable).map(PostPageDto::new);
            }
        }

        Specification<Post> spec = PostSpec.withTitleOrBody(keyword);
        spec = spec.and(PostSpec.withStatus(status));
        return genericPostRepository.findAll(spec, pageable).map(PostPageDto::new);
    }

    public Post findOne(Long id) {
        return postRepository.findByIdWithAdmin(id).orElseThrow(PostNotFoundException::new);
    }

    public void delete(Long id) {
        Post post = findOne(id);
        post.markAsDeleted(true);
    }

    public void blind(Long id) {
        Post post = findOne(id);
        post.blind();
    }

    public void active(Long id) {
        Post post = findOne(id);
        post.unblind();
    }

}
