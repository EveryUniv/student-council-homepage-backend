package com.dku.council.infra.nhn.service;

import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.impl.NHNAuthServiceImpl;
import com.dku.council.mock.MultipartFileMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}\\b-[0-9a-f]{4}\\b-[0-9a-f]{4}\\b-[0-9a-f]{4}\\b-[0-9a-f]{12}$");

    @Mock
    private NHNAuthServiceImpl authService;

    @Mock
    private ObjectStorageService storageService;

    @Mock
    private ObjectUploadContext uploadContext;

    @InjectMocks
    private FileUploadService service;


    @Test
    @DisplayName("upload 로직 검증")
    public void uploadFiles() {
        // given
        String prefix = "prefix";
        String ext = "txt";
        final int totalFiles = 10;

        List<MultipartFile> files = MultipartFileMock.createList(totalFiles, ext);
        when(authService.requestToken()).thenReturn("token");
        when(storageService.isInObject(any())).thenReturn(false);
        when(uploadContext.makeObjectId(any(), any())).thenReturn("fileId");

        // when
        ArrayList<UploadedFile> uploadedFiles = service.newContext().uploadFiles(FileRequest.ofList(files), prefix);

        // then
        for (int i = 1; i <= totalFiles; i++) {
            UploadedFile file = uploadedFiles.get(i - 1);
            assertThat(file.getFileId()).isEqualTo("fileId");
            assertThat(file.getOriginalName()).isEqualTo("myFile" + i + ".txt");
            assertThat(file.getMimeType()).isEqualTo(MediaType.TEXT_PLAIN);
        }
    }

    @Test
    @DisplayName("upload 로직 검증 - 단일")
    public void uploadFile() {
        //given
        String title = "test";
        String ext = "txt";
        MultipartFile file = MultipartFileMock.create(title, ext);

        when(authService.requestToken()).thenReturn("token");
        when(uploadContext.makeObjectId(any(), any())).thenReturn("fileId");

        //when
        UploadedFile uploadedFile = service.newContext().uploadFile(new FileRequest(file), "test");

        //then
        assertThat(uploadedFile.getOriginalName()).isEqualTo(file.getOriginalFilename());
        assertThat(uploadedFile.getFileId()).isEqualTo("fileId");
    }

    @Test
    @DisplayName("upload시 object storage 호출이 정확한가?")
    public void uploadFilesCallProperly() {
        // given
        final int totalFiles = 10;
        List<MultipartFile> files = MultipartFileMock.createList(totalFiles, "txt");
        when(authService.requestToken()).thenReturn("token");
        when(storageService.isInObject(any())).thenReturn(false);
        when(uploadContext.makeObjectId(any(), any())).thenReturn("fileId");

        // when
        service.newContext().uploadFiles(FileRequest.ofList(files), "prefix");

        // then
        verify(storageService, times(totalFiles)).uploadObject(eq("token"), any(), any(), eq(MediaType.TEXT_PLAIN));
    }

    @Test
    @DisplayName("delete시 object storage 호출이 정확한가?")
    public void deleteFilesCallProperly() {
        // given
        when(authService.requestToken()).thenReturn("token");

        // when
        service.newContext().deleteFile("fileId");

        // then
        verify(storageService).deleteObject("token", "fileId");
    }
}