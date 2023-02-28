package com.dku.council.domain.category.service;

import com.dku.council.domain.category.exception.CategoryIntegrityException;
import com.dku.council.domain.category.exception.CategoryNotFoundException;
import com.dku.council.domain.category.model.dto.CategoryDto;
import com.dku.council.domain.category.model.entity.Category;
import com.dku.council.domain.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository repository;

    /**
     * 카테고리 목록을 가져옵니다.
     *
     * @return 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> list() {
        List<Category> categories = repository.findAll();
        return categories.stream()
                .map(CategoryDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리를 추가합니다.
     *
     * @param name 카테고리 이름
     * @return 생성된 카테고리 아이디
     */
    public Long create(String name) {
        Category category = new Category(name);
        category = repository.save(category);
        return category.getId();
    }

    /**
     * 카테고리의 이름을 변경합니다.
     *
     * @param categoryId 카테고리 아이디
     * @param name       새로운 이름
     * @return 변경된 카테고리 아이디
     */
    public Long rename(Long categoryId, String name) {
        Category category = repository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        category.updateName(name);
        return categoryId;
    }

    /**
     * 카테고리를 삭제합니다.
     *
     * @param categoryId 카테고리 아이디
     * @return 삭제된 카테고리 아이디
     */
    public Long delete(Long categoryId) {
        Category category = repository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        try {
            repository.delete(category);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new CategoryIntegrityException(e);
        }
        return categoryId;
    }
}
