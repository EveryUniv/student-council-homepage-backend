package com.dku.council.infra.nhn.service;

import org.springframework.lang.Nullable;

import java.io.InputStream;

public interface ObjectStorageService {
    boolean isInObject(String objectName);

    String getObjectURL(String objectName);

    void uploadObject(String tokenId, String objectName, final InputStream inputStream, @Nullable String contentType);

    void deleteObject(String tokenId, String objectName);
}
