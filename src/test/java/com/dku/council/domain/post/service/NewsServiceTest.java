package com.dku.council.domain.post.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.dto.page.SummarizedNewsDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleNewsDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.NewsRepository;
import com.dku.council.domain.user.model.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.DummyMultipartFile;
import com.dku.council.infra.nhn.service.FileUploadService;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ViewCountService viewCountService;

    @Mock
    private FileUploadService fileUploadService;

    @InjectMocks
    private NewsService service;


    @Test
    @DisplayName("list가 잘 동작하는지?")
    public void list() {
        // given
        List<News> keywordNewsList = generateNewsList("keyword-", 10);
        List<News> allNewsList = generateNewsList("generic-", 20);

        Page<News> keywordNews = new DummyPage<>(keywordNewsList, 10);
        Page<News> allNews = new DummyPage<>(allNewsList, 10);

        when(newsRepository.findAll((Specification<News>) any(), (Pageable) any())).thenReturn(keywordNews);
        when(newsRepository.findAll((Pageable) any())).thenReturn(allNews);
        when(fileUploadService.getBaseURL()).thenReturn("http://base/");

        // when
        Page<SummarizedNewsDto> keywordPage = service.list("keyword", Pageable.unpaged());
        Page<SummarizedNewsDto> allPage = service.list(null, Pageable.unpaged());

        // then
        assertThat(keywordPage.getTotalElements()).isEqualTo(keywordNewsList.size());
        assertSummarizedNewsDtoList(keywordPage.getContent(), keywordNewsList);

        assertThat(allPage.getTotalElements()).isEqualTo(allNewsList.size());
        assertSummarizedNewsDtoList(allPage.getContent(), allNewsList);
    }

    private User generateUser() {
        return User.builder()
                .classId("11111111")
                .password("pwd")
                .name("name")
                .major(Major.ADMIN)
                .phone("010-1111-2222")
                .build();
    }

    private List<News> generateNewsList(String prefix, int size) {
        List<News> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            News news = News.builder()
                    .user(generateUser())
                    .title(prefix + i)
                    .body("")
                    .build();
            result.add(news);
        }

        return result;
    }

    private News generateNews(User user, Long newsId) throws NoSuchFieldException, IllegalAccessException {
        News news = News.builder()
                .user(user)
                .title("")
                .body("")
                .build();

        Field id = Post.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(news, newsId);

        return news;
    }

    private void assertSummarizedNewsDtoList(List<SummarizedNewsDto> actual, List<News> expected) {
        for (int i = 0; i < actual.size(); i++) {
            SummarizedNewsDto dto = actual.get(i);
            News news = expected.get(i);
            assertThat(dto.getTitle()).isEqualTo(news.getTitle());
        }
    }

    @Test
    @DisplayName("새롭게 잘 생성되는지?")
    public void create() throws NoSuchFieldException, IllegalAccessException {
        // given
        User user = generateUser();
        News news = generateNews(user, 3L);

        List<MultipartFile> files = generateMultipartFiles();
        RequestCreateNewsDto dto = new RequestCreateNewsDto("title", "body", files);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(newsRepository.save(any())).thenReturn(news);

        // when
        Long newsId = service.create(2L, dto);

        // then
        assertThat(newsId).isEqualTo(3L);

        verify(fileUploadService).uploadFiles(argThat(fileList -> {
            assertThat(fileList).isEqualTo(files);
            return true;
        }), any());

        verify(newsRepository).save(argThat(entity -> {
            assertThat(entity.getUser()).isEqualTo(user);
            return true;
        }));
    }

    private List<MultipartFile> generateMultipartFiles() {
        List<MultipartFile> files = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            files.add(new DummyMultipartFile("file", "myFile" + i + ".txt"));
        }
        return files;
    }

    @Test
    @DisplayName("생성할 때 유저가 없으면 오류")
    public void failedCreateByNotFoundUser() {
        // given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () ->
                service.create(2L, new RequestCreateNewsDto("title", "body", List.of())));
    }

    @Test
    @DisplayName("단건 조회가 잘 동작하는지?")
    public void findOne() throws NoSuchFieldException, IllegalAccessException {
        // given
        News news = generateNews(generateUser(), 4L);
        when(newsRepository.findById(any())).thenReturn(Optional.of(news));

        // when
        ResponseSingleNewsDto dto = service.findOne(4L, "Addr");

        // then
        verify(viewCountService).increasePostViews(argThat(post -> {
            assertThat(post.getId()).isEqualTo(news.getId());
            return true;
        }), eq("Addr"));

        assertThat(dto.getId()).isEqualTo(4L);
    }

    @Test
    @DisplayName("없는 게시글 단건 조회시 오류")
    public void failedFindOneByNotFound() {
        // given
        when(newsRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class, () ->
                service.findOne(0L, ""));
    }

    @Test
    @DisplayName("게시글 삭제가 잘 동작하는지?")
    public void delete() throws NoSuchFieldException, IllegalAccessException {
        // given
        News news = generateNews(generateUser(), 4L);
        List<PostFile> files = IntStream.range(1, 10)
                .mapToObj(i -> new PostFile("id" + i, "name" + i))
                .collect(Collectors.toList());
        attachFilesToNews(news, files);
        when(newsRepository.findById(any())).thenReturn(Optional.of(news));

        // when
        service.delete(4L);

        // then
        verify(fileUploadService).deletePostFiles(argThat(fileList -> {
            assertThat(fileList.size()).isEqualTo(news.getFiles().size());
            return true;
        }));

        verify(newsRepository).delete(eq(news));
    }

    private void attachFilesToNews(News news, List<PostFile> newFiles) throws NoSuchFieldException, IllegalAccessException {
        Field files = Post.class.getDeclaredField("files");
        files.setAccessible(true);
        files.set(news, newFiles);
    }

    @Test
    @DisplayName("없는 게시글 삭제시 오류")
    public void failedDeleteByNotFound() {
        // given
        when(newsRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(PostNotFoundException.class, () ->
                service.delete(0L));
    }
}