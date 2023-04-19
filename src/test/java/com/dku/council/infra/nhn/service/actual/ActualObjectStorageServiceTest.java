package com.dku.council.infra.nhn.service.actual;

import com.dku.council.infra.nhn.service.NHNAuthService;
import com.dku.council.infra.nhn.service.ObjectStorageService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import com.dku.council.util.WebClientUtil;
import com.dku.council.util.YamlProperties;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ActualObjectStorageServiceTest {
    private static YamlProperties properties;
    private ObjectStorageService storageService;
    private NHNAuthService authService;


    @BeforeAll
    static void beforeAll() throws IOException {
        properties = new YamlProperties();
        properties.load();
    }

    @BeforeEach
    public void beforeEach() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        WebClient webClient = WebClient.builder()
                .clientConnector(WebClientUtil.logger())
                .build();

        String defaultThumbnail = properties.get("app.post.thumbnail.default");
        String osApiPath = properties.get("nhn.os.api-path");
        String authApiPath = properties.get("nhn.auth.api-path");
        String tenantId = properties.get("nhn.auth.tenant-id");
        String username = properties.get("nhn.auth.username");
        String password = properties.get("nhn.auth.password");

        ObjectUploadContext uploadContext = new ObjectUploadContext(osApiPath, defaultThumbnail);

        this.storageService = new ObjectStorageService(webClient, uploadContext);
        this.authService = new NHNAuthService(webClient, authApiPath, tenantId, username, password);

        // call @postconstruct manually
        Method init = NHNAuthService.class.getDeclaredMethod("initialize");
        init.setAccessible(true);
        init.invoke(authService);
    }

    @Test
    @Disabled
    @DisplayName("실제로 object를 upload해본다.")
    public void actualUploadObject() {
        String token = authService.requestToken();
        InputStream inStream = ActualObjectStorageServiceTest.class.getResourceAsStream("/dummy/dummy_img1.jpg");
        storageService.uploadObject(token, "TestObject", inStream, MediaType.IMAGE_JPEG);
    }

    @Test
    @Disabled
    @DisplayName("실제로 object를 delete해본다.")
    public void actualDeleteObject() {
        String token = authService.requestToken();
        storageService.deleteObject(token, "TestObject");
    }
}