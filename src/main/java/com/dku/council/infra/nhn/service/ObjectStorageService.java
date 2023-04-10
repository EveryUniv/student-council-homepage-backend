package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.InvalidAccessObjectStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final WebClient webClient;
    private final ObjectUploadContext uploadContext;


    public boolean isInObject(String objectName) {
        try {
            webClient.get()
                    .uri(uploadContext.getObjectUrl(objectName))
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

    public void uploadObject(String tokenId, String objectName, final InputStream inputStream, @Nullable MediaType contentType) {
        try {
            WebClient.RequestBodySpec spec = webClient.put()
                    .uri(uploadContext.getObjectUrl(objectName))
                    .header("X-Auth-Token", tokenId);

            if (contentType != null) {
                spec = spec.header("Content-Type", contentType.toString());
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
                    .uri(uploadContext.getObjectUrl(objectName))
                    .header("X-Auth-Token", tokenId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Throwable e) {
            throw new InvalidAccessObjectStorageException(e);
        }
    }

}
