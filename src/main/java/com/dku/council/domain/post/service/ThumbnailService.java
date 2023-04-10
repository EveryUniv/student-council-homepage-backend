package com.dku.council.domain.post.service;

import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final FileUploadService fileUploadService;
    private final ObjectUploadContext uploadContext;

    @Value("${app.post.thumbnail.size}")
    private final int size;


    public void createThumbnails(List<UploadedFile> files) {
        FileUploadService.Context uploadCtx = fileUploadService.newContext();
        for (UploadedFile file : files) {
            try {
                if (file.getMimeType().getType().equalsIgnoreCase("image")) {
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    InputStream fileInStream = file.getFile().getInputStream();
                    Thumbnails.of(fileInStream)
                            .size(size, size) // todo 비율 맞춰 줄이기
                            .toOutputStream(outStream);

                    InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
                    FileRequest req = new FileRequest(file.getOriginalName(), file.getMimeType(), () -> inStream);

                    String thumbnailId = uploadContext.makeThumbnailId(file.getFileId());
                    uploadCtx.uploadFileWithName(req, thumbnailId);

                    fileInStream.close();
                    inStream.close();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
