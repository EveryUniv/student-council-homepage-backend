package com.dku.council.domain.category.controller;

import com.dku.council.domain.category.model.dto.CategoryDto;
import com.dku.council.domain.category.model.dto.RequestCreateCategoryDto;
import com.dku.council.domain.category.model.dto.RequestRenameCategoryDto;
import com.dku.council.domain.category.service.CategoryService;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "카테고리", description = "카테고리 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/post/category")
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 카테고리 목록 가져오기
     * 모든 카테고리 목록을 가져옵니다. 카테고리는 모든 게시판이 공유해서 사용합니다.
     *
     * @return 카테고리 목록
     */
    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.list();
    }

    /**
     * 카테고리를 추가합니다.
     *
     * @param dto 요청 body
     * @return 생성된 카테고리 아이디
     */
    @PostMapping
    @AdminOnly
    public ResponseIdDto create(@Valid @RequestBody RequestCreateCategoryDto dto) {
        Long id = categoryService.create(dto.getName());
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
                                @Valid @RequestBody RequestRenameCategoryDto dto) {
        Long id = categoryService.rename(categoryId, dto.getName());
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
        Long id = categoryService.delete(categoryId);
        return new ResponseIdDto(id);
    }
}
