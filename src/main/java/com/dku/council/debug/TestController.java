package com.dku.council.debug;

import com.dku.council.domain.user.model.MajorData;
import com.dku.council.domain.user.model.UserRole;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "테스트", description = "개발용 테스트 api")
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
        User user = User.builder()
                .name("테스트")
                .studentId("32171111")
                .phone("010-1234-5678")
                .status(UserStatus.ACTIVE)
                .major(new Major(MajorData.COMPUTER_SCIENCE))
                .password("TestPwd")
                .role(UserRole.USER).build();
        return jwtProvider.issue(user);
    }

    // TODO Authentication말고 이를 상속받은 authentication도 자동으로 주입이 되는지 테스트
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
