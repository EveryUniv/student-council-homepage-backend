package com.dku.council.domain.tag.controller;

import com.dku.council.domain.tag.model.dto.RequestCreateTagDto;
import com.dku.council.domain.tag.model.dto.RequestRenameTagDto;
import com.dku.council.domain.tag.model.dto.TagDto;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "태그", description = "태그 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/category")
public class TagController {
    private final TagService tagService;

    /**
     * 카테고리 목록 가져오기
     * 모든 카테고리 목록을 가져옵니다. 카테고리는 모든 게시판이 공유해서 사용합니다.
     *
     * @return 카테고리 목록
     */
    @GetMapping
    public List<TagDto> list() {
        return tagService.list();
    }

    /**
     * 카테고리를 추가합니다.
     *
     * @param dto 요청 body
     * @return 생성된 카테고리 아이디
     */
    @PostMapping
    @AdminOnly
    public ResponseIdDto create(@Valid @RequestBody RequestCreateTagDto dto) {
        Long id = tagService.create(dto.getName());
        return new ResponseIdDto(id);
    }

    /**
     * 카테고리 이름을 변경합니다.
     *
     * @param categoryId 카테고리 아이디
     * @param dto        요청 body
     * @return 변경된 카테고리 아이디
     */
    @PatchMapping("/{categoryId}")
    @AdminOnly
    public ResponseIdDto rename(@PathVariable Long categoryId,
                                @Valid @RequestBody RequestRenameTagDto dto) {
        Long id = tagService.rename(categoryId, dto.getName());
        return new ResponseIdDto(id);
    }

    /**
     * 카테고리를 삭제합니다.
     * 카테고리에 속한 다른 포스트가 존재하면 카테고리를 삭제할 수 없습니다.
     *
     * @param categoryId 카테고리 아이디
     * @return 삭제된 카테고리 아이디
     */
    @DeleteMapping("/{categoryId}")
    @AdminOnly
    public ResponseIdDto delete(@PathVariable Long categoryId) {
        Long id = tagService.delete(categoryId);
        return new ResponseIdDto(id);
    }
}
