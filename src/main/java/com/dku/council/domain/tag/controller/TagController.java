package com.dku.council.domain.tag.controller;

import com.dku.council.domain.tag.model.dto.RequestCreateTagDto;
import com.dku.council.domain.tag.model.dto.RequestRenameTagDto;
import com.dku.council.domain.tag.model.dto.TagDto;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.global.auth.role.AdminAuth;
import com.dku.council.global.model.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "태그", description = "태그 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/tag")
public class TagController {
    private final TagService tagService;

    /**
     * 태그 목록 가져오기
     * 모든 태그 목록을 가져옵니다. 태그는 모든 게시판이 공유해서 사용합니다.
     *
     * @return 태그 목록
     */
    @GetMapping
    public List<TagDto> list() {
        return tagService.list();
    }

    /**
     * 태그를 추가합니다.
     *
     * @param dto 요청 body
     * @return 생성된 태그 아이디
     */
    @PostMapping
    @AdminAuth
    public ResponseIdDto create(@Valid @RequestBody RequestCreateTagDto dto) {
        Long id = tagService.create(dto.getName());
        return new ResponseIdDto(id);
    }

    /**
     * 태그 이름을 변경합니다.
     *
     * @param tagId 태그 아이디
     * @param dto        요청 body
     * @return 변경된 태그 아이디 (입력받은 tagId와 동일)
     */
    @PatchMapping("/{tagId}")
    @AdminAuth
    public ResponseIdDto rename(@PathVariable Long tagId,
                                @Valid @RequestBody RequestRenameTagDto dto) {
        Long id = tagService.rename(tagId, dto.getName());
        return new ResponseIdDto(id);
    }

    /**
     * 태그를 삭제합니다.
     * 태그와 연관된 다른 포스트가 존재하면 태그를 삭제할 수 없습니다.
     *
     * @param tagId 태그 아이디
     * @return 삭제된 태그 아이디 (입력받은 tagId와 동일)
     */
    @DeleteMapping("/{tagId}")
    @AdminAuth
    public ResponseIdDto delete(@PathVariable Long tagId) {
        Long id = tagService.delete(tagId);
        return new ResponseIdDto(id);
    }
}
