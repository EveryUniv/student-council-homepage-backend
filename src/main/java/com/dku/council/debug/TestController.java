package com.dku.council.debug;

import com.dku.council.domain.UserRole;
import com.dku.council.domain.user.User;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@ConditionalOnExpression("${app.enable-test-controller:false}")
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

    private final JwtProvider jwtProvider;

    @GetMapping("/ex")
    public void dtoController(@Valid @RequestBody TestDto dto) {
        log.info(dto.toString());
    }

    @GetMapping
    public AuthenticationToken test() {
        User user = new User(12L, UserRole.USER);
        return jwtProvider.issue(user);
    }

    @GetMapping("/auth")
    public String auth(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal);

        return "ok";
    }

    @GetMapping("/user")
    public String user(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal);

        return "ok";
    }
}
