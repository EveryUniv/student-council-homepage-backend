package com.dku.council.domain.user.repository.impl;

import com.dku.council.domain.user.repository.SignupAuthRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// TODO Test it
@Repository
public class SignupAuthRedisRepository implements SignupAuthRepository {
    @Override
    public void setAuthPayload(String signupToken, String authName, Object data) {
        // TODO Implementation
    }

    @Override
    public <T> Optional<T> getAuthPayload(String signupToken, String authName, Class<T> payloadClass) {
        // TODO Implementation
        return Optional.empty();
    }
}
