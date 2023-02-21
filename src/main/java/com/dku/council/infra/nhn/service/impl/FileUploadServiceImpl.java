package com.dku.council.infra.nhn.service.impl;

import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final NHNAuthServiceImpl nhnAuthService;
    private final ObjectStorageServiceImpl s3service;

    public ArrayList<UploadedFile> uploadFiles(List<MultipartFile> files, String prefix) {
        String token = nhnAuthService.requestToken();
        ArrayList<UploadedFile> postFiles = new ArrayList<>();
        files.forEach(file -> {
            String originName = file.getOriginalFilename();
            if (originName == null) {
                originName = "";
            }

            String ext = originName.substring(originName.lastIndexOf(".") + 1);
            String fileId;
            do {
                fileId = prefix + "-" + UUID.randomUUID() + "." + ext;
            } while (s3service.isInObject(fileId));

            try {
                s3service.uploadObject(token, fileId, file.getInputStream());
                postFiles.add(new UploadedFile(fileId, originName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return postFiles;
    }

    public void deletePostFiles(List<UploadedFile> files) {
        String token = nhnAuthService.requestToken();
        for (UploadedFile file : files) {
            s3service.deleteObject(token, file.getFileId());
        }
    }

    @Override
    public String getBaseURL() {
        return s3service.getObjectURL("");
    }
}
