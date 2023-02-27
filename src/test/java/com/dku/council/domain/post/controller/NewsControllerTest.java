package com.dku.council.domain.post.controller;

import com.dku.council.base.AbstractContainerRedisTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class NewsControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("News 리스트 가져오기")
    void list() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/"))
                .andDo(print());

        // then
        //result.
    }
}