package com.dku.council.infra.nhn.service;

import java.io.InputStream;

public interface ObjectStorageService {
    boolean isInObject(String objectName);

    String getObjectURL(String objectName);

    void uploadObject(String tokenId, String objectName, final InputStream inputStream);

    void deleteObject(String tokenId, String objectName);
}
