package com.dku.council.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class ResourceUtil {
    public static String readResource(String path) {
        try (InputStream inStream = ResourceUtil.class.getResourceAsStream(path);
             InputStreamReader reader = new InputStreamReader(inStream, StandardCharsets.UTF_8)) {
            return readStringFromReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readStringFromReader(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int len;

        while ((len = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, len);
        }

        return sb.toString();
    }
}
