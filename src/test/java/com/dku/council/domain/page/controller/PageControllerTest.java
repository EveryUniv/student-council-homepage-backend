//package com.dku.council.domain.page.controller;
//
//import com.dku.council.domain.page.model.dto.request.RequestCarouselImageDto;
//import com.dku.council.domain.page.repository.CarouselImageRepository;
//import com.dku.council.domain.user.model.entity.User;
//import com.dku.council.domain.user.repository.UserRepository;
//import com.dku.council.mock.MultipartFileMock;
//import com.dku.council.mock.UserMock;
//import com.dku.council.mock.user.UserAuth;
//import com.dku.council.util.FullIntegrationTest;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import static org.hamcrest.Matchers.containsInAnyOrder;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//@Transactional
//@FullIntegrationTest
//public class PageControllerTest {
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CarouselImageRepository carouselImageRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private User user;
//    private User admin;
//
//    @BeforeEach
//    void setupUser(){
//        user = UserMock.create(0L);
//        user = userRepository.save(user);
//        UserAuth.withUser(user.getId());
//
//        admin = UserMock.create(1L);
//        admin = userRepository.save(admin);
//        UserAuth.withAdmin(admin.getId());
//    }
//
//    @Test
//    @DisplayName("캐러셀 등록하기 - User")
//    void create() throws Exception {
//        //given
//        String title = "test";
//        String ext = ".jpg";
//        MultipartFile file = MultipartFileMock.create(title, ext);
//
//        RequestCarouselImageDto dto = new RequestCarouselImageDto(file, "localhost:8080/test");
//
//        //when
//        ResultActions result = mvc.perform(post("/page/carousel").content(objectMapper.writeValueAsBytes(dto)).contentType(MediaType.APPLICATION_JSON));
//
//    }
//}
