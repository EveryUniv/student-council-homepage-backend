package com.dku.council.domain.post.service;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateReportDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.report.exception.AlreadyReportedException;
import com.dku.council.domain.report.model.entity.Report;
import com.dku.council.domain.report.model.entity.ReportCategory;
import com.dku.council.domain.report.repository.ReportRepository;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.NotGrantedException;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

/**
 * 기본 기능만 제공하는 게시판을 대상으로 하는 서비스입니다.
 * 기본 기능은 제목, 본문, 댓글, 파일, 태그를 포함한 게시판을 의미합니다.
 * 왠만한 게시판들은 이 서비스로 커버가 가능합니다. 복잡한 조회, 생성, 비즈니스 로직이 포함된 게시판은
 * 이걸 사용하지말고 따로 만드는 게 낫습니다.
 * 이걸 사용하려면, 반드시 Bean에 등록되어있어야 합니다. (PostConfig)
 *
 * @param <E> Entity 타입
 */
@RequiredArgsConstructor
public class GenericPostService<E extends Post> {

    protected final GenericPostRepository<E> postRepository;
    protected final UserRepository userRepository;
    protected final TagService tagService;
    protected final ViewCountService viewCountService;
    protected final FileUploadService fileUploadService;
    protected final LikeService likeService;

    /**
     * 게시글 목록으로 조회
     *
     * @param spec     검색 방법
     * @param pageable 페이징 방법
     * @return 페이징된 목록
     */
    @Transactional(readOnly = true)
    public Page<SummarizedGenericPostDto> list(Specification<E> spec, Pageable pageable, int bodySize) {
        Page<E> result = list(spec, pageable);
        return result.map((post) -> makeListDto(bodySize, post));
    }

    @Transactional(readOnly = true)
    public <T> Page<T> list(Specification<E> spec, Pageable pageable, int bodySize,
                            PostResultMapper<T, SummarizedGenericPostDto, E> mapper) {
        Page<E> result = list(spec, pageable);

        return result.map((post) -> {
            SummarizedGenericPostDto dto = makeListDto(bodySize, post);
            return mapper.map(dto, post);
        });
    }

    private Page<E> list(Specification<E> spec, Pageable pageable) {
        if (spec == null) {
            spec = Specification.where(null);
        }

        spec = spec.and(PostSpec.withActive());
        return postRepository.findAll(spec, pageable);
    }

    private SummarizedGenericPostDto makeListDto(int bodySize, E post) {
        int likes = likeService.getCountOfLikes(post.getId(), LikeTarget.POST);
        return new SummarizedGenericPostDto(fileUploadService.getBaseURL(), bodySize, likes, post);
    }

    /**
     * 게시글 등록
     *
     * @param userId 등록한 사용자 id
     * @param dto    게시글 dto
     */
    @Transactional
    public Long create(Long userId, RequestCreateGenericPostDto<E> dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        E post = dto.toEntity(user);
        tagService.addTagsToPost(post, dto.getTagIds());

        fileUploadService.uploadFiles(dto.getFiles(), post.getClass().getSimpleName())
                .forEach((file) -> new PostFile(file).changePost(post));

        E savedPost = postRepository.save(post);
        return savedPost.getId();
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
    public ResponseSingleGenericPostDto findOne(Long postId, Long userId, String remoteAddress) {
        E post = viewPost(postId, remoteAddress);
        return makePostDto(userId, post);
    }

    @Transactional
    public <T> T findOne(Long postId, Long userId, String remoteAddress,
                         PostResultMapper<T, ResponseSingleGenericPostDto, E> mapper) {
        E post = viewPost(postId, remoteAddress);
        ResponseSingleGenericPostDto dto = makePostDto(userId, post);
        return mapper.map(dto, post);
    }

    private ResponseSingleGenericPostDto makePostDto(Long userId, E post) {
        int likes = likeService.getCountOfLikes(post.getId(), LikeTarget.POST);
        boolean isMine = post.getUser().getId().equals(userId);
        boolean isLiked = likeService.isLiked(post.getId(), userId, LikeTarget.POST);
        return new ResponseSingleGenericPostDto(fileUploadService.getBaseURL(), likes, isMine, isLiked, post);
    }

    /**
     * post를 가져옵니다. 조회와 동시에 조회수가 올라갑니다.
     *
     * @param postId        조회할 게시글 id
     * @param remoteAddress 요청자 IP Address. 조회수 카운팅에 사용된다.
     * @return 게시글 Entity
     */
    @Transactional
    public E viewPost(Long postId, String remoteAddress) {
        E post = findPost(postId);
        viewCountService.increasePostViews(post, remoteAddress);
        return post;
    }

    /**
     * post를 가져옵니다.
     *
     * @param postId 조회할 게시글 id
     * @return 게시글 Entity
     */
    @Transactional(readOnly = true)
    public E findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
    }


    /**
     * 게시글 삭제. 실제 DB에서 삭제처리되지 않고 표시만 해둔다.
     *
     * @param postId      게시글 id
     * @param userId      삭제하는 사용자 id
     * @param isUserAdmin 사용자가 Admin인지?
     */
    @Transactional
    public void delete(Long postId, Long userId, boolean isUserAdmin) {
        E post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if (isUserAdmin) {
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
    public void blind(Long postId) {
        E post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.blind();
    }


        @FunctionalInterface
    public interface PostResultMapper<T, D, E extends Post> {
        T map(D dto, E post);
    }
}
