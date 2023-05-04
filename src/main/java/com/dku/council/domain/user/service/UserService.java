package com.dku.council.domain.user.service;

import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.domain.user.exception.AlreadyNicknameException;
import com.dku.council.domain.user.exception.WrongPasswordException;
import com.dku.council.domain.user.model.UserStatus;
import com.dku.council.domain.user.model.dto.request.RequestExistPasswordChangeDto;
import com.dku.council.domain.user.model.dto.request.RequestLoginDto;
import com.dku.council.domain.user.model.dto.request.RequestNickNameChangeDto;
import com.dku.council.domain.user.model.dto.response.ResponseLoginDto;
import com.dku.council.domain.user.model.dto.response.ResponseMajorDto;
import com.dku.council.domain.user.model.dto.response.ResponseRefreshTokenDto;
import com.dku.council.domain.user.model.dto.response.ResponseUserInfoDto;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtProvider;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final MajorRepository majorRepository;
    private final UserRepository userRepository;
    private final UserInfoCacheService userInfoCacheService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeService likeService;


    public ResponseLoginDto login(RequestLoginDto dto) {
        User user = userRepository.findByStudentId(dto.getStudentId())
                .orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            AuthenticationToken token = jwtProvider.issue(user);
            userInfoCacheService.cacheUserInfo(user.getId(), user);
            return new ResponseLoginDto(token);
        } else {
            throw new WrongPasswordException();
        }
    }

    public ResponseRefreshTokenDto refreshToken(HttpServletRequest request, String refreshToken) {
        String accessToken = jwtProvider.getAccessTokenFromHeader(request);
        AuthenticationToken token = jwtProvider.reissue(accessToken, refreshToken);
        return new ResponseRefreshTokenDto(token);
    }

    @Transactional
    public ResponseUserInfoDto getUserInfo(Long userId) {
        User user = findUser(userId);

        String year = user.getYearOfAdmission().toString();
        Major major = user.getMajor();
        String phoneNumber = user.getPhone();

        Long writePostCount = postRepository.countByUserId(userId);
        Long commentedPostCount = commentRepository.countByUserId(userId);
        Long likedPostCount = likeService.getCountOfLikedElements(userId, LikeTarget.POST);

        return new ResponseUserInfoDto(user.getStudentId(), user.getName(),
                user.getNickname(), year, major.getName(), major.getDepartment(),
                phoneNumber, writePostCount, commentedPostCount, likedPostCount,
                user.getUserRole().isAdmin());
    }

    public List<ResponseMajorDto> getAllMajors() {
        return majorRepository.findAll().stream()
                .map(ResponseMajorDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void changeNickName(Long userId, RequestNickNameChangeDto dto) {
        User user = findUser(userId);
        checkAlreadyNickname(dto.getNickname());
        user.changeNickName(dto.getNickname());
        userInfoCacheService.invalidateUserInfo(userId);
    }

    @Transactional
    public void changePassword(Long userId, RequestExistPasswordChangeDto dto) {
        User user = findUser(userId);
        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
            user.changePassword(encodedPassword);
            userInfoCacheService.invalidateUserInfo(userId);
        } else {
            throw new WrongPasswordException();
        }
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = findUser(userId);
        user.changeStatus(UserStatus.ACTIVE);
        userInfoCacheService.invalidateUserInfo(userId);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = findUser(userId);
        user.changeStatus(UserStatus.INACTIVE);
        userInfoCacheService.invalidateUserInfo(userId);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public void checkAlreadyNickname(String nickname) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new AlreadyNicknameException();
        }
    }
}
