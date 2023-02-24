package com.dku.council.domain.user.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class LoginUserNotFoundException extends LocalizedMessageException {
    public LoginUserNotFoundException() {
        super(HttpStatus.BAD_REQUEST, "notfound.login-user");
    }
}
