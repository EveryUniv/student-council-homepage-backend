package com.dku.council.mock;

import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

public class UploadedFileMock {
    public static List<UploadedFile> createList(int totalFiles) {
        List<UploadedFile> files = new ArrayList<>(totalFiles);
        for (int i = 1; i <= totalFiles; i++) {
            files.add(new UploadedFile("file" + i,
                    new FileRequest("myFile" + i + ".txt", MediaType.TEXT_PLAIN, () -> null)));
        }
        return files;
    }
}
