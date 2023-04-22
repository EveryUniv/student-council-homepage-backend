package com.dku.council.domain.post.controller;

import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.request.RequestCreateReplyDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.post.GenericPostRepository;
import com.dku.council.domain.statistic.model.entity.PetitionStatistic;
import com.dku.council.domain.statistic.repository.PetitionStatisticRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.model.dto.ResponseSuccessDto;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.PetitionStatisticMock;
import com.dku.council.mock.UserMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.FieldReflector;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.dku.council.util.test.FullIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@FullIntegrationTest
class PetitionControllerTest extends AbstractContainerRedisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private GenericPostRepository<Petition> postRepository;

    @Autowired
    private PetitionStatisticRepository petitionStatisticRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.post.petition.expires}")
    private Duration expiresTime;

    @Value("${app.post.petition.threshold-comment-count}")
    private int thresholdCommentCount;

    private Petition petition;
    private User user;
    private Major major;
    private Major major2;


    @BeforeEach
    void setupUser() {
        major = majorRepository.save(MajorMock.create());
        major2 = majorRepository.save(MajorMock.create("MAJOR", "DEP"));

        user = UserMock.create(11L, major);
        user = userRepository.save(user);

        petition = PetitionMock.create(user, "title", "body");
        petition = postRepository.save(petition);

        UserAuth.withUser(user.getId());
    }


    @Test
    @DisplayName("청원 상태로 리스트 조회")
    void listWithState() throws Exception {
        // given
        Petition target = null;
        for (PetitionStatus status : PetitionStatus.values()) {
            String name = status.name();
            Petition petition = PetitionMock.create(user, "title" + name, "body" + name);
            FieldReflector.inject(Petition.class, petition, "extraStatus", status);
            petition = postRepository.save(petition);
            if (status == PetitionStatus.WAITING) {
                target = petition;
            }
        }

        // when
        String waitingName = PetitionStatus.WAITING.name();
        ResultActions result = mvc.perform(get("/post/petition")
                .param("status", waitingName));

        // then
        String expiresAt = petition.getCreatedAt().plus(expiresTime).toLocalDate().toString();
        result.andExpect(status().isOk())
                .andExpect(jsonPath("content.size()", is(1)))
                .andExpect(jsonPath("content[0].id", is(target.getId().intValue())))
                .andExpect(jsonPath("content[0].title", is("title" + waitingName)))
                .andExpect(jsonPath("content[0].body", is("body" + waitingName)))
                .andExpect(jsonPath("content[0].expiresAt", is(expiresAt)))
                .andExpect(jsonPath("content[0].status", is(PetitionStatus.WAITING.name())))
                .andExpect(jsonPath("content[0].agreeCount", is(0)));

    }

    @Test
    @DisplayName("단건 조회")
    void findOne() throws Exception {
        // when
        ResultActions result = mvc.perform(get("/post/petition/" + petition.getId()));

        // then
        String expiresAt = petition.getCreatedAt().plus(expiresTime).toLocalDate().toString();

        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(petition.getId().intValue())))
                .andExpect(jsonPath("title", is("title")))
                .andExpect(jsonPath("body", is("body")))
                .andExpect(jsonPath("author", is(petition.getDisplayingUsername())))
                .andExpect(jsonPath("mine", is(true)))
                .andExpect(jsonPath("expiresAt", is(expiresAt)))
                .andExpect(jsonPath("status", is(PetitionStatus.ACTIVE.name())))
                .andExpect(jsonPath("statisticList", is(new ArrayList<>())))
                .andExpect(jsonPath("agreeCount", is(0)));
    }

    @Test
    @DisplayName("답변 등록")
    void reply() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        RequestCreateReplyDto dto = new RequestCreateReplyDto("hello good");

        // when
        ResultActions result = mvc.perform(post("/post/petition/reply/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
        assertThat(petition.getAnswer()).isEqualTo("hello good");
        assertThat(petition.getExtraStatus()).isEqualTo(PetitionStatus.ANSWERED);
    }


    @Test
    @DisplayName("동의 버튼 클릭")
    void agreeComment() throws Exception {
        // when
        ResultActions result = mvc.perform(post("/post/petition/agree/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(new ResponseSuccessDto()))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());

        List<PetitionStatistic> statistics = petitionStatisticRepository.findAll();
        assertThat(statistics.size()).isEqualTo(1);
        assertThat(statistics.get(0).getDepartment()).isEqualTo("MyDepartment");
    }

    @Test
    @DisplayName("동의 버튼 클릭 실패 - 이미 동의한 경우")
    void agreeCommentTwice() throws Exception {
        // given
        PetitionStatistic statistic = PetitionStatisticMock.create(user, petition);
        petitionStatisticRepository.save(statistic);

        // when
        ResultActions result = mvc.perform(post("/post/petition/agree/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(new ResponseSuccessDto()))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("답변대기 상태 변화 - 동의 댓글이 k개 이상인 경우")
    void agreeCommentOver150() throws Exception {
        // given
        List<User> users = UserMock.createList(major, thresholdCommentCount);
        users = userRepository.saveAll(users);

        List<PetitionStatistic> comments = PetitionStatisticMock.createList(petition, users, thresholdCommentCount);
        petitionStatisticRepository.saveAll(comments);

        // when
        ResultActions result = mvc.perform(post("/post/petition/agree/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(new ResponseSuccessDto()))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
        assertThat(petition.getExtraStatus()).isEqualTo(PetitionStatus.WAITING);
    }

    @Test
    @DisplayName("통계 응답 확인")
    void agreeCommentOver150AndStatistic() throws Exception {
        // given
        List<User> users1 = UserMock.createList(major, 60);
        List<User> users2 = UserMock.createList(major2, 50);
        List<User> users = Stream.concat(users1.stream(), users2.stream())
                .collect(Collectors.toList());
        users = userRepository.saveAll(users);

        List<PetitionStatistic> agreeComments = PetitionStatisticMock.createList(petition, users, 110);
        petitionStatisticRepository.saveAll(agreeComments);

        // when
        ResultActions result = mvc.perform(get("/post/petition/" + petition.getId()))
                .andDo(print());

        // then
        String expiresAt = petition.getCreatedAt().plus(expiresTime).toLocalDate().toString();

        result.andExpect(status().isOk())
                .andExpect(jsonPath("id", is(petition.getId().intValue())))
                .andExpect(jsonPath("title", is("title")))
                .andExpect(jsonPath("body", is("body")))
                .andExpect(jsonPath("author", is(petition.getDisplayingUsername())))
                .andExpect(jsonPath("mine", is(true)))
                .andExpect(jsonPath("expiresAt", is(expiresAt)))
                .andExpect(jsonPath("status", is(PetitionStatus.ACTIVE.name())))
                .andExpect(jsonPath("agreeCount", is(110)))
                .andExpect(jsonPath("statisticList.size()", is(2)))
                .andExpect(jsonPath("statisticList[0].agreeCount", is(60)))
                .andExpect(jsonPath("statisticList[0].department", is(major.getDepartment())))
                .andExpect(jsonPath("statisticList[1].agreeCount", is(50)))
                .andExpect(jsonPath("statisticList[1].department", is(major2.getDepartment())));

    }


}