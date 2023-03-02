package com.dku.council.domain.tag.service;

import com.dku.council.domain.tag.exception.TagIntegrityException;
import com.dku.council.domain.tag.exception.TagNotFoundException;
import com.dku.council.domain.tag.model.dto.TagDto;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository repository;

    /**
     * 카테고리 목록을 가져옵니다.
     *
     * @return 카테고리 목록
     */
    @Transactional(readOnly = true)
    public List<TagDto> list() {
        List<Tag> categories = repository.findAll();
        return categories.stream()
                .map(TagDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리를 추가합니다.
     *
     * @param name 카테고리 이름
     * @return 생성된 카테고리 아이디
     */
    public Long create(String name) {
        Tag tag = new Tag(name);
        tag = repository.save(tag);
        return tag.getId();
    }

    /**
     * 카테고리의 이름을 변경합니다.
     *
     * @param categoryId 카테고리 아이디
     * @param name       새로운 이름
     * @return 변경된 카테고리 아이디
     */
    public Long rename(Long categoryId, String name) {
        Tag tag = repository.findById(categoryId).orElseThrow(TagNotFoundException::new);
        tag.updateName(name);
        return categoryId;
    }

    /**
     * 카테고리를 삭제합니다.
     *
     * @param categoryId 카테고리 아이디
     * @return 삭제된 카테고리 아이디
     */
    public Long delete(Long categoryId) {
        Tag tag = repository.findById(categoryId).orElseThrow(TagNotFoundException::new);
        try {
            repository.delete(tag);
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new TagIntegrityException(e);
        }
        return categoryId;
    }
}
