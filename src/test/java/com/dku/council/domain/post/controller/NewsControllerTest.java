package com.dku.council.domain.post.controller;

import com.dku.council.common.AbstractContainerRedisTest;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.UserMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
//@DevTest
class NewsControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenericPostRepository<News> postRepository;


    @Test
    @DisplayName("News 리스트 가져오기")
    void list() throws Exception {
        // given
        User user = UserMock.create();
        userRepository.save(user);

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
}