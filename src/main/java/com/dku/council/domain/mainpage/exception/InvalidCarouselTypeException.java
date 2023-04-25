package com.dku.council.domain.mainpage.exception;

import com.dku.council.global.error.exception.LocalizedMessageException;
import org.springframework.http.HttpStatus;

public class InvalidCarouselTypeException extends LocalizedMessageException {
    public InvalidCarouselTypeException(String filename) {
        super(HttpStatus.BAD_REQUEST, "invalid.carousel-type", filename);
    }
}
