package com.dku.council.mock;

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

public class ServerMock {

    private static final Logger log = getLogger(ServerMock.class);

    /**
     * MockWebServer에 json타입의 body 응답을 추가합니다.
     *
     * @param server           서버
     * @param bodyDataFileName json body 파일 위치. /test/resources/mockdata/부터 시작합니다. 확장자 .json은 생략합니다.
     */
    public static void json(MockWebServer server, String bodyDataFileName) {
        enqueueResponse(server, HttpStatus.OK, bodyDataFileName + ".json");
    }

    /**
     * MockWebServer에 json타입의 body 응답을 추가합니다.
     *
     * @param server           서버
     * @param responseCode     응답 코드
     * @param bodyDataFileName json body 파일 위치. /test/resources/mockdata/부터 시작합니다. 확장자 .json은 생략합니다.
     */
    public static void json(MockWebServer server, HttpStatus responseCode, String bodyDataFileName) {
        enqueueResponse(server, responseCode, bodyDataFileName + ".json");
    }

    /**
     * MockWebServer에 html타입의 body 응답을 추가합니다.
     *
     * @param server           서버
     * @param bodyDataFileName html 파일 위치. /test/resources/mockdata/부터 시작합니다. 확장자 .html은 생략합니다.
     */
    public static void html(MockWebServer server, String bodyDataFileName) {
        enqueueResponse(server, HttpStatus.OK, bodyDataFileName + ".html");
    }

    private static void enqueueResponse(MockWebServer server, HttpStatus responseCode, String bodyDataFileName) {
        String body = readMockData(bodyDataFileName);
        server.enqueue(new MockResponse().setResponseCode(responseCode.value()).setBody(body)
                .addHeader("Content-Type", "application/json"));
    }

    /**
     * MockWebServer에 statusCode만 있는 빈 응답을 추가합니다.
     *
     * @param server       서버
     * @param responseCode 응답 코드
     */
    public static void status(MockWebServer server, HttpStatus responseCode) {
        server.enqueue(new MockResponse().setResponseCode(responseCode.value()));
    }

    /**
     * /test/resources/mockdata/에 존재하는 파일을 읽습니다.
     *
     * @param path 파일 위치. /test/resources/mockdata/부터 시작합니다.
     * @return 파일 내용
     */
    public static String readMockData(String path) {
        String name = "/mockdata/" + path;
        log.debug("Load mocking data: {}", name);
        return readResource(name);
    }

    public static String readResource(String path) {
        try (InputStream inStream = ServerMock.class.getResourceAsStream(path);
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