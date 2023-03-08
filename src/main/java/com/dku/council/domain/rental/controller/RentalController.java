package com.dku.council.domain.rental.controller;

import com.dku.council.global.auth.jwt.AppAuthentication;
import com.dku.council.global.auth.role.AdminOnly;
import com.dku.council.global.auth.role.UserOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "물품 대여", description = "학생회 물품 대여 관련 api")
@RestController
@RequestMapping("/rental")
@RequiredArgsConstructor
public class RentalController {


    /**
     * 대여 신청 현황 조회. (Admin)
     * 누가, 언제, 얼마나 대여를 신청했는지 목록을 가져온다.
     *
     * @param keyword 대여 품목 이름, 대여자 이름, 제목, 본문 공통 검색어. 지정하지않으면 모든 대여 현황 조회.
     */
    @GetMapping
    @AdminOnly
    public void list(@RequestParam(required = false) String keyword,
                     @ParameterObject Pageable pageable) {
    }

    /**
     * 대여 품목 조회
     *
     * @param itemKeyword 대여 품목 이름 검색어. 지정하지않으면 모든 대여 품목 조회.
     */
    @GetMapping("/item")
    public void listItem(@RequestParam(required = false) String itemKeyword,
                         @ParameterObject Pageable pageable) {
    }

    /**
     * 내가 대여한 품목들 조회
     *
     * @param keyword 제목이나 내용에 포함된 검색어. 지정하지않으면 모든 대여 품목 조회.
     */
    @GetMapping("/my")
    @UserOnly
    public void myList(AppAuthentication auth,
                       @RequestParam(required = false) String keyword,
                       @ParameterObject Pageable pageable) {
    }

    /**
     * 대여 신청
     */
    @PostMapping
    @UserOnly
    public void create(AppAuthentication auth) {
    }

    /**
     * 대여 품목 추가 (Admin)
     */
    @PostMapping("/item")
    @AdminOnly
    public void addItem(AppAuthentication auth) {
    }

    /**
     * 대여 품목 삭제. (Admin)
     * 주의! 대여 품목 삭제시 연관된 학생들의 대여 현황도 볼 수 없게 됩니다. (DB에는 남긴 합니다)
     *
     * @param id 삭제할 품목 id
     */
    @DeleteMapping("/item/{id}")
    @AdminOnly
    public void deleteItem(AppAuthentication auth, @PathVariable Long id) {
    }
}
