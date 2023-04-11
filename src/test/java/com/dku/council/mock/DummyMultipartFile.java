package com.dku.council.mock;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public class DummyMultipartFile implements MultipartFile {

    private final String parameterName;
    private final String originalFileName;
    private final String contentType;

    public DummyMultipartFile(@NotNull String parameterName, String originalFileName, String contentType) {
        this.parameterName = parameterName;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
    }

    @NotNull
    @Override
    public String getName() {
        return parameterName;
    }

    @Override
    public String getOriginalFilename() {
        return originalFileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte @NotNull [] getBytes() {
        return new byte[0];
    }

    @NotNull
    @Override
    public InputStream getInputStream() {
        return InputStream.nullInputStream();
    }

    @Override
    public void transferTo(@NotNull File dest) throws IllegalStateException {
    }
}
