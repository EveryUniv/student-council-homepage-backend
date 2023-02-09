package com.dku.council.debug;

import com.dku.council.domain.UserRole;
import com.dku.council.domain.user.User;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtAuthenticationTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final JwtAuthenticationTokenProvider jwtAuthenticationTokenProvider;

    @GetMapping
    public AuthenticationToken test(){
        User user = new User(12L, UserRole.ADMIN);
        return jwtAuthenticationTokenProvider.issue(user);
    }

    @GetMapping("/auth")
    public String auth(Authentication authentication){
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal);

        return "ok";
    }

    @GetMapping("/user")
    public String user(Authentication authentication){
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal);

        return "ok";
    }
}
