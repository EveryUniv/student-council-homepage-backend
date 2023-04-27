package com.dku.council.util.base;

import com.dku.council.util.ResourceUtil;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractMockServerTest {

    private static final Logger log = getLogger(AbstractMockServerTest.class);
    protected static MockWebServer mockServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }


    /**
     * MockWebServer에 text/plain타입의 body 응답을 추가합니다.
     *
     * @param bodyDataFileName body 파일 위치. /test/resources/mockdata/부터 시작합니다.
     */
    public void mockPlain(String bodyDataFileName) {
        enqueueResponse(HttpStatus.OK, bodyDataFileName, MediaType.TEXT_PLAIN);
    }

    /**
     * MockWebServer에 text/plain타입의 body 응답을 추가합니다.
     *
     * @param responseCode     응답 코드
     * @param bodyDataFileName body 파일 위치. /test/resources/mockdata/부터 시작합니다.
     */
    public void mockPlain(HttpStatus responseCode, String bodyDataFileName) {
        enqueueResponse(responseCode, bodyDataFileName, MediaType.TEXT_PLAIN);
    }

    /**
     * MockWebServer에 json타입의 body 응답을 추가합니다.
     *
     * @param bodyDataFileName json body 파일 위치. /test/resources/mockdata/부터 시작합니다. 확장자 .json은 생략합니다.
     */
    public void mockJson(String bodyDataFileName) {
        enqueueResponse(HttpStatus.OK, bodyDataFileName + ".json", MediaType.APPLICATION_JSON);
    }

    /**
     * MockWebServer에 json타입의 body 응답을 추가합니다.
     *
     * @param responseCode     응답 코드
     * @param bodyDataFileName json body 파일 위치. /test/resources/mockdata/부터 시작합니다. 확장자 .json은 생략합니다.
     */
    public void mockJson(HttpStatus responseCode, String bodyDataFileName) {
        enqueueResponse(responseCode, bodyDataFileName + ".json", MediaType.APPLICATION_JSON);
    }

    /**
     * MockWebServer에 html타입의 body 응답을 추가합니다.
     *
     * @param bodyDataFileName html 파일 위치. /test/resources/mockdata/부터 시작합니다. 확장자 .html은 생략합니다.
     */
    public void mockHtml(String bodyDataFileName) {
        enqueueResponse(HttpStatus.OK, bodyDataFileName + ".html", MediaType.TEXT_HTML);
    }

    /**
     * MockWebServer에 xml타입의 body 응답을 추가합니다.
     *
     * @param bodyDataFileName html 파일 위치. /test/resources/mockdata/부터 시작합니다. 확장자 .xml은 생략합니다.
     */
    public void mockXml(String bodyDataFileName) {
        enqueueResponse(HttpStatus.OK, bodyDataFileName + ".xml", MediaType.APPLICATION_XML);
    }

    private void enqueueResponse(HttpStatus responseCode, String bodyDataFileName, MediaType type) {
        String body = readMockData(bodyDataFileName);
        mockServer.enqueue(new MockResponse().setResponseCode(responseCode.value()).setBody(body)
                .addHeader("Content-Type", type.toString()));
    }

    /**
     * MockWebServer에 statusCode만 있는 빈 응답을 추가합니다.
     *
     * @param responseCode 응답 코드
     */
    public void mockWithStatus(HttpStatus responseCode) {
        mockServer.enqueue(new MockResponse().setResponseCode(responseCode.value()));
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
        return ResourceUtil.readResource(name);
    }
}
