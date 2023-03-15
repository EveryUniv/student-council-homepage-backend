package com.dku.council.debug;

import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "테스트", description = "개발용 테스트 api")
@RestController
@ConditionalOnExpression("${app.enable-test-controller:false}")
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

    private final UserRepository userRepository;

    /**
     * 학번으로 유저 삭제.
     * 이 유저로 작성한 게시글, 댓글 등이 있는 경우에는 삭제되지 않습니다. 모두 삭제하고 유저를 삭제하세요.
     *
     * @param studentId 학번
     */
    @GetMapping
    public void deleteUser(@RequestParam("studentId") String studentId) {
        User user = userRepository.findByStudentId(studentId).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }
}
