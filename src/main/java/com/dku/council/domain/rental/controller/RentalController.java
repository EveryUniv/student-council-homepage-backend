package com.dku.council.domain.rental.controller;

import com.dku.council.domain.post.model.dto.response.ResponsePage;
import com.dku.council.domain.rental.model.dto.RentalDto;
import com.dku.council.domain.rental.model.dto.RentalItemDto;
import com.dku.council.domain.rental.model.dto.SummarizedRentalDto;
import com.dku.council.domain.rental.model.dto.request.RequestCreateRentalDto;
import com.dku.council.domain.rental.model.dto.request.RequestRentalItemDto;
import com.dku.council.domain.rental.model.entity.Rental;
import com.dku.council.domain.rental.model.entity.RentalItem;
import com.dku.council.domain.rental.repository.spec.RentalSpec;
import com.dku.council.domain.rental.service.RentalItemService;
import com.dku.council.domain.rental.service.RentalService;
import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import com.dku.council.global.model.dto.ResponseIdDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "물품 대여", description = "학생회 물품 대여 관련 api")
@RestController
@RequestMapping("/rental")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final RentalItemService rentalItemService;

    /**
     * 대여 신청 현황 조회. (Admin)
     * 누가, 언제, 얼마나 대여를 신청했는지 목록을 가져온다.
     *
     * @param keyword 대여 품목 이름, 대여자 이름, 제목, 본문 공통 검색어. 지정하지않으면 모든 대여 현황 조회.
     */
    @GetMapping
//    @AdminOnly
    public ResponsePage<SummarizedRentalDto> list(@RequestParam(required = false) String keyword,
                                                  @ParameterObject Pageable pageable) {
        Specification<Rental> spec = RentalSpec.withTitleOrBody(keyword);
        spec = spec.or(RentalSpec.withUsername(keyword));
        spec = spec.or(RentalSpec.withItemName(keyword));
        return rentalService.list(spec, pageable);
    }

    /**
     * 대여 품목 조회
     *
     * @param keyword 대여 품목 이름 검색어. 지정하지않으면 모든 대여 품목 조회.
     */
    @GetMapping("/item")
    public ResponsePage<RentalItemDto> listItem(@RequestParam(required = false) String keyword,
                                                @ParameterObject Pageable pageable) {
        Specification<RentalItem> spec = RentalSpec.withName(keyword);
        return rentalItemService.list(spec, pageable);
    }

    /**
     * 내가 대여한 품목들 조회
     *
     * @param keyword 제목이나 내용에 포함된 검색어. 지정하지않으면 모든 대여 품목 조회.
     */
    @GetMapping("/my")
    @UserOnly
    public ResponsePage<SummarizedRentalDto> myList(AppAuthentication auth,
                                                    @RequestParam(required = false) String keyword,
                                                    @ParameterObject Pageable pageable) {
        Specification<Rental> spec = RentalSpec.withTitleOrBody(keyword);
        spec = spec.or(RentalSpec.withUser(auth.getUserId()));
        spec = spec.or(RentalSpec.withItemName(keyword));
        return rentalService.list(spec, pageable);
    }

    /**
     * 대여 현황 단건 조회.
     * 대여 현황을 더 자세히 조회합니다.
     * 운영자는 모든 대여 현황을 볼 수 있지만, 유저는 본인이 신청한 대여만 볼 수 있습니다.
     *
     * @param id 조회할 대여 현황 id
     */
    @GetMapping("/{id}")
    @UserOnly
    public RentalDto findOneItem(AppAuthentication auth, @PathVariable Long id) {
        return rentalService.findOne(id, auth.getUserId(), auth.isAdmin());
    }

    /**
     * 대여 신청
     */
    @PostMapping
    @UserOnly
    public ResponseIdDto create(AppAuthentication auth, @Valid @RequestBody RequestCreateRentalDto dto) {
        Long id = rentalService.create(auth.getUserId(), dto);
        return new ResponseIdDto(id);
    }

    /**
     * 대여 반납 처리 (Admin)
     * 대여되었던 물품을 반납으로 처리합니다. 대여 현황에서 삭제되고, 물품수가 1 올라갑니다.
     */
    @PostMapping("/return/{id}")
    @AdminOnly
    public ResponseIdDto returnItem(@PathVariable Long id) {
        rentalService.returnItem(id);
        return new ResponseIdDto(id);
    }

    /**
     * 대여 품목 추가 (Admin)
     */
    @PostMapping("/item")
    @AdminOnly
    public ResponseIdDto addItem(@Valid @RequestBody RequestRentalItemDto dto) {
        Long id = rentalItemService.create(dto);
        return new ResponseIdDto(id);
    }

    /**
     * 대여 품목 삭제. (Admin)
     * 주의! 대여 품목 삭제시 연관된 학생들의 대여 현황도 볼 수 없게 됩니다. (DB에는 남긴 합니다)
     *
     * @param id 삭제할 품목 id
     */
    @DeleteMapping("/item/{id}")
    @AdminOnly
    public ResponseIdDto deleteItem(@PathVariable Long id) {
        rentalItemService.delete(id);
        return new ResponseIdDto(id);
    }

    /**
     * 대여 품목 변경 (Admin)
     */
    @PatchMapping("/item/{id}")
    @AdminOnly
    public ResponseIdDto patchItem(@PathVariable Long id, @Valid @RequestBody RequestRentalItemDto dto) {
        rentalItemService.patch(id, dto);
        return new ResponseIdDto(id);
    }
}
