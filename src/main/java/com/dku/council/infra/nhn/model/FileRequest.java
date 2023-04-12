package com.dku.council.infra.nhn.model;

import com.dku.council.infra.nhn.exception.InvalidFileContentTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class FileRequest {
    private final String originalFilename;
    private final MediaType contentType;
    private final InputStreamSupplier inStreamSupplier;

    public FileRequest(MultipartFile file) {
        this.originalFilename = file.getOriginalFilename();
        this.inStreamSupplier = file::getInputStream;

        String fileMimeType = file.getContentType();
        if (fileMimeType == null) {
            this.contentType = MediaType.APPLICATION_OCTET_STREAM;
        } else {
            try {
                this.contentType = MediaType.parseMediaType(file.getContentType());
            } catch (InvalidMediaTypeException e) {
                throw new InvalidFileContentTypeException(e);
            }
        }
    }

    public InputStream getInputStream() throws IOException {
        return inStreamSupplier.get();
    }

    public static List<FileRequest> ofList(List<MultipartFile> files) {
        List<FileRequest> fileRequests = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                fileRequests.add(new FileRequest(file));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return fileRequests;
    }

    @FunctionalInterface
    public interface InputStreamSupplier {
        InputStream get() throws IOException;
    }
}
