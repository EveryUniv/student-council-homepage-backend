package com.dku.council.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Base64;

@Disabled
public class PropertiesBase64Encoder {

    @Test
    public void propertiesEncode() {
        try {
            String properties = ResourceUtil.readResource("/application.yml");
            String encoded = Base64.getEncoder().encodeToString(properties.getBytes());
            System.out.println("========================= Encoded Properties String =========================");
            System.out.println(encoded);
        } catch (NullPointerException e){
            System.out.println("Can't find resource yml. Encoding task ignored");
        }
    }
}
