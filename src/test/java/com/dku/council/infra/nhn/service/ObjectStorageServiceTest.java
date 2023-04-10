package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.InvalidAccessObjectStorageException;
import com.dku.council.infra.nhn.service.impl.ObjectStorageServiceImpl;
import com.dku.council.util.base.AbstractMockServerTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectStorageServiceTest extends AbstractMockServerTest {

    private ObjectStorageService service;
    private String apiPath;


    @BeforeEach
    public void beforeEach() {
        WebClient webClient = WebClient.create();
        this.apiPath = "http://localhost:" + mockServer.getPort();
        this.service = new ObjectStorageServiceImpl(webClient, apiPath);
    }

    @Test
    @DisplayName("이미있는 파일인 경우 잘 catch하는지")
    public void checkIfIsInStorage() {
        // given
        mockWithStatus(HttpStatus.OK);

        // when
        boolean isIn = service.isInObject("object");

        // then
        assertThat(isIn).isEqualTo(true);
    }

    @Test
    @DisplayName("존재하지않는 파일인 경우 false반환")
    public void checkIfIsNotInStorage() {
        // given
        mockWithStatus(HttpStatus.NOT_FOUND);

        // when
        boolean isIn = service.isInObject("object");

        // then
        assertThat(isIn).isEqualTo(false);
    }

    @Test
    @DisplayName("object url을 잘 생성하는지?")
    public void getObjectUrl() {
        // when
        String url = service.getObjectURL("object");

        // then
        assertThat(url).isEqualTo(String.format(apiPath, "object"));
    }

    @Test
    @DisplayName("성공 응답 - Upload")
    public void uploadingSuccessResponse() {
        // given
        mockWithStatus(HttpStatus.NOT_FOUND); // storage에 없는 파일이어야 함
        mockWithStatus(HttpStatus.OK);

        // when & then(no error)
        service.uploadObject("token", "object", InputStream.nullInputStream(), "image/png");
    }

    @Test
    @DisplayName("성공 응답 - Delete")
    public void deletionSuccessResponse() {
        // given
        mockWithStatus(HttpStatus.OK);

        // when & then(no error)
        service.deleteObject("token", "object");
    }

    @Test
    @DisplayName("실패 응답 - Upload 실패 status code")
    public void failedUploadByFailedStatusCode() {
        // given
        mockWithStatus(HttpStatus.NOT_FOUND); // storage에 없는 파일이어야 함
        mockWithStatus(HttpStatus.BAD_REQUEST);

        // when & then(no error)
        Assertions.assertThrows(InvalidAccessObjectStorageException.class, () ->
                service.uploadObject("token", "object", InputStream.nullInputStream(), "image/png"));
    }

    @Test
    @DisplayName("실패 응답 - Delete 실패 status code")
    public void failedDeleteByFailedStatusCode() {
        // given
        mockWithStatus(HttpStatus.BAD_REQUEST);

        // when & then(no error)
        Assertions.assertThrows(InvalidAccessObjectStorageException.class, () ->
                service.deleteObject("token", "object"));
    }
}