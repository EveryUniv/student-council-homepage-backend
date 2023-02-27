package com.dku.council.domain.post.controller;

import com.dku.council.common.AbstractContainerRedisTest;
import com.dku.council.common.DevTest;
import com.dku.council.domain.category.Category;
import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.model.dto.request.RequestLoginDto;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.domain.user.service.UserService;
import com.dku.council.global.auth.jwt.JwtProvider;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.UserMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@DevTest
class NewsControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GenericPostRepository<News> postRepository;

    private User user;
    private String auth;


    @BeforeEach
    void setupUser() {
        user = UserMock.create(0L, passwordEncoder);
        user = userRepository.save(user);

        auth = "Bearer " + userService.login(new RequestLoginDto(UserMock.STUDENT_ID, UserMock.PASSWORD))
                .getAccessToken();
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
                .andExpect(jsonPath("$.content[*].id", Matchers.containsInAnyOrder(ids)));
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
                        .header(JwtProvider.AUTHORIZATION, auth)
                        .param("title", "제목")
                        .param("body", "본문"))
                .andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists());

        List<News> all = postRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.get(0).getTitle()).isEqualTo("제목");
        assertThat(all.get(0).getBody()).isEqualTo("본문");
    }

    @Test
    @DisplayName("News 생성 - 카테고리 명시")
    void createWithCategory() throws Exception {
        // given
        Category category = new Category("category");
        categoryRepository.save(category);

        // when
        ResultActions result = mvc.perform(multipart("/post/news")
                        .header(JwtProvider.AUTHORIZATION, auth)
                        .param("title", "제목")
                        .param("body", "본문")
                        .param("categoryId", category.getId().toString()))
                .andDo(print());

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id").exists());

        List<News> all = postRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.get(0).getTitle()).isEqualTo("제목");
        assertThat(all.get(0).getBody()).isEqualTo("본문");
        assertThat(all.get(0).getCategory().getId()).isEqualTo(category.getId());
    }
}