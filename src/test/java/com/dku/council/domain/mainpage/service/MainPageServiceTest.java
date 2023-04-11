package com.dku.council.domain.mainpage.service;

import com.dku.council.domain.mainpage.exception.CarouselNotFoundException;
import com.dku.council.domain.mainpage.model.dto.request.RequestCarouselImageDto;
import com.dku.council.domain.mainpage.model.dto.response.CarouselImageResponse;
import com.dku.council.domain.mainpage.model.entity.CarouselImage;
import com.dku.council.domain.mainpage.repository.CarouselImageRepository;
import com.dku.council.global.error.exception.IllegalTypeException;
import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import com.dku.council.mock.MultipartFileMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MainPageServiceTest {

    @Mock
    private CarouselImageRepository carouselImageRepository;

    @Mock
    private ObjectUploadContext uploadContext;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private FileUploadService.Context context;

    @InjectMocks
    private MainPageService service;

    @Test
    @DisplayName("확장자 실패 - jpg | jpeg ...")
    void notImage() {
        // given
        MultipartFile file = MultipartFileMock.create("test", "docs");
        RequestCarouselImageDto request = new RequestCarouselImageDto(file, "test/test");

        // when & then
        assertThrows(IllegalTypeException.class, () -> service.addCarouselImage(request));
    }

    @Test
    @DisplayName("확장자 성공 - jpg | jpeg ...")
    void Image() {
        // given
        MultipartFile file = MultipartFileMock.create("test", "jpg");
        RequestCarouselImageDto request = new RequestCarouselImageDto(file, "test/test");
        UploadedFile uploadedFile = new UploadedFile("fileId",
                new FileRequest("", MediaType.IMAGE_JPEG, null));

        when(fileUploadService.newContext()).thenReturn(context);
        when(context.uploadFile(any(), eq("carousel"))).thenReturn(uploadedFile);

        // when
        service.addCarouselImage(request);

        // then
        assertThat(uploadedFile.getFileId()).isEqualTo("fileId");
    }

    @Test
    @DisplayName("이미지 가져오기")
    void get() {
        // given
        MultipartFile file = MultipartFileMock.create("test", "jpg");
        RequestCarouselImageDto request = new RequestCarouselImageDto(file, "test/test");
        UploadedFile uploadedFile = new UploadedFile("fileId",
                new FileRequest("", MediaType.IMAGE_JPEG, null));

        when(fileUploadService.newContext()).thenReturn(context);
        when(context.uploadFile(any(), eq("carousel"))).thenReturn(uploadedFile);
        when(carouselImageRepository.findAll()).thenReturn(List.of(
                CarouselImage.builder()
                        .redirectUrl("test/test")
                        .fileId("1234")
                        .build()
        ));

        // when
        service.addCarouselImage(request);

        // then
        List<CarouselImageResponse> carouselImages = service.getCarouselImages();
        CarouselImageResponse carouselImageResponse = carouselImages.get(0);

        assertThat(carouselImageResponse.getRedirectUrl()).isEqualTo("test/test");
    }

    @Test
    @DisplayName("이미지 삭제")
    void delete() {
        // given
        CarouselImage carouselImage = CarouselImage.builder()
                .redirectUrl("test/test")
                .fileId("1234")
                .build();

        when(carouselImageRepository.findById(carouselImage.getId())).thenReturn(Optional.of(carouselImage));
        when(carouselImageRepository.findById(100L)).thenThrow(CarouselNotFoundException.class);
        when(fileUploadService.newContext()).thenReturn(context);

        // when & then
        assertThrows(CarouselNotFoundException.class, () -> service.deleteCarouselImage(100L));
        service.deleteCarouselImage(carouselImage.getId());
    }

}
