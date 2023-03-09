package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.VocStatus;
import com.dku.council.domain.post.model.dto.request.RequestCreateReplyDto;
import com.dku.council.domain.post.model.entity.posttype.Voc;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.UserMock;
import com.dku.council.mock.VocMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.FullIntegrationTest;
import com.dku.council.util.base.AbstractContainerRedisTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@FullIntegrationTest
class VocControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenericPostRepository<Voc> postRepository;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private Voc voc;
    private User user;


    @BeforeEach
    void setupUser() {
        user = UserMock.create(11L);
        user = userRepository.save(user);

        User user2 = UserMock.create(11L);
        user2 = userRepository.save(user2);

        voc = VocMock.create(user, "title", "body");
        voc = postRepository.save(voc);

        Voc voc2 = VocMock.create(user2, "title2", "body2");
        postRepository.save(voc2);

        UserAuth.withUser(user.getId());
    }


    @Test
    @DisplayName("단건 조회")
    void findOne() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/post/voc/" + voc.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(voc.getId().intValue())))
                .andExpect(jsonPath("title", is("title")))
                .andExpect(jsonPath("body", is("body")))
                .andExpect(jsonPath("author", is(user.getName())))
                .andExpect(jsonPath("mine", is(true)))
                .andExpect(jsonPath("status", is(VocStatus.WAITING.name())));
    }

    @Test
    @DisplayName("내 Voc보기")
    void listMine() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/post/voc/my"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("content.size()", is(1)))
                .andExpect(jsonPath("content[0].id", is(voc.getId().intValue())))
                .andExpect(jsonPath("content[0].title", is("title")))
                .andExpect(jsonPath("content[0].body", is("body")))
                .andExpect(jsonPath("content[0].status", is(VocStatus.WAITING.name())));
    }

    @Test
    @DisplayName("답변 등록")
    void reply() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        RequestCreateReplyDto dto = new RequestCreateReplyDto("hello good");

        // when
        ResultActions result = mvc.perform(post("/post/voc/reply/" + voc.getId())
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
        assertThat(voc.getAnswer()).isEqualTo("hello good");
        assertThat(voc.getExtraStatus()).isEqualTo(VocStatus.ANSWERED);
    }
}