package com.dku.council.domain.user.service;

import com.dku.council.domain.user.model.UserInfo;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserInfoMemoryRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository persistenceRepository;
    private final UserInfoMemoryRepository memoryRepository;

    @Transactional(readOnly = true)
    public UserInfo getUserInfo(Long userId) {
        Instant now = Instant.now();
        return memoryRepository.getUserInfo(userId, now)
                .orElseGet(() -> {
                    User user = persistenceRepository.findByIdWithMajor(userId)
                            .orElseThrow(UserNotFoundException::new);
                    UserInfo userInfo = new UserInfo(user);
                    memoryRepository.setUserInfo(userId, userInfo, now);
                    return userInfo;
                });
    }

    public void invalidateUserInfo(Long userId) {
        memoryRepository.removeUserInfo(userId);
    }

    public void cacheUserInfo(Long userId, User user) {
        UserInfo userInfo = new UserInfo(user);
        memoryRepository.setUserInfo(userId, userInfo, Instant.now());
    }
}
