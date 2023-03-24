package com.dku.council.domain.comment;

import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.GeneralForumRepository;
import com.dku.council.domain.user.model.entity.Major;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.MajorRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.CommentMock;
import com.dku.council.mock.GeneralForumMock;
import com.dku.council.mock.MajorMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeneralForumRepository generalForumRepository;

    @Autowired
    private CommentRepository repository;

    private User user1;
    private User user2;
    private GeneralForum post1;
    private GeneralForum post2;

    @BeforeEach
    void setUp() {
        Major major = MajorMock.create();
        major = majorRepository.save(major);

        user1 = UserMock.create(major);
        user1 = userRepository.save(user1);

        user2 = UserMock.create(major);
        user2 = userRepository.save(user2);

        post1 = GeneralForumMock.create(user1);
        post1 = generalForumRepository.save(post1);

        post2 = GeneralForumMock.create(user1);
        post2 = generalForumRepository.save(post2);

        List<Comment> comments = CommentMock.createList(post1, List.of(user1, user2), 10);
        comments.get(8).updateStatus(CommentStatus.DELETED);
        comments.get(9).updateStatus(CommentStatus.DELETED);
        repository.saveAll(comments);

        List<Comment> comments2 = CommentMock.createList(post2, List.of(user1, user2), 7);
        comments2.get(5).updateStatus(CommentStatus.DELETED);
        comments2.get(6).updateStatus(CommentStatus.DELETED);
        repository.saveAll(comments2);
    }

    @Test
    @DisplayName("포스트 아이디로 댓글 조회 테스트")
    void findAllByPostId() {
        // when
        Page<Comment> actual1 = repository.findAllByPostId(post1.getId(), Pageable.unpaged());
        Page<Comment> actual2 = repository.findAllByPostId(post2.getId(), Pageable.unpaged());
        Page<Comment> actual3 = repository.findAllByPostId(post1.getId(), Pageable.ofSize(5));
        Page<Comment> actual4 = repository.findAllByPostId(post1.getId(), PageRequest.of(1, 5));

        // then
        assertThat(actual1.getTotalElements()).isEqualTo(8);
        assertThat(actual2.getTotalElements()).isEqualTo(5);
        assertThat(actual3.getTotalPages()).isEqualTo(2);
        assertThat(actual3.getNumberOfElements()).isEqualTo(5);
        assertThat(actual3.getNumber()).isEqualTo(0);
        assertThat(actual3.getTotalElements()).isEqualTo(8);
        assertThat(actual4.getNumberOfElements()).isEqualTo(3);
        assertThat(actual4.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("포스트 아이디와 유저 아이디로 댓글 조회 테스트")
    void findAllByPostIdAndUserId() {
        // when
        List<Comment> actual1 = repository.findAllByPostIdAndUserId(post1.getId(), user1.getId());
        List<Comment> actual2 = repository.findAllByPostIdAndUserId(post1.getId(), user2.getId());
        List<Comment> actual3 = repository.findAllByPostIdAndUserId(post2.getId(), user1.getId());
        List<Comment> actual4 = repository.findAllByPostIdAndUserId(post2.getId(), user2.getId());

        // then
        assertThat(actual1).hasSize(4);
        assertThat(actual2).hasSize(4);
        assertThat(actual3).hasSize(3);
        assertThat(actual4).hasSize(2);
    }
}