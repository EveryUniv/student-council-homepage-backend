package com.dku.council.domain.user.service;

import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dku.council.domain.user.model.UserStatus.INACTIVE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWithdrawService {

    private final UserRepository userRepository;
    private final UserInfoService cacheService;

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.changeStatus(INACTIVE);
        //TODO inactive가 된 시간을 업데이트하고 그 시간을 토대로 threshold를 확인해서
        //TODO defaultUser로 바꿔치기 해야함
        userRepository.save(user);
        cacheService.invalidateUserInfo(userId);
    }
}
