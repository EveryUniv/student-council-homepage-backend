package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.nhn.exception.AlreadyInStorageException;
import com.dku.council.infra.nhn.exception.InvalidAccessObjectStorageException;
import com.dku.council.infra.nhn.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ObjectStorageServiceImpl implements ObjectStorageService {

    private final WebClient webClient;

    @Value("${nhn.os.api-path}")
    private final String apiPath;


    public String getObjectURL(String objectName) {
        return String.format(apiPath, objectName);
    }

    public boolean isInObject(String objectName) {
        try {
            webClient.get()
                    .uri(getObjectURL(objectName))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
        }
        return true;
    }

    public void uploadObject(String tokenId, String objectName, final InputStream inputStream, @Nullable String contentType) {
        if (isInObject(objectName)) {
            throw new AlreadyInStorageException();
        }
        try {
            WebClient.RequestBodySpec spec = webClient.put()
                    .uri(getObjectURL(objectName))
                    .header("X-Auth-Token", tokenId);

            if (contentType != null) {
                spec = spec.header("Content-Type", contentType);
            }

            spec.body(BodyInserters.fromResource(new InputStreamResource(inputStream)))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Throwable e) {
            throw new InvalidAccessObjectStorageException(e);
        }
    }

    public void deleteObject(String tokenId, String objectName) {
        try {
            webClient.delete()
                    .uri(getObjectURL(objectName))
                    .header("X-Auth-Token", tokenId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Throwable e) {
            throw new InvalidAccessObjectStorageException(e);
        }
    }

}
