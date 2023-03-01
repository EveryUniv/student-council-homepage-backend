package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.dto.page.SummarizedPetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreatePetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateReplyDto;
import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.post.service.PetitionService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.dto.ResponseIdDto;
import com.dku.council.infra.nhn.service.FileUploadService;
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

@Tag(name = "청원 게시판", description = "청원 게시판 관련 api")
@RestController
@RequestMapping("/post/petition")
@RequiredArgsConstructor
public class PetitionController {

    private final PetitionService petitionService;
    private final FileUploadService fileUploadService;

    /**
     * 게시글 목록으로 조회
     *
     * @param keyword  제목이나 내용에 포함된 검색어. 지정하지 않으면 모든 게시글 조회.
     * @param pageable 페이징 size, sort, page
     * @return 페이징 된 회의록 목록
     */
    @GetMapping
    public ResponsePage<SummarizedPetitionDto> list(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Long categoryId,
                                                    @ParameterObject Pageable pageable) {
        Specification<Petition> spec = PostSpec.genericPostCondition(keyword, categoryId);
        Page<SummarizedPetitionDto> list = petitionService.list(spec, pageable)
                .map(post -> new SummarizedPetitionDto(fileUploadService.getBaseURL(), post, post.getComments().size())); // TODO 댓글 개수는 캐싱해서 사용하기 (반드시)
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
        Long postId = petitionService.create(auth.getUserId(), request);
        return new ResponseIdDto(postId);
    }

    /**
     * 게시글 단건 조회
     *
     * @param id 조회할 게시글 id
     * @return 총학소식 게시글 정보
     */
    @GetMapping("/{id}")
    @UserOnly
    public ResponsePetitionDto findOne(AppAuthentication auth,
                                       @PathVariable Long id,
                                       HttpServletRequest request) {
        return petitionService.findOnePetition(id, auth.getUserId(), request.getRemoteAddr());
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
        petitionService.delete(id, auth.getUserId(), auth.isAdmin());
    }

    /**
     * 운영진 답변 등록
     *
     * @param postId 답변할 게시글 ID
     * @param dto    요청 body
     */
    @DeleteMapping("/reply/{postId}")
    @AdminOnly
    public void reply(@PathVariable Long postId,
                      @Valid @RequestBody RequestCreateReplyDto dto) {
        petitionService.reply(postId, dto.getAnswer());
    }
}
