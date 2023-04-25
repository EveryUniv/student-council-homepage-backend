package com.dku.council.debug.controller;

import com.dku.council.debug.service.ErrorLogService;
import com.dku.council.domain.user.exception.AlreadyNicknameException;
import com.dku.council.domain.user.exception.AlreadyPhoneException;
import com.dku.council.domain.user.exception.AlreadyStudentIdException;
import com.dku.council.domain.user.exception.MajorNotFoundException;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "테스트", description = "개발용 테스트 api")
@RestController
@ConditionalOnExpression("${app.enable-test-controller:false}")
@RequiredArgsConstructor
@RequestMapping("/test")
@Slf4j
public class TestController {

    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final PasswordEncoder passwordEncoder;
    private final ErrorLogService errorLogService;

    /**
     * 학번으로 유저 삭제.
     * <p>이 유저로 작성한 게시글, 댓글 등이 있는 경우에는 삭제되지 않습니다. 모두 삭제하고 유저를 삭제하세요.</p>
     *
     * @param studentId 학번
     */
    @DeleteMapping("/user")
    public void deleteUser(@RequestParam String studentId) {
        User user = userRepository.findByStudentId(studentId).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    /**
     * 유저 강제 회원가입
     * <p>인증없이 강제로 회원가입합니다. 단, 학번, 닉네임, 전화번호는 중복되면 안됩니다.</p>
     *
     * @param studentId 학번
     */
    @PostMapping("/user")
    public void addUser(@RequestParam String studentId,
                        @RequestParam String password,
                        @RequestParam String name,
                        @RequestParam String nickname,
                        @RequestParam Long majorId,
                        @RequestParam Integer yearOfAdmission,
                        @RequestParam String phone) {
        if (userRepository.findByStudentId(studentId).isPresent()) {
            throw new AlreadyStudentIdException();
        }

        if (userRepository.findByPhone(phone).isPresent()) {
            throw new AlreadyPhoneException();
        }

        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new AlreadyNicknameException();
        }

        Major major = majorRepository.findById(majorId)
                .orElseThrow(MajorNotFoundException::new);

        String encryptedPassword = passwordEncoder.encode(password);

        User user = User.builder()
                .studentId(studentId)
                .password(encryptedPassword)
                .name(name)
                .nickname(nickname)
                .major(major)
                .yearOfAdmission(yearOfAdmission)
                .academicStatus("재학")
                .phone(phone)
                .build();

        userRepository.save(user);
    }

    /**
     * 에러 로그 조회
     * <p>trackingId를 통해 에러 로그를 조회합니다.</p>
     *
     * @param trackingId 에러 로그의 trackingId
     * @return 에러 로그
     */
    @GetMapping(value = "/error/{trackingId}", produces = "text/plain;charset=UTF-8")
    public String findError(@PathVariable String trackingId) {
        return errorLogService.findErrorLog(trackingId);
    }
}
