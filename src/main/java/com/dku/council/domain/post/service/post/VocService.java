package com.dku.council.domain.post.service.post;

import com.dku.council.domain.post.model.VocStatus;
import com.dku.council.domain.post.model.dto.list.SummarizedVocDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateVocDto;
import com.dku.council.domain.post.model.dto.response.ResponseVocDto;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import com.dku.council.domain.post.repository.post.VocRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.global.auth.role.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VocService {

    private final GenericPostService<Voc> postService;
    private final VocRepository repository;


    public Page<SummarizedVocDto> list(String keyword, List<Long> tagIds, Pageable pageable, int bodySize) {
        Specification<Voc> spec = PostSpec.withTitleOrBody(keyword);
        spec = spec.and(PostSpec.withTags(tagIds));
        return postService.list(repository, spec, pageable, bodySize, SummarizedVocDto::new);
    }

    public Page<SummarizedVocDto> listMine(String keyword, List<Long> tagIds, Long userId, Pageable pageable,
                                           int bodySize) {
        Specification<Voc> spec = PostSpec.withTitleOrBody(keyword);
        spec = spec.and(PostSpec.withTags(tagIds));
        spec = spec.and(PostSpec.withAuthor(userId));
        return postService.list(repository, spec, pageable, bodySize, SummarizedVocDto::new);
    }

    public ResponseVocDto findOne(Long postId, Long userId, UserRole role, String address) {
        return postService.findOne(repository, postId, userId, role, address, ResponseVocDto::new);
    }

    @Transactional
    public void reply(Long postId, String answer) {
        Voc post = postService.findPost(repository, postId, UserRole.ADMIN);
        post.replyAnswer(answer);
        post.updateVocStatus(VocStatus.ANSWERED);
    }

    public Long create(Long userId, RequestCreateVocDto request) {
        return postService.create(repository, userId, request);
    }

    public void delete(Long id, Long userId, boolean admin) {
        postService.delete(repository, id, userId, admin);
    }

    public void blind(Long id) {
        postService.blind(repository, id);
    }

    public void unblind(Long id) {
        postService.unblind(repository, id);
    }
}
