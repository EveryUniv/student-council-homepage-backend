package com.dku.council.util;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.slf4j.LoggerFactory.getLogger;

public class MockServerUtil {

    private static final Logger log = getLogger(MockServerUtil.class);

    public static void jsonBody(MockWebServer server, String bodyDataFileName) {
        jsonBody(server, HttpStatus.OK, bodyDataFileName);
    }

    public static void jsonBody(MockWebServer server, HttpStatus responseCode, String bodyDataFileName) {
        String path = String.format("%s.json", bodyDataFileName);
        String body = readMockData(path);
        server.enqueue(new MockResponse().setResponseCode(responseCode.value()).setBody(body)
                .addHeader("Content-Type", "application/json"));
    }

    public static void status(MockWebServer server, HttpStatus responseCode) {
        server.enqueue(new MockResponse().setResponseCode(responseCode.value()));
    }

    public static String readMockData(String path) {
        String name = "/mockdata/" + path;
        log.debug("Load mocking data: {}", name);

        try (InputStream inStream = MockServerUtil.class.getResourceAsStream(name);
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
