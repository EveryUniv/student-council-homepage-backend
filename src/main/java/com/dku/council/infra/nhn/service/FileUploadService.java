package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.model.UploadedFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public interface FileUploadService {
    ArrayList<UploadedFile> uploadFiles(List<MultipartFile> files, String post);

    void deletePostFiles(List<UploadedFile> files);

    String getBaseURL();
}
