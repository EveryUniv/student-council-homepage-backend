package com.dku.council.domain.post.service;

import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
                    PipedInputStream pis = new PipedInputStream();
                    PipedOutputStream pos = new PipedOutputStream(pis);

                    Thumbnailator.createThumbnail(file.getFile().getInputStream(), pos, size, size); // todo 비율 맞춰 줄이기
                    FileRequest req = new FileRequest(file.getOriginalName(), file.getMimeType(), () -> pis);

                    String thumbnailId = uploadContext.makeThumbnailId(file.getFileId());
                    uploadCtx.uploadFileWithName(req, thumbnailId);
                    pis.close();
                    pos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
