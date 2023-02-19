package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.ExternalAPIPath;
import com.dku.council.infra.nhn.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.InputStream;

// TODO Test it
@Service
@RequiredArgsConstructor
public class ObjectStorageServiceImpl implements ObjectStorageService {

    @Value("${nhn.os.storage-account}")
    private final String storageAccount;

    @Value("${nhn.os.storage-name}")
    private final String storageName;

    private final WebClient webClient;


    public String getObjectURL(String objectName) {
        return ExternalAPIPath.NHNObjectStorage(storageAccount, storageName, objectName);
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
