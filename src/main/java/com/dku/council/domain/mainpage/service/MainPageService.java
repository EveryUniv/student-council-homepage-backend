package com.dku.council.domain.mainpage.service;

import com.dku.council.domain.mainpage.exception.CarouselNotFoundException;
import com.dku.council.domain.mainpage.exception.InvalidCarouselTypeException;
import com.dku.council.domain.mainpage.model.dto.PetitionSummary;
import com.dku.council.domain.mainpage.model.dto.PostSummary;
import com.dku.council.domain.mainpage.model.dto.request.RequestCarouselImageDto;
import com.dku.council.domain.mainpage.model.dto.response.CarouselImageResponse;
import com.dku.council.domain.mainpage.model.dto.response.MainPageResponseDto;
import com.dku.council.domain.mainpage.model.entity.CarouselImage;
import com.dku.council.domain.mainpage.repository.CarouselImageRepository;
import com.dku.council.domain.post.repository.post.ConferenceRepository;
import com.dku.council.domain.post.repository.post.NewsRepository;
import com.dku.council.domain.post.repository.post.PetitionRepository;
import com.dku.council.infra.nhn.model.FileRequest;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainPageService {

    private final FileUploadService fileUploadService;
    private final ObjectUploadContext uploadContext;

    private final CarouselImageRepository carouselImageRepository;
    private final PetitionRepository petitionRepository;
    private final NewsRepository newsRepository;
    private final ConferenceRepository conferenceRepository;

    /**
     * 캐러셀 이미지 목록을 가져옵니다. 정렬기준 : 최신 등록일
     */
    public List<CarouselImageResponse> getCarouselImages() {
        return carouselImageRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(image -> new CarouselImageResponse(uploadContext, image))
                .collect(Collectors.toList());
    }

    /**
     * 캐러셀 이미지를 등록합니다.
     */
    @Transactional
    public void addCarouselImage(RequestCarouselImageDto dto) {
        MultipartFile file = dto.getImageFile();
        String redirectUrl = dto.getRedirectUrl();
        String originalFilename = file.getOriginalFilename();

        if (!verifyImageExtension(originalFilename)) {
            throw new InvalidCarouselTypeException(originalFilename);
        }

        String fileId = fileUploadService.newContext()
                .uploadFile(new FileRequest(file), "carousel")
                .getFileId();

        CarouselImage carouselImage = CarouselImage.builder()
                .fileId(fileId)
                .redirectUrl(redirectUrl)
                .build();

        carouselImageRepository.save(carouselImage);
    }

    /**
     * 캐러셀 id 로 저장되어 있는 Object 를 삭제합니다.
     */
    @Transactional
    public void deleteCarouselImage(Long carouselId) {
        CarouselImage carouselImage = carouselImageRepository.findById(carouselId)
                .orElseThrow(CarouselNotFoundException::new);

        fileUploadService.newContext().deleteFile(carouselImage.getFileId());
        carouselImageRepository.delete(carouselImage);
    }

    public MainPageResponseDto mainPageInfo() {
        List<PetitionSummary> petitions = petitionRepository.findTopByOrderByCreatedAtDesc(PageRequest.of(0, 5)).stream()
                .map(PetitionSummary::new)
                .collect(Collectors.toList());
        List<PostSummary> news = newsRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(PostSummary::new)
                .collect(Collectors.toList());
        List<PostSummary> conferences = conferenceRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(PostSummary::new)
                .collect(Collectors.toList());

        List<CarouselImageResponse> carouselImages = getCarouselImages();

        return new MainPageResponseDto(carouselImages, news, conferences, petitions);
    }

    private boolean verifyImageExtension(String originName) {
        if (originName == null) return false;

        return originName.endsWith(".jpg") ||
                originName.endsWith(".jpeg") ||
                originName.endsWith(".png") ||
                originName.endsWith(".gif") ||
                originName.endsWith(".svg") ||
                originName.endsWith(".webp");
    }

    @Transactional
    public void changeRedirectUrl(Long carouselId, String redirectUrl) {
        CarouselImage carouselImage = carouselImageRepository.findById(carouselId)
                .orElseThrow(CarouselNotFoundException::new);
        carouselImage.editRedirectUrl(redirectUrl);
    }
}
