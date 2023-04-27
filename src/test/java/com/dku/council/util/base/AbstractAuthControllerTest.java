package com.dku.council.util.base;

import com.dku.council.mock.user.UserAuth;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public abstract class AbstractAuthControllerTest {

    protected static final Long USER_ID = 1L;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        UserAuth.withUser(USER_ID);
    }
}
