package com.dku.council.domain.tag.service;

import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.tag.exception.TagNotFoundException;
import com.dku.council.domain.tag.model.dto.TagDto;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.tag.repository.PostTagRepository;
import com.dku.council.domain.tag.repository.TagRepository;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.TagMock;
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
class TagServiceTest {

    @Mock
    private TagRepository repository;

    @Mock
    private PostTagRepository postTagRepository;

    @InjectMocks
    private TagService service;


    @Test
    @DisplayName("태그 목록 불러오기")
    void list() {
        // given
        List<Tag> categories = TagMock.createList(10);
        List<Long> repoIdList = categories.stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
        when(repository.findAll()).thenReturn(categories);

        // when
        List<Long> idList = service.list().stream()
                .map(TagDto::getId)
                .collect(Collectors.toList());

        // then
        assertThat(repoIdList).containsExactlyInAnyOrderElementsOf(idList);
    }

    @Test
    @DisplayName("태그 추가하기")
    void create() {
        // given
        Tag tag = TagMock.create(10L);
        when(repository.save(any())).thenReturn(tag);

        // when
        Long created = service.create("");

        // then
        assertThat(created).isEqualTo(10L);
    }


    @Test
    @DisplayName("태그 이름 변경")
    void rename() {
        // given
        Tag tag = TagMock.create(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(tag));

        // when
        Long edited = service.rename(10L, "new name");

        // then
        assertThat(edited).isEqualTo(10L);
        assertThat(tag.getName()).isEqualTo("new name");
    }

    @Test
    @DisplayName("태그 이름 변경 실패 - 태그가 없음")
    void failedRenameByNotFound() {
        // given
        when(repository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TagNotFoundException.class,
                () -> service.rename(10L, "new name"));
    }

    @Test
    @DisplayName("태그 삭제")
    void delete() {
        // given
        Tag tag = TagMock.create(10L);
        when(repository.findById(10L)).thenReturn(Optional.of(tag));

        // when
        Long deleted = service.delete(10L);

        // then
        assertThat(deleted).isEqualTo(10L);
        verify(repository).delete(tag);
    }

    @Test
    @DisplayName("태그 삭제 실패 - 태그가 없음")
    void failedDeletionByNotFound() {
        // given
        when(repository.findById(10L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(TagNotFoundException.class,
                () -> service.delete(10L));
    }

    @Test
    @DisplayName("Post에 태그 추가하기")
    void addTagsToPost() {
        // given
        Post post = NewsMock.createDummy();
        List<Long> tagIds = List.of(10L, 11L, 12L, 13L);
        List<Tag> tags = List.of(TagMock.create(10L), TagMock.create(11L), TagMock.create(12L), TagMock.create(13L));
        when(repository.findAllById(tagIds)).thenReturn(tags);

        // when
        service.addTagsToPost(post, tagIds);

        // then
        List<Long> actualTagIds = post.getPostTags().stream()
                .map((pt) -> pt.getTag().getId())
                .collect(Collectors.toList());

        assertThat(tagIds).containsExactlyInAnyOrderElementsOf(actualTagIds);
        verify(postTagRepository).saveAll(any());
    }
}