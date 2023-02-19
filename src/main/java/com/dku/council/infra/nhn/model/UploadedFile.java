package com.dku.council.infra.nhn.model;

import lombok.Getter;

@Getter
public class UploadedFile {
    private final String fileId;

    private final String originalName;

    public UploadedFile(String fileId, String originalName) {
        this.fileId = fileId;
        this.originalName = originalName;
    }
}
