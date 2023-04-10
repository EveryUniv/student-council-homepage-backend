package com.dku.council.domain.mainpage.model.dto.response;

import com.dku.council.domain.mainpage.model.entity.CarouselImage;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import lombok.Getter;

@Getter
public class CarouselImageResponse {
    private final Long id;
    private final String url;
    private final String redirectUrl;

    public CarouselImageResponse(ObjectUploadContext context, CarouselImage image) {
        this.id = image.getId();
        this.url = context.getObjectUrl(image.getFileId());
        this.redirectUrl = image.getRedirectUrl();
    }
}
