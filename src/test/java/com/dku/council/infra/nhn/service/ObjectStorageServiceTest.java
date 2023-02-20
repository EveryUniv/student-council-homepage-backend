package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.InvalidAccessObjectStorageException;
import com.dku.council.infra.nhn.service.impl.ObjectStorageServiceImpl;
import com.dku.council.util.MockServerUtil;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class ObjectStorageServiceTest {

    private static MockWebServer mockServer;
    private ObjectStorageService service;


    @BeforeAll
    static void beforeAll() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        String apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new ObjectStorageServiceImpl(webClient, apiPath);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockServer.shutdown();
    }

    @Test
    @DisplayName("성공 응답 - Upload")
    public void uploadingSuccessResponse() {
        // given
        MockServerUtil.status(mockServer, HttpStatus.OK);

        // when & then(no error)
        service.uploadObject("token", "object", InputStream.nullInputStream());
    }

    @Test
    @DisplayName("성공 응답 - Delete")
    public void deletionSuccessResponse() {
        // given
        MockServerUtil.status(mockServer, HttpStatus.OK);

        // when & then(no error)
        service.deleteObject("token", "object");
    }

    @Test
    @DisplayName("실패 응답 - Upload 실패 status code")
    public void failedUploadByFailedStatusCode() {
        // given
        MockServerUtil.status(mockServer, HttpStatus.BAD_REQUEST);

        // when & then(no error)
        Assertions.assertThrows(InvalidAccessObjectStorageException.class, () ->
                service.uploadObject("token", "object", InputStream.nullInputStream()));
    }

    @Test
    @DisplayName("실패 응답 - Delete 실패 status code")
    public void failedDeleteByFailedStatusCode() {
        // given
        MockServerUtil.status(mockServer, HttpStatus.BAD_REQUEST);

        // when & then(no error)
        Assertions.assertThrows(InvalidAccessObjectStorageException.class, () ->
                service.deleteObject("token", "object"));
    }
}