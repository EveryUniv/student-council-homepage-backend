package com.dku.council.domain.post.service.post;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGenericPostDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.repository.post.GenericPostRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.ThumbnailService;
import com.dku.council.domain.post.service.ViewCountService;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.NotGrantedException;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenericPostService<E extends Post> {

    protected final UserRepository userRepository;
    protected final TagService tagService;
    protected final ViewCountService viewCountService;
    protected final LikeService likeService;

    protected final FileUploadService fileUploadService;
    protected final ObjectUploadContext uploadContext;
    protected final ThumbnailService thumbnailService;

    /**
     * 게시글 목록으로 조회
     *
     * @param spec     검색 방법
     * @param pageable 페이징 방법
     * @return 페이징된 목록
     */
    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> list(GenericPostRepository<E> repository, Specification<E> spec,
                                               Pageable pageable, int bodySize, boolean withBlind) {
        Page<E> result = list(repository, spec, pageable, withBlind);
        return result.map((post) -> makeListDto(bodySize, post));
    }

    @Transactional(readOnly = true)
    public <T> Page<T> list(GenericPostRepository<E> repository, Specification<E> spec, Pageable pageable, int bodySize,
                            boolean withBlind, PostResultMapper<T, SummarizedGenericPostDto, E> mapper) {
        Page<E> result = list(repository, spec, pageable, withBlind);

        return result.map((post) -> {
            SummarizedGenericPostDto dto = makeListDto(bodySize, post);
            return mapper.map(dto, post);
        });
    }

    private Page<E> list(GenericPostRepository<E> repository, Specification<E> spec, Pageable pageable, boolean withBlind) {
        if (spec == null) {
            spec = Specification.where(null);
        }

        if (withBlind) {
            spec = spec.and(PostSpec.withActiveOrBlinded());
        } else {
            spec = spec.and(PostSpec.withActive());
        }

        return repository.findAll(spec, pageable);
    }

    private SummarizedGenericPostDto makeListDto(int bodySize, E post) {
        int likes = likeService.getCountOfLikes(post.getId(), LikeTarget.POST);
        return new SummarizedGenericPostDto(uploadContext, bodySize, likes, post);
    }

    /**
     * 게시글 등록
     *
     * @param userId 등록한 사용자 id
     * @param dto    게시글 dto
     */
    @Transactional
    public Long create(GenericPostRepository<E> repository, Long userId, RequestCreateGenericPostDto<E> dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        E post = dto.toEntity(user);
        tagService.addTagsToPost(post, dto.getTagIds());

        attachFiles(dto.getFiles(), post);

        E savedPost = repository.save(post);
        return savedPost.getId();
    }

    private void attachFiles(List<MultipartFile> dtoFiles, E post) {
        List<UploadedFile> files = fileUploadService.newContext().uploadFiles(
                FileRequest.ofList(dtoFiles),
                post.getClass().getSimpleName());

        FileUploadService.Context uploadCtx = fileUploadService.newContext();
        List<PostFile> postFiles = new ArrayList<>();

        for (UploadedFile file : files) {
            PostFile.PostFileBuilder builder = PostFile.builder()
                    .fileName(file.getOriginalName())
                    .mimeType(file.getMimeType().toString())
                    .fileId(file.getFileId());

            String thumbnailId = thumbnailService.createThumbnail(uploadCtx, file);
            if (thumbnailId != null) {
                builder.thumbnailId(thumbnailId);
            }
            postFiles.add(builder.build());
        }

        for (PostFile file : postFiles) {
            file.changePost(post);
        }
    }

    /**
     * 게시글 단건 조회
     *
     * @param postId        조회할 게시글 id
     * @param userId        조회하는 사용자 id. 내 게시글인지 판단하는데 사용된다.
     * @param remoteAddress 요청자 IP Address. 조회수 카운팅에 사용된다.
     * @return 게시글 정보
     */
    @Transactional
    public ResponseSingleGenericPostDto findOne(GenericPostRepository<E> repository, Long postId, Long userId,
                                                String remoteAddress, boolean withBlinded) {
        E post = viewPost(repository, postId, remoteAddress, withBlinded);
        return makePostDto(userId, post);
    }

    @Transactional
    public <T> T findOne(GenericPostRepository<E> repository, Long postId, Long userId, String remoteAddress,
                         boolean withBlinded, PostResultMapper<T, ResponseSingleGenericPostDto, E> mapper) {
        E post = viewPost(repository, postId, remoteAddress, withBlinded);
        ResponseSingleGenericPostDto dto = makePostDto(userId, post);
        return mapper.map(dto, post);
    }

    private ResponseSingleGenericPostDto makePostDto(Long userId, E post) {
        int likes = likeService.getCountOfLikes(post.getId(), LikeTarget.POST);
        boolean isMine = post.getUser().getId().equals(userId);
        boolean isLiked = likeService.isLiked(post.getId(), userId, LikeTarget.POST);
        return new ResponseSingleGenericPostDto(uploadContext, likes, isMine, isLiked, post);
    }

    /**
     * post를 가져옵니다. 조회와 동시에 조회수가 올라갑니다.
     *
     * @param postId        조회할 게시글 id
     * @param remoteAddress 요청자 IP Address. 조회수 카운팅에 사용된다.
     * @return 게시글 Entity
     */
    @Transactional
    public E viewPost(GenericPostRepository<E> repository, Long postId, String remoteAddress, boolean withBlind) {
        E post = findPost(repository, postId, withBlind);
        viewCountService.increasePostViews(post, remoteAddress);
        return post;
    }

    /**
     * post를 가져옵니다.
     *
     * @param repository 조회할 게시글 repository
     * @param postId     조회할 게시글 id
     * @return 게시글 Entity
     */
    @Transactional(readOnly = true)
    public E findPost(GenericPostRepository<E> repository, Long postId, boolean withBlind) {
        Optional<E> post;
        if (withBlind) {
            post = repository.findWithBlindedById(postId);
        } else {
            post = repository.findById(postId);
        }
        return post.orElseThrow(PostNotFoundException::new);
    }


    /**
     * 게시글 삭제. 실제 DB에서 삭제처리되지 않고 표시만 해둔다.
     *
     * @param repository 게시글 repository
     * @param postId     게시글 id
     * @param userId     삭제하는 사용자 id
     * @param isAdmin    사용자가 Admin인지?
     */
    @Transactional
    public void delete(GenericPostRepository<E> repository, Long postId, Long userId, boolean isAdmin) {
        E post = repository.findById(postId).orElseThrow(PostNotFoundException::new);
        if (isAdmin) {
            post.markAsDeleted(true);
        } else if (post.getUser().getId().equals(userId)) {
            post.markAsDeleted(false);
        } else {
            throw new NotGrantedException();
        }
    }

    /**
     * 게시글을 blind처리 합니다.
     *
     * @param postId 게시글 id
     */
    @Transactional
    public void blind(GenericPostRepository<E> repository, Long postId) {
        E post = repository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.blind();
    }

    /**
     * 게시글을 unblind처리 합니다.
     *
     * @param postId 게시글 id
     */
    @Transactional
    public void unblind(GenericPostRepository<E> repository, Long postId) {
        E post = repository.findBlindedPostById(postId).orElseThrow(PostNotFoundException::new);
        post.unblind();
    }


    @FunctionalInterface
    public interface PostResultMapper<T, D, E extends Post> {
        T map(D dto, E post);
    }
}
