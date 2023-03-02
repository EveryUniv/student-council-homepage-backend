package com.dku.council.domain.post.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.dto.request.RequestCreateGenericPostDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.tag.exception.TagNotFoundException;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.tag.repository.TagRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.NotGrantedException;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

/**
 * 기본 기능만 제공하는 게시판을 대상으로 하는 서비스입니다.
 * 기본 기능은 제목, 본문, 댓글, 파일, 카테고리를 포함한 게시판을 의미합니다.
 * 왠만한 게시판들은 이 서비스로 커버가 가능합니다. 복잡한 조회, 생성, 비즈니스 로직이 포함된 게시판은
 * 이걸 사용하지말고 따로 만드는 게 낫습니다.
 * 이걸 사용하려면, 반드시 Bean에 등록되어있어야 합니다. (PostConfig)
 * todo 의존성을 좀 줄여보자
 *
 * @param <E> Entity 타입
 */
@RequiredArgsConstructor
public class GenericPostService<E extends Post> {

    protected final GenericPostRepository<E> postRepository;
    protected final UserRepository userRepository;
    protected final TagRepository tagRepository;
    protected final ViewCountService viewCountService;
    protected final FileUploadService fileUploadService;
    protected final MessageSource messageSource;


    /**
     * 게시글 목록으로 조회
     *
     * @param specification 검색 방법
     * @param pageable      페이징 방법
     * @return 페이징된 목록
     */
    @Transactional(readOnly = true)
    public Page<E> list(Specification<E> specification, Pageable pageable) {
        return postRepository.findAll(specification, pageable);
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
        Long categoryId = dto.getCategoryId();

        Tag tag = null;
        if (categoryId != null) {
            tag = tagRepository.findById(categoryId).orElseThrow(TagNotFoundException::new);
        }

        E post = dto.toEntity(user, tag);

        fileUploadService.uploadFiles(dto.getFiles(), "news")
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
    @Transactional(readOnly = true)
    public ResponseSingleGenericPostDto findOne(Long postId, Long userId, String remoteAddress) {
        E post = viewPost(postId, remoteAddress);
        return new ResponseSingleGenericPostDto(fileUploadService.getBaseURL(), userId, post);
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
     * post를 가져옵니다. deletion처리를 위해 repository에서 직접 가져오지말고 이걸 사용해야합니다.
     *
     * @param postId 조회할 게시글 id
     * @return 게시글 Entity
     */
    @Transactional(readOnly = true)
    public E findPost(Long postId) {
        E post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if (post.getStatus() != PostStatus.ACTIVE) {
            throw new PostNotFoundException();
        }
        return post;
    }

    /**
     * 게시글 삭제. 실제 DB에서 삭제처리되지 않고 표시만 해둔다.
     * todo 배치처리를 통해 12개월이 지난 게시글은 삭제처리
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

    /**
     * 첨부파일의 Base URL을 가져옵니다.
     *
     * @return 첨부파일 Base URL
     */
    public String getFileBaseUrl() {
        return fileUploadService.getBaseURL();
    }
}
