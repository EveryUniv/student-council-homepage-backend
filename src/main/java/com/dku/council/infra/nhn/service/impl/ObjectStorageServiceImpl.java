package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.ExternalAPIPath;
import com.dku.council.infra.nhn.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ObjectStorageServiceImpl implements ObjectStorageService {

    @Value("${nhn.os.storage-account}")
    private final String storageAccount;

    @Value("${nhn.os.storage-name}")
    private final String storageName;

    // TODO RestTemplate대신 WebClient로 교체하기.
    private final RestTemplate restTemplate;


    public String getObjectURL(String objectName) {
        return ExternalAPIPath.NHNObjectStorage(storageAccount, storageName, objectName);
    }

    public void uploadObject(String tokenId, String objectName, final InputStream inputStream) {
        // InputStream을 요청 본문에 추가할 수 있도록 RequestCallback 오버라이드
        final RequestCallback requestCallback = request -> {
            request.getHeaders().add("X-Auth-Token", tokenId);
            IOUtils.copy(inputStream, request.getBody());
        };

        HttpMessageConverterExtractor<String> responseExtractor
                = new HttpMessageConverterExtractor<>(String.class, restTemplate.getMessageConverters());

        restTemplate.execute(getObjectURL(objectName), HttpMethod.PUT, requestCallback, responseExtractor);
    }

    public void deleteObject(String tokenId, String objectName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", tokenId);
        HttpEntity<String> requestHttpEntity = new HttpEntity<>(null, headers);

        this.restTemplate.exchange(getObjectURL(objectName), HttpMethod.DELETE, requestHttpEntity, String.class);
    }

}
