package com.dku.council.infra.nhn.model;

import com.dku.council.domain.post.model.entity.PostFile;
import lombok.Getter;
import org.springframework.http.MediaType;

@Getter
public class UploadedFile {
    private final String fileId;

    private final String originalName;

    private final MediaType mimeType;

    private final FileRequest file;

    public UploadedFile(String fileId, FileRequest file) {
        this.fileId = fileId;
        this.originalName = file.getOriginalFilename();
        this.mimeType = file.getContentType();
        this.file = file;
    }

    public PostFile toEntity() {
        return new PostFile(fileId, originalName, mimeType.toString());
    }
}
