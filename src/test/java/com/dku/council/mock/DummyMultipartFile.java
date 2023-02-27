package com.dku.council.mock;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public class DummyMultipartFile implements MultipartFile {

    private final String parameterName;
    private final String originalFileName;

    public DummyMultipartFile(@NotNull String parameterName, String originalFileName) {
        this.parameterName = parameterName;
        this.originalFileName = originalFileName;
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
        return "";
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @NotNull
    @Override
    public byte[] getBytes() {
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
