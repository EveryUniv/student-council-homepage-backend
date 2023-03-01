package com.dku.council.domain.post.controller;

import com.dku.council.common.AbstractContainerRedisTest;
import com.dku.council.common.MvcMockResponse;
import com.dku.council.common.OnlyDevTest;
import com.dku.council.domain.category.model.entity.Category;
import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.dto.ResponseIdDto;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.UserMock;
import com.dku.council.mock.user.UserAuth;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@OnlyDevTest
class NewsControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GenericPostRepository<News> postRepository;

    private User user;


    @BeforeEach
    void setupUser() {
        user = UserMock.create(0L);
        user = userRepository.save(user);
        UserAuth.withUser(user.getId());
    }


    @Test
    @DisplayName("News 리스트 가져오기")
    void list() throws Exception {
        // given
        List<News> news = NewsMock.createList("news", user, 10);
        postRepository.saveAll(news);

        // when
        ResultActions result = mvc.perform(get("/post/news"))
                .andDo(print());

        // then
        Integer[] ids = getIdArray(news, 10);
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].id", containsInAnyOrder(ids)));
    }

    private Integer[] getIdArray(List<News> news, int size) {
        Integer[] ids = new Integer[size];
        for (int i = 0; i < size; i++) {
            ids[i] = (int) news.get(i).getId().longValue();
        }
        return ids;
    }

    @Test
    @DisplayName("News 생성")
    void create() throws Exception {
        // when
        ResultActions result = mvc.perform(multipart("/post/news")
                        .param("title", "제목")
                        .param("body", "본문"))
                .andDo(print());

        // then
        MvcResult response = result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andReturn();

        ResponseIdDto dto = MvcMockResponse.getResponse(response, ResponseIdDto.class);
        News actualNews = postRepository.findById(dto.getId()).orElseThrow();

        assertThat(actualNews.getTitle()).isEqualTo("제목");
        assertThat(actualNews.getBody()).isEqualTo("본문");
    }

    @Test
    @DisplayName("News 생성 - 카테고리 명시")
    void createWithCategory() throws Exception {
        // given
        Category category = new Category("category");
        categoryRepository.save(category);

        // when
        ResultActions result = mvc.perform(multipart("/post/news")
                        .param("title", "제목")
                        .param("body", "본문")
                        .param("categoryId", category.getId().toString()))
                .andDo(print());

        // then
        MvcResult response = result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andReturn();

        ResponseIdDto dto = MvcMockResponse.getResponse(response, ResponseIdDto.class);
        News actualNews = postRepository.findById(dto.getId()).orElseThrow();

        assertThat(actualNews.getTitle()).isEqualTo("제목");
        assertThat(actualNews.getBody()).isEqualTo("본문");
        assertThat(actualNews.getCategory().getId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("News 단건조회")
    void findOne() throws Exception {
        // given
        News news = News.builder()
                .title("제목").body("본문").user(user).build();
        news = postRepository.save(news);

        // when
        ResultActions result = mvc.perform(get("/post/news/" + news.getId()))
                .andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(news.getId().intValue())))
                .andExpect(jsonPath("title", is("제목")))
                .andExpect(jsonPath("body", is("본문")))
                .andExpect(jsonPath("author", is(UserMock.NAME)))
                .andExpect(jsonPath("mine", is(true)));
    }

    @Test
    @DisplayName("News 삭제")
    void delete() throws Exception {
        // given
        News news = NewsMock.create(user, null);
        news = postRepository.save(news);

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.delete("/post/news/" + news.getId()))
                .andDo(print());

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("News 삭제 - Admin")
    void deleteByAdmin() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        News news = NewsMock.create();
        news = postRepository.save(news);

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.delete("/post/news/" + news.getId()))
                .andDo(print());

        // then
        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("News 삭제 실패 - 권한 없음")
    void failedDeleteByAccessDenied() throws Exception {
        // given
        News news = NewsMock.create();
        news = postRepository.save(news);

        // when
        ResultActions result = mvc.perform(MockMvcRequestBuilders.delete("/post/news/" + news.getId()))
                .andDo(print());

        // then
        result.andExpect(status().isForbidden());
    }
}