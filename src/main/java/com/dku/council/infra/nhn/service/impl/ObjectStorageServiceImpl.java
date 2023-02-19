package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.nhn.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;

// TODO Test it
@Service
@RequiredArgsConstructor
public class ObjectStorageServiceImpl implements ObjectStorageService {

    @Value("${nhn.os.api-path}")
    private final String apiPath;

    private final WebClient webClient;


    public String getObjectURL(String objectName) {
        return String.format(apiPath, objectName);
    }

    public void uploadObject(String tokenId, String objectName, final InputStream inputStream) {
        webClient.put()
                .uri(getObjectURL(objectName))
                .header("X-Auth-Token", tokenId)
                .body(BodyInserters.fromResource(new InputStreamResource(inputStream)))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deleteObject(String tokenId, String objectName) {
        webClient.delete()
                .uri(getObjectURL(objectName))
                .header("X-Auth-Token", tokenId)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
