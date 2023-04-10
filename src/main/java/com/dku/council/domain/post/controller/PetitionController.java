package com.dku.council.domain.post.controller;

import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.list.SummarizedPetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreatePetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateReplyDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.domain.post.service.PetitionService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.jwt.JwtProvider;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.model.dto.ResponseIdDto;
import com.dku.council.global.util.RemoteAddressUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static com.dku.council.domain.like.model.LikeTarget.POST;

@Tag(name = "청원 게시판", description = "청원 게시판 관련 api")
@RestController
@RequestMapping("/post/petition")
@RequiredArgsConstructor
public class PetitionController {

    private final PetitionService petitionService;
    private final GenericPostService<Petition> petitionPostService;
    private final LikeService likeService;

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword  제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param tagIds   조회할 태그 목록. or 조건으로 검색된다. 지정하지않으면 모든 게시글 조회.
     * @param status   조회할 청원 상태. 지정하지 않으면 모든 게시글 조회.
     * @param bodySize 게시글 본문 길이. (글자 단위) 지정하지 않으면 50 글자.
     * @param pageable 페이징 size, sort, page
     * @return 페이징 된 청원 목록
     */
    @GetMapping
    @SecurityRequirement(name = JwtProvider.AUTHORIZATION)
    public ResponsePage<SummarizedPetitionDto> list(AppAuthentication auth,
                                                    @RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) List<Long> tagIds,
                                                    @RequestParam(required = false) PetitionStatus status,
                                                    @RequestParam(defaultValue = "50") int bodySize,
                                                    @ParameterObject Pageable pageable) {
        Specification<Petition> spec = PostSpec.withTitleOrBody(keyword);
        spec = spec.and(PostSpec.withPetitionStatus(status));
        spec = spec.and(PostSpec.withTags(tagIds));
        Page<SummarizedPetitionDto> list = petitionService.listPetition(spec, bodySize, pageable, auth.getUserId());
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     *
     * @return 생성된 게시글 id
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth, @Valid @ModelAttribute RequestCreatePetitionDto request) {
        Long postId = petitionPostService.create(auth.getUserId(), request);
        return new ResponseIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 청원 게시글 정보
     */
    @GetMapping("/{id}")
    @UserOnly
    public ResponsePetitionDto findOne(AppAuthentication auth,
                                       @PathVariable Long id,
                                       HttpServletRequest request) {
        return petitionService.findOnePetition(id, auth.getUserId(), RemoteAddressUtil.getProxyableAddr(request));
    }

    /**
     * 게시글 삭제 (Admin)
     * 운영진만 삭제할 수 있습니다. 청원 게시판에서는 본인이 작성한 게시글이어도 삭제할 수 없습니다.
     *
     * @param id 삭제할 게시글 id
     */
    @DeleteMapping("/{id}")
    @AdminOnly
    public void delete(AppAuthentication auth, @PathVariable Long id) {
        petitionPostService.delete(id, auth.getUserId(), auth.isAdmin());
    }

    /**
     * 게시글을 블라인드 처리
     * 블라인드 처리된 게시글은 운영진만 볼 수 있습니다.
     *
     * @param id 블라인드 처리할 게시글 id
     */
    @PatchMapping("/blind/{id}")
    @AdminOnly
    public void blind(@PathVariable Long id) {
        petitionPostService.blind(id);
    }

/**
     * 게시글을 블라인드 해제
     * 블라인드 처리된 게시글은 운영진만 볼 수 있습니다.
     *
     * @param id 블라인드 해제할 게시글 id
     */
    @PatchMapping("/unblind/{id}")
    @AdminOnly
    public void unblind(@PathVariable Long id) {
        petitionPostService.unblind(id);
    }

    /**
     * 운영진 답변 등록 (Admin)
     *
     * @param postId 답변할 게시글 ID
     * @param dto    요청 body
     */
    @PostMapping("/reply/{postId}")
    @AdminOnly
    public void reply(AppAuthentication auth,
                      @PathVariable Long postId,
                      @Valid @RequestBody RequestCreateReplyDto dto) {
        petitionService.reply(postId, dto.getAnswer(), auth.getUserId());
    }

    /**
     * 동의 하기 : 해당 게시글에 동의합니다. (default : 동의합니다)
     *
     * @param postId    동의할 게시글 id
     */
    @PostMapping("/agree/{postId}")
    @UserOnly
    public void agreePetition(AppAuthentication auth,
                                       @PathVariable Long postId) {
        petitionService.agreePetition(postId, auth.getUserId());
    }


    /**
     * 게시글에 좋아요 표시
     * 중복으로 좋아요 표시해도 1개만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @PostMapping("/like/{id}")
    @UserOnly
    public void like(AppAuthentication auth, @PathVariable Long id) {
        likeService.like(id, auth.getUserId(), POST);
    }

    /**
     * 좋아요 취소
     * 중복으로 좋아요 취소해도 최초 1건만 적용됩니다.
     *
     * @param id 게시글 id
     */
    @DeleteMapping("/like/{id}")
    @UserOnly
    public void cancelLike(AppAuthentication auth, @PathVariable Long id) {
        likeService.cancelLike(id, auth.getUserId(), POST);
    }
}
