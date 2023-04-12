package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.exception.AlreadyInStorageException;
import com.dku.council.infra.nhn.exception.InvalidAccessObjectStorageException;
import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final NHNAuthService nhnAuthService;
    private final ObjectStorageService s3service;
    private final ObjectUploadContext uploadContext;


    public Context newContext() {
        String token = nhnAuthService.requestToken();
        return new Context(token);
    }

    public class Context {
        private final String token;

        private Context(String token) {
            this.token = token;
        }

        public ArrayList<UploadedFile> uploadFiles(List<FileRequest> files, String prefix) {
            ArrayList<UploadedFile> postFiles = new ArrayList<>();
            for (FileRequest req : files) {
                postFiles.add(uploadFile(req, prefix));
            }
            return postFiles;
        }

        public UploadedFile uploadFile(FileRequest file, String prefix) {
            String originName = file.getOriginalFilename();
            if (originName == null) originName = "";

            String ext = originName.substring(originName.lastIndexOf(".") + 1);
            String fileId;
            do {
                fileId = uploadContext.makeObjectId(prefix, ext);
            } while (s3service.isInObject(fileId));
            return upload(file, fileId);
        }

        public UploadedFile uploadFileWithName(FileRequest file, String objectName) {
            if (s3service.isInObject(objectName)) {
                throw new AlreadyInStorageException();
            }
            return upload(file, objectName);
        }

        private UploadedFile upload(FileRequest file, String objectName) {
            try {
                s3service.uploadObject(token, objectName, file.getInputStream(), file.getContentType());
                return new UploadedFile(objectName, file);
            } catch (Throwable e) {
                throw new InvalidAccessObjectStorageException(e);
            }
        }

        public void deleteFile(String fileId) {
            s3service.deleteObject(token, fileId);
        }
    }
}
