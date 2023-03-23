package com.dku.council.util.base;

import com.dku.council.global.auth.jwt.AuthenticationTokenProvider;
import com.dku.council.mock.user.UserAuth;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

public abstract class AbstractAuthControllerTest {

    @Autowired
    protected MockMvc mvc;

    @MockBean
    private AuthenticationTokenProvider jwtProvider;

    @BeforeEach
    void setUp() {
        UserAuth.withUser(1L);
    }
}
