package com.dku.council.domain.post.service;

import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final ObjectUploadContext uploadContext;

    @Value("${app.post.thumbnail.size}")
    private final int size;


    public String createThumbnail(FileUploadService.Context uploadCtx, UploadedFile file) {
        if (!file.getMimeType().getType().equalsIgnoreCase("image")) {
            return null;
        }

        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            InputStream fileInStream = file.getFile().getInputStream();
            Thumbnails.of(fileInStream)
                    .size(size, size)
                    .outputFormat("png")
                    .toOutputStream(outStream);

            InputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
            FileRequest req = new FileRequest(file.getOriginalName(), MediaType.IMAGE_PNG, () -> inStream);

            String thumbnailId = uploadContext.makeObjectId("thumb", "png");
            uploadCtx.uploadFileWithName(req, thumbnailId);

            fileInStream.close();
            inStream.close();
            outStream.close();

            return thumbnailId;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
