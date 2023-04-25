package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.post.GenericPostRepository;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.tag.repository.TagRepository;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.model.dto.ResponseIdDto;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.TagMock;
import com.dku.council.mock.UserMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.EntityUtil;
import com.dku.council.util.MvcMockResponse;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@FullIntegrationTest
class NewsControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private GenericPostRepository<News> postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Major major;
    private User user;
    private List<News> allNews;
    private List<News> news1;

    @BeforeEach
    void setupUser() {
        major = majorRepository.save(MajorMock.create());

        user = UserMock.create(0L, major);
        user = userRepository.save(user);
        UserAuth.withUser(user.getId());

        allNews = new ArrayList<>();

        news1 = NewsMock.createList("news", user, 3);
        allNews.addAll(news1);

        List<News> news2 = NewsMock.createList("test", user, 2);
        allNews.addAll(news2);

        postRepository.saveAll(allNews);
        postRepository.saveAll(NewsMock.createList("ews", user, 3, false));
    }


    @Test
    @DisplayName("News 리스트 가져오기")
    void list() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/post/news"));

        // then
        Integer[] ids = EntityUtil.getIdArray(allNews);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(ids)));
    }

    @Test
    @DisplayName("News 리스트 가져오기 - 키워드 명시")
    void listWithKeyword() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/post/news")
                .param("keyword", "ews"));

        // then
        Integer[] ids = EntityUtil.getIdArray(news1);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(ids)));
    }

    @Test
    @DisplayName("News 리스트 가져오기 - 태그 명시")
    void listWithTags() throws Exception {
        // given
        Tag tag1 = TagMock.create();
        List<News> news2 = createPostsWithTag(tag1, 6);

        Tag tag2 = TagMock.create();
        createPostsWithTag(tag2, 7);

        // when
        ResultActions result = mvc.perform(get("/post/news")
                .param("tagIds", tag1.getId().toString()));

        // then
        Integer[] tag1Ids = EntityUtil.getIdArray(news2);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(tag1Ids)));
    }

    @Test
    @DisplayName("News 리스트 가져오기 - 여러 태그 명시")
    void listWithMultipleTags() throws Exception {
        // given
        Tag tag1 = TagMock.create();
        List<News> news2 = createPostsWithTag(tag1, 6);

        Tag tag2 = TagMock.create();
        List<News> news3 = createPostsWithTag(tag2, 7);

        // when
        ResultActions result = mvc.perform(get("/post/news")
                .param("tagIds", tag1.getId().toString())
                .param("tagIds", tag2.getId().toString()));

        // then
        List<News> expected = Stream.concat(news2.stream(), news3.stream())
                .collect(Collectors.toList());
        Integer[] allIds = EntityUtil.getIdArray(expected);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(allIds)));
    }

    private List<News> createPostsWithTag(Tag tag, int size) {
        List<News> newsList = NewsMock.createList("news-with-tag", user, size);
        tag = tagRepository.save(tag);
        newsList = postRepository.saveAll(newsList);
        for (News news : newsList) {
            tagService.addTagsToPost(news, List.of(tag.getId()));
        }
        return newsList;
    }

    @Test
    @DisplayName("News 생성")
    void create() throws Exception {
        // when
        UserAuth.withAdmin(user.getId());

        ResultActions result = mvc.perform(multipart("/post/news")
                .param("title", "제목")
                .param("body", "본문"));

        // then
        MvcResult response = result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andReturn();

        ResponseIdDto dto = MvcMockResponse.getResponse(objectMapper, response, ResponseIdDto.class);
        News actualNews = postRepository.findById(dto.getId()).orElseThrow();

        assertThat(actualNews.getTitle()).isEqualTo("제목");
        assertThat(actualNews.getBody()).isEqualTo("본문");
    }

    @Test
    @DisplayName("News 생성 - 태그 명시")
    void createWithTag() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());

        Tag tag = new Tag("tag");
        tagRepository.save(tag);
        Tag tag2 = new Tag("tag2");
        tagRepository.save(tag2);
        String[] tagIds = new String[]{tag.getId().toString(), tag2.getId().toString()};

        // when
        ResultActions result = mvc.perform(multipart("/post/news")
                .param("title", "제목")
                .param("body", "본문")
                .param("tagIds", tagIds));

        // then
        MvcResult response = result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andReturn();

        ResponseIdDto dto = MvcMockResponse.getResponse(objectMapper, response, ResponseIdDto.class);
        News actualNews = postRepository.findById(dto.getId()).orElseThrow();

        assertThat(actualNews.getTitle()).isEqualTo("제목");
        assertThat(actualNews.getBody()).isEqualTo("본문");
        assertThat(actualNews.getPostTags().get(0).getTag().getId()).isEqualTo(tag.getId());
        assertThat(actualNews.getPostTags().get(1).getTag().getId()).isEqualTo(tag2.getId());
    }

    @Test
    @DisplayName("News 단건조회")
    void findOne() throws Exception {
        // given
        News news = allNews.get(0);

        // when
        ResultActions result = mvc.perform(get("/post/news/" + news.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(news.getId().intValue())))
                .andExpect(jsonPath("title", is("news0")))
                .andExpect(jsonPath("body", is("0")))
                .andExpect(jsonPath("author", is(news.getDisplayingUsername())))
                .andExpect(jsonPath("mine", is(true)));
    }

    @Test
    @DisplayName("News 삭제")
    void delete() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        News news = allNews.get(0);

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.delete("/post/news/" + news.getId()));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("News 삭제 - Admin")
    void deleteByAdmin() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        News news = NewsMock.create(user);
        news = postRepository.save(news);

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.delete("/post/news/" + news.getId()));

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("News 삭제 실패 - 권한 없음")
    void failedDeleteByAccessDenied() throws Exception {
        // given
        News news = NewsMock.create(userRepository.save(UserMock.create(0L, major)));
        news = postRepository.save(news);

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.delete("/post/news/" + news.getId()));

        // then
        result.andExpect(status().isForbidden());
    }
}