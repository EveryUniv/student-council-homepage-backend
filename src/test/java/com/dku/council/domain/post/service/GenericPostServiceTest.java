package com.dku.council.domain.post.service;

import com.dku.council.domain.like.service.impl.CachedLikeServiceImpl;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.post.GenericPostRepository;
import com.dku.council.domain.post.service.post.GenericPostService;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.auth.role.UserRole;
import com.dku.council.global.error.exception.NotGrantedException;
import com.dku.council.global.error.exception.UserNotFoundException;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import com.dku.council.mock.MultipartFileMock;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static com.dku.council.domain.like.model.LikeTarget.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenericPostServiceTest {

    @Mock
    private GenericPostRepository<News> newsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagService tagService;

    @Mock
    private ViewCountService viewCountService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private FileUploadService.Context fileUploadContext;

    @Mock
    private ObjectUploadContext uploadContext;

    @Mock
    private ThumbnailService thumbnailService;

    @Mock
    private CachedLikeServiceImpl postLikeService;

    @InjectMocks
    private GenericPostService<News> newsService;


    @Test
    @DisplayName("list가 잘 동작하는지?")
    public void list() {
        // given
        List<News> allNewsList = NewsMock.createListDummy("generic-", 20);
        Page<News> allNews = new DummyPage<>(allNewsList, 20);

        when(newsRepository.findAll((Specification<News>) any(), (Pageable) any())).thenReturn(allNews);
        when(postLikeService.getCountOfLikes(any(), eq(POST))).thenReturn(15);

        // when
        Page<SummarizedGenericPostDto> allPage = newsService.list(newsRepository, null, Pageable.unpaged(),
                500);

        // then
        assertThat(allPage.getTotalElements()).isEqualTo(allNewsList.size());
        for (int i = 0; i < allPage.getTotalElements(); i++) {
            SummarizedGenericPostDto dto = allPage.getContent().get(i);
            News news = allNewsList.get(i);
            assertThat(dto.getId()).isEqualTo(news.getId());
            assertThat(dto.getTitle()).isEqualTo(news.getTitle());
            assertThat(dto.getBody()).isEqualTo(news.getBody());
            assertThat(dto.getLikes()).isEqualTo(15);
            assertThat(dto.getViews()).isEqualTo(news.getViews());
        }
    }

    @Test
    @DisplayName("새롭게 잘 생성되는지?")
    public void create() {
        // given
        User user = UserMock.createDummyMajor(99L);
        News news = NewsMock.create(user, 3L);

        List<MultipartFile> files = MultipartFileMock.createList(10);
        RequestCreateNewsDto dto = new RequestCreateNewsDto("title", "body", null, files);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(newsRepository.save(any())).thenReturn(news);
        when(fileUploadService.newContext()).thenReturn(fileUploadContext);

        // when
        Long newsId = newsService.create(newsRepository, 2L, dto);

        // then
        assertThat(newsId).isEqualTo(3L);

        verify(fileUploadContext).uploadFiles(argThat(fileList -> {
            assertThat(fileList).hasSize(files.size());
            for (int i = 0; i < files.size(); i++) {
                assertThat(fileList.get(i).getOriginalFilename()).isEqualTo(files.get(i).getOriginalFilename());
            }
            return true;
        }), any());

        verify(newsRepository).save(argThat(entity -> {
            assertThat(entity.getUser()).isEqualTo(user);
            return true;
        }));
    }

    @Test
    @DisplayName("태그를 명시하며 생성하기")
    public void createWithTag() {
        // given
        User user = UserMock.createDummyMajor(99L);
        News news = NewsMock.create(user, 3L);
        List<Long> tagIds = List.of(10L, 11L, 12L, 13L);

        RequestCreateNewsDto dto = new RequestCreateNewsDto("title", "body", tagIds, List.of());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(newsRepository.save(any())).thenReturn(news);
        when(fileUploadService.newContext()).thenReturn(fileUploadContext);

        // when
        Long newsId = newsService.create(newsRepository, 2L, dto);

        // then
        assertThat(newsId).isEqualTo(3L);

        verify(newsRepository).save(argThat(entity -> {
            assertThat(entity.getUser()).isEqualTo(user);
            return true;
        }));
        verify(tagService).addTagsToPost(any(), eq(tagIds));
    }


    @Test
    @DisplayName("생성할 때 유저가 없으면 오류")
    public void failedCreateByNotFoundUser() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () ->
                newsService.create(newsRepository, 2L,
                        new RequestCreateNewsDto("title", "body", List.of(), List.of())));
    }

    @Test
    @DisplayName("단건 조회가 잘 동작하는지?")
    public void findOne() {
        // given
        News news = NewsMock.createDummy(4L);
        when(newsRepository.findById(any())).thenReturn(Optional.of(news));
        when(postLikeService.isLiked(any(), any(), eq(POST))).thenReturn(false);

        // when
        ResponseSingleGenericPostDto dto = newsService.findOne(newsRepository, 4L,
                news.getUser().getId(), news.getUser().getUserRole(), "Addr");

        // then
        verify(viewCountService).increasePostViews(argThat(post -> {
            assertThat(post.getId()).isEqualTo(news.getId());
            return true;
        }), eq("Addr"));

        assertThat(dto.getId()).isEqualTo(4L);
        assertThat(dto.isLiked()).isEqualTo(false);
        assertThat(dto.isMine()).isEqualTo(true);
    }


    @Test
    @DisplayName("없는 게시글 단건 조회시 오류")
    public void failedFindOneByNotFound() {
        // given
        when(newsRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class, () ->
                newsService.findOne(newsRepository, 0L, 4L, UserRole.USER, "Addr"));
    }

    @Test
    @DisplayName("없는 게시글 삭제시 오류")
    public void failedDeleteByNotFound() {
        // given
        when(newsRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class, () ->
                newsService.delete(newsRepository, 0L, 0L, false));
    }

    @Test
    @DisplayName("권한 없는 게시글 삭제시 오류")
    public void failedDeleteByAccessDenied() {
        // given
        News news = NewsMock.createDummy(4L);
        when(newsRepository.findById(any())).thenReturn(Optional.of(news));

        // when & then
        assertThrows(NotGrantedException.class, () ->
                newsService.delete(newsRepository, 0L, 0L, false));
    }
}