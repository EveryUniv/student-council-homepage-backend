package com.dku.council.infra;

import com.dku.council.domain.post.model.entity.PostFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

// TODO Dummy Service. Replace it newer
public class FileUploadService {

    public ArrayList<PostFile> uploadFiles(List<MultipartFile> files, String news) {
        return new ArrayList<>();
    }

    public void deletePostFiles(List<PostFile> files) {

    }
}
