package com.dku.council.domain.post.controller;

import com.dku.council.domain.comment.CommentRepository;
import com.dku.council.domain.comment.CommentStatus;
import com.dku.council.domain.comment.model.dto.RequestCreateCommentDto;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.request.RequestCreateReplyDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.dto.ResponseSuccessDto;
import com.dku.council.mock.CommentMock;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.UserMock;
import com.dku.council.mock.user.UserAuth;
import com.dku.council.util.FieldReflector;
import com.dku.council.util.FullIntegrationTest;
import com.dku.council.util.base.AbstractContainerRedisTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private CommentRepository commentRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private GenericPostRepository<Petition> postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.post.petition.expires}")
    private Duration expiresTime;

    private Petition petition;
    private User user;
    private Major major;


    @BeforeEach
    void setupUser() {
        major = majorRepository.save(MajorMock.create());

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
                .andExpect(jsonPath("content[0].status", is(PetitionStatus.WAITING.name())));
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
                .andExpect(jsonPath("status", is(PetitionStatus.ACTIVE.name())));
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
    @Ignore
    @Test
    @DisplayName("동의 댓글 목록")
    void listComment() throws Exception {
        // given
        Comment comment = CommentMock.create(petition, user);
        commentRepository.save(comment);

        // when
        ResultActions result = mvc.perform(get("/post/petition/comment/" + petition.getId()));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("content.size()", is(1)))
                .andExpect(jsonPath("content[0].text", is(comment.getText())));
    }
    @Ignore
    @Test
    @DisplayName("동의 댓글 생성")
    void createComment() throws Exception {
        // given
        RequestCreateCommentDto dto = new RequestCreateCommentDto("this is comment");

        // when
        ResultActions result = mvc.perform(post("/post/petition/comment/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getText()).isEqualTo("this is comment");
        assertThat(comments.get(0).getPost().getId()).isEqualTo(petition.getId());
    }

    @Ignore
    @Test
    @DisplayName("동의 댓글 생성 실패 - 같은글에 2회이상 불가")
    void createCommentTwice() throws Exception {
        // given
        Comment comment = CommentMock.create(petition, user);
        commentRepository.save(comment);
        RequestCreateCommentDto dto = new RequestCreateCommentDto("this is comment");

        // when
        ResultActions result = mvc.perform(post("/post/petition/comment/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Ignore
    @Test
    @DisplayName("동의 댓글 임계치 초과시 답변대기로 변경")
    void createCommentAndStateChanges() throws Exception {
        // given
        List<User> users = UserMock.createList(major, 150);
        users = userRepository.saveAll(users);

        List<Comment> comments = CommentMock.createList(petition, users, 150);
        commentRepository.saveAll(comments);

        RequestCreateCommentDto dto = new RequestCreateCommentDto("this is comment");

        // when
        ResultActions result = mvc.perform(post("/post/petition/comment/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
        assertThat(petition.getExtraStatus()).isEqualTo(PetitionStatus.WAITING);
    }

    @Ignore
    @Test
    @DisplayName("동의 댓글 삭제")
    void deleteComment() throws Exception {
        // given
        UserAuth.withAdmin(user.getId());
        Comment comment = CommentMock.create(petition, user);
        comment = commentRepository.save(comment);

        // when
        ResultActions result = mvc.perform(delete("/post/petition/comment/" + comment.getId()));

        // then
        result.andExpect(status().isOk());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.get(0).getStatus()).isEqualTo(CommentStatus.DELETED_BY_ADMIN);
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

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(1);
        assertThat(comments.get(0).getText()).isEqualTo("동의합니다.");
        assertThat(comments.get(0).getPost().getId()).isEqualTo(petition.getId());
    }

    @Test
    @DisplayName("동의 버튼 클릭 실패 - 이미 동의한 경우")
    void agreeCommentTwice() throws Exception {
        // given
        Comment comment = CommentMock.create(petition, user);
        commentRepository.save(comment);

        // when
        ResultActions result = mvc.perform(post("/post/petition/agree/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(new ResponseSuccessDto()))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("답변대기 상태 변화 - 동의 댓글이 150개 이상인 경우")
    void agreeCommentOver150() throws Exception {
        // given
        List<User> users = UserMock.createList(major, 150);
        users = userRepository.saveAll(users);

        List<Comment> comments = CommentMock.createList(petition, users, 150);
        commentRepository.saveAll(comments);

        // when
        ResultActions result = mvc.perform(post("/post/petition/agree/" + petition.getId())
                .content(objectMapper.writeValueAsBytes(new ResponseSuccessDto()))
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk());
        assertThat(petition.getExtraStatus()).isEqualTo(PetitionStatus.WAITING);
    }


}