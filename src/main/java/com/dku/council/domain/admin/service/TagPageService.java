package com.dku.council.domain.admin.service;

import com.dku.council.domain.admin.dto.TagPageDto;
import com.dku.council.domain.tag.exception.TagNotFoundException;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagPageService {
    private final TagRepository tagRepository;

    public List<TagPageDto> list(){
        return tagRepository.findAll().stream().map(TagPageDto::new).collect(Collectors.toList());
    }

    public Tag findOne(Long id){
        return tagRepository.findById(id).orElseThrow(TagNotFoundException::new);
    }

    public void delete(Long id){
        Tag tag = findOne(id);
        tagRepository.delete(tag);
    }

    public void create(String name){
        Tag tag = new Tag(name);
        tagRepository.save(tag);
    }

    public void rename(Long id, String name){
        Tag tag = findOne(id);
        tag.updateName(name);
    }

}
