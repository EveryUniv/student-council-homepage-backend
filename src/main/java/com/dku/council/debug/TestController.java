package com.dku.council.debug;

import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnExpression("${app.enable-test-controller:false}")
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping
    public void free(AppAuthentication auth) {
    }

    @GetMapping("/user")
    @UserOnly
    public void useronly(AppAuthentication auth) {
    }

    @GetMapping("/admin")
    @AdminOnly
    public void adminonly(AppAuthentication auth) {
    }
}
