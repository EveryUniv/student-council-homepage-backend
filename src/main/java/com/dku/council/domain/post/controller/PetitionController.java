package com.dku.council.domain.post.controller;

import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.list.SummarizedPetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreatePetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateReplyDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.service.post.PetitionService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.auth.role.UserAuth;
import com.dku.council.global.model.dto.ResponseIdDto;
import com.dku.council.global.util.RemoteAddressUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponsePage<SummarizedPetitionDto> list(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) List<Long> tagIds,
                                                    @RequestParam(required = false) PetitionStatus status,
                                                    @RequestParam(defaultValue = "50") int bodySize,
                                                    @ParameterObject Pageable pageable) {
        Page<SummarizedPetitionDto> list = petitionService.listPetition(keyword, tagIds, status,
                bodySize, pageable);
        return new ResponsePage<>(list);
    }

    /**
     * 게시글 등록
     *
     * @return 생성된 게시글 id
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @UserAuth
    public ResponseIdDto create(AppAuthentication auth, @Valid @ModelAttribute RequestCreatePetitionDto request) {
        Long postId = petitionService.create(auth.getUserId(), request);
        return new ResponseIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 청원 게시글 정보
     */
    @GetMapping("/{id}")
    @UserAuth
    public ResponsePetitionDto findOne(AppAuthentication auth,
                                       @PathVariable Long id,
                                       HttpServletRequest request) {
        return petitionService.findOnePetition(id, auth.getUserId(), auth.getUserRole(),
                RemoteAddressUtil.getProxyableAddr(request));
    }

    /**
     * 게시글을 블라인드 처리
     * 블라인드 처리된 게시글은 운영진만 볼 수 있습니다.
     *
     * @param id 블라인드 처리할 게시글 id
     */
    @PatchMapping("/blind/{id}")
    @AdminAuth
    public void blind(@PathVariable Long id) {
        petitionService.blind(id);
    }

    /**
     * 게시글을 블라인드 해제
     * 블라인드 처리된 게시글은 운영진만 볼 수 있습니다.
     *
     * @param id 블라인드 해제할 게시글 id
     */
    @PatchMapping("/unblind/{id}")
    @AdminAuth
    public void unblind(@PathVariable Long id) {
        petitionService.unblind(id);
    }

    /**
     * 운영진 답변 등록 (Admin)
     *
     * @param postId 답변할 게시글 ID
     * @param dto    요청 body
     */
    @PostMapping("/reply/{postId}")
    @AdminAuth
    public void reply(@PathVariable Long postId,
                      @Valid @RequestBody RequestCreateReplyDto dto) {
        petitionService.reply(postId, dto.getAnswer());
    }

    /**
     * 해당 게시글에 동의
     *
     * @param postId 동의할 게시글 id
     */
    @PostMapping("/agree/{postId}")
    @UserAuth
    public void agreePetition(AppAuthentication auth,
                              @PathVariable Long postId) {
        petitionService.agreePetition(postId, auth.getUserId());
    }


    /**
     * 게시글에 좋아요 표시
     * <p>중복으로 좋아요 표시해도 1개만 적용됩니다.</p>
     *
     * @param id 게시글 id
     */
    @PostMapping("/like/{id}")
    @UserAuth
    public void like(AppAuthentication auth, @PathVariable Long id) {
        likeService.like(id, auth.getUserId(), POST);
    }

    /**
     * 좋아요 취소
     * <p>중복으로 좋아요 취소해도 최초 1건만 적용됩니다.</p>
     *
     * @param id 게시글 id
     */
    @DeleteMapping("/like/{id}")
    @UserAuth
    public void cancelLike(AppAuthentication auth, @PathVariable Long id) {
        likeService.cancelLike(id, auth.getUserId(), POST);
    }
}
