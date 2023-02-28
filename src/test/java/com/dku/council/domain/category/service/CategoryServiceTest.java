package com.dku.council.domain.category.service;

import com.dku.council.domain.category.exception.CategoryNotFoundException;
import com.dku.council.domain.category.model.dto.CategoryDto;
import com.dku.council.domain.category.model.entity.Category;
import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.mock.CategoryMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService service;


    @Test
    @DisplayName("카테고리 목록 불러오기")
    void list() {
        // given
        List<Category> categories = CategoryMock.createList(10);
        List<Long> repoIdList = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());
        when(repository.findAll()).thenReturn(categories);

        // when
        List<Long> idList = service.list().stream()
                .map(CategoryDto::getId)
                .collect(Collectors.toList());

        // then
        assertThat(repoIdList).containsExactlyInAnyOrderElementsOf(idList);
    }

    @Test
    @DisplayName("카테고리 추가하기")
    void create() {
        // given
        Category category = CategoryMock.create(10L);
        when(repository.save(any())).thenReturn(category);

        // when
        Long created = service.create("");

        // then
        assertThat(created).isEqualTo(10L);
    }

    @Test
    @DisplayName("카테고리 이름 변경")
    void rename() {
        // given
        Category category = CategoryMock.create(10L);
        when(repository.findById(any())).thenReturn(Optional.of(category));

        // when
        Long edited = service.rename(10L, "new name");

        // then
        assertThat(edited).isEqualTo(10L);
        assertThat(category.getName()).isEqualTo("new name");
    }

    @Test
    @DisplayName("카테고리 이름 변경 실패 - 카테고리가 없음")
    void failedRenameByNotFound() {
        // given
        when(repository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class,
                () -> service.rename(10L, "new name"));
    }

    @Test
    @DisplayName("카테고리 삭제")
    void delete() {
        // given
        Category category = CategoryMock.create(10L);
        when(repository.findById(any())).thenReturn(Optional.of(category));

        // when
        Long deleted = service.delete(10L);

        // then
        assertThat(deleted).isEqualTo(10L);
        verify(repository).delete(category);
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 카테고리가 없음")
    void failedDeletionByNotFound() {
        // given
        when(repository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class,
                () -> service.delete(10L));
    }
}