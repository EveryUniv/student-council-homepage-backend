package com.dku.council.infra.nhn.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObjectUploadContext {

    @Value("${nhn.os.api-path}")
    private final String apiPath;


    public String getObjectUrl(String objectName) {
        return String.format(apiPath, objectName);
    }

    public String makeObjectId(String prefix, String extension) {
        return makeObjName(prefix, UUID.randomUUID() + "." + extension);
    }

    private String makeObjName(String prefix, String id) {
        return prefix + "-" + id;
    }
}
