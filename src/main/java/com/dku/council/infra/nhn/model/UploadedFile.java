package com.dku.council.infra.nhn.model;

import com.dku.council.domain.post.model.entity.PostFile;
import lombok.Getter;

@Getter
public class UploadedFile {
    private final String fileId;

    private final String originalName;

    public UploadedFile(String fileId, String originalName) {
        this.fileId = fileId;
        this.originalName = originalName;
    }

    public static UploadedFile of(PostFile entity) {
        return new UploadedFile(entity.getFileId(), entity.getFileName());
    }
}
