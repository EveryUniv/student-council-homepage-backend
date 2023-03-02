package com.dku.council.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
@Disabled
public class CryptedPasswordEncoder {

    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void passwordEncode() {
        String password = "121212";
        String encoded = encoder.encode(password);
        System.out.println(encoded);
    }
}
