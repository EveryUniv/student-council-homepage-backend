package com.dku.council.mock;

import com.dku.council.infra.nhn.model.UploadedFile;

import java.util.ArrayList;
import java.util.List;

public class UploadedFileMock {
    public static List<UploadedFile> createList(int totalFiles) {
        List<UploadedFile> files = new ArrayList<>(totalFiles);
        for (int i = 1; i <= totalFiles; i++) {
            files.add(new UploadedFile("file" + i, "myFile" + i + ".txt"));
        }
        return files;
    }
}
