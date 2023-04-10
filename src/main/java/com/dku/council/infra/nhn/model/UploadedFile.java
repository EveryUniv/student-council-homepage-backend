package com.dku.council.infra.nhn.model;

import com.dku.council.domain.post.model.entity.PostFile;
import lombok.Getter;

@Getter
public class UploadedFile {
    private final String fileId;

    private final String originalName;

    private final String mimeType;

    public UploadedFile(String fileId, String originalName, String mimeType) {
        this.fileId = fileId;
        this.originalName = originalName;
        this.mimeType = mimeType;
    }

    public PostFile toEntity() {
        return new PostFile(fileId, originalName, mimeType);
    }
}
