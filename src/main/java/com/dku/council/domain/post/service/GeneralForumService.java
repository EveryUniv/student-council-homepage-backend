package com.dku.council.domain.post.service;

import com.dku.council.domain.category.Category;
import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.exception.CategoryNotFoundException;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.dto.page.SummarizedGeneralForumDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGeneralForumDto;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.GeneralForumRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.NotGrantedException;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeneralForumService {

    private final GeneralForumRepository generalForumRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final CategoryRepository categoryRepository;
    private final ViewCountService viewCountService;

    /**
     * 게시글 목록으로 조회
     * @param keyword 검색 키워드
     * @param category 카테고리
     * @param pageable 페이징 size, sort, page
     * @return 페이징된 자유게시판 목록
     */
    public Page<SummarizedGeneralForumDto> list(String keyword, String category, Pageable pageable) {
        /**
         * todo : QueryDsl로 변환하기
         */
        Page<GeneralForum> page = generalForumRepository.findAll(PostSpec.condition(keyword, category), pageable);

        return page.map(generalForum -> new SummarizedGeneralForumDto(fileUploadService.getBaseURL(), generalForum));
    }

    /**
     * 게시글 등록
     *
     * @param userId 등록한 사용자 id
     * @param dto 게시글 dto
     */
    @Transactional
    public Long create(Long userId, RequestCreateGeneralForumDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(CategoryNotFoundException::new);
        GeneralForum generalForum = dto.toEntity(user, category);

        fileUploadService.uploadFiles(dto.getFiles(), "general-forum")
                .forEach((file) -> new PostFile(file).changePost(generalForum));

        GeneralForum save = generalForumRepository.save(generalForum);
        return save.getId();
    }

    /**
     * 게시글 단건 조회
     *
     * @param postId 조회할 게시글 id
     * @param remoteAddress 요청자 IP Address. 조회수 카운팅에 사용된다.
     * @return 자유게시판 게시글 정보
     */
    public ResponseSingleGeneralForumDto findOne(Long postId, String remoteAddress, Long userId) {
        GeneralForum generalForum = generalForumRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        viewCountService.increasePostViews(generalForum, remoteAddress);
        return new ResponseSingleGeneralForumDto(fileUploadService.getBaseURL(), generalForum, userId);
    }

    /**
     * 게시글 삭제 (상태변환)
     * @param postId  삭제할 게시글 id
     * @param userId  요청자 pk
     * @param isAdmin 관리자 권한 확인
     */
    @Transactional
    public void delete(Long postId, Long userId, boolean isAdmin) {
        GeneralForum generalForum = generalForumRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        if(isAdmin){
            generalForum.updateStatus(PostStatus.DELETED_BY_ADMIN);
        }
        else if(generalForum.getUser().getId().equals(userId)){
            generalForum.updateStatus(PostStatus.DELETED);
        }else{
            throw new NotGrantedException();
        }
    }
}
