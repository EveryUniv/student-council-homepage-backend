package com.dku.council.domain.comment;

import com.dku.council.domain.comment.model.CommentStatus;
import com.dku.council.domain.comment.model.entity.Comment;
import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.post.GeneralForumRepository;
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

    private User user1, user2;
    private GeneralForum post1, post2, post3, post4;

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

        post3 = GeneralForumMock.create(user1);
        post3 = generalForumRepository.save(post3);

        post4 = GeneralForumMock.create(user1);
        post4.markAsDeleted(true);
        post4 = generalForumRepository.save(post4);

        List<Comment> comments = CommentMock.createList(post1, List.of(user1, user2), 10);
        comments.get(8).updateStatus(CommentStatus.DELETED);
        comments.get(9).updateStatus(CommentStatus.DELETED);
        repository.saveAll(comments);

        List<Comment> comments2 = CommentMock.createList(post2, List.of(user1, user2), 7);
        comments2.get(5).updateStatus(CommentStatus.DELETED);
        comments2.get(6).updateStatus(CommentStatus.DELETED);
        repository.saveAll(comments2);

        List<Comment> comments3 = CommentMock.createList(post3, List.of(user2), 5);
        repository.saveAll(comments3);

        List<Comment> comments4 = CommentMock.createList(post4, List.of(user2), 5);
        repository.saveAll(comments4);
    }

    @Test
    @DisplayName("포스트 아이디로 댓글 조회 테스트")
    void findAllByPostId() {
        // when
        Page<Comment> actual1 = repository.findAllByPostId(post1.getId(), Pageable.unpaged());
        Page<Comment> actual2 = repository.findAllByPostId(post2.getId(), Pageable.unpaged());
        Page<Comment> actual3 = repository.findAllByPostId(post1.getId(), Pageable.ofSize(5));
        Page<Comment> actual4 = repository.findAllByPostId(post1.getId(), PageRequest.of(1, 5));
        Page<Comment> actual5 = repository.findAllByPostId(post4.getId(), Pageable.unpaged());

        // then
        assertThat(actual1.getTotalElements()).isEqualTo(8);
        assertThat(actual2.getTotalElements()).isEqualTo(5);
        assertThat(actual3.getTotalPages()).isEqualTo(2);
        assertThat(actual3.getNumberOfElements()).isEqualTo(5);
        assertThat(actual3.getNumber()).isEqualTo(0);
        assertThat(actual3.getTotalElements()).isEqualTo(8);
        assertThat(actual4.getNumberOfElements()).isEqualTo(3);
        assertThat(actual4.getNumber()).isEqualTo(1);
        assertThat(actual5.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("포스트 아이디와 유저 아이디로 댓글 조회 테스트")
    void findAllByPostIdAndUserId() {
        // when
        List<Comment> actual1 = repository.findAllByPostIdAndUserId(post1.getId(), user1.getId());
        List<Comment> actual2 = repository.findAllByPostIdAndUserId(post1.getId(), user2.getId());
        List<Comment> actual3 = repository.findAllByPostIdAndUserId(post2.getId(), user1.getId());
        List<Comment> actual4 = repository.findAllByPostIdAndUserId(post2.getId(), user2.getId());
        List<Comment> actual5 = repository.findAllByPostIdAndUserId(post4.getId(), user2.getId());

        // then
        assertThat(actual1).hasSize(4);
        assertThat(actual2).hasSize(4);
        assertThat(actual3).hasSize(3);
        assertThat(actual4).hasSize(2);
        assertThat(actual5).hasSize(0);
    }

    @Test
    @DisplayName("댓글 작성한 글들 조회 테스트")
    void findAllCommentedByUserId() {
        // when
        Page<Post> posts1 = repository.findAllCommentedByUserId(user1.getId(), Pageable.unpaged());
        Page<Post> posts2 = repository.findAllCommentedByUserId(user2.getId(), Pageable.unpaged());

        // then
        assertThat(posts1.map(Post::getId)).containsExactlyInAnyOrder(post1.getId(), post2.getId());
        assertThat(posts2.map(Post::getId)).containsExactlyInAnyOrder(post1.getId(), post2.getId(), post3.getId());
    }

    @Test
    @DisplayName("댓글 작성한 글들 개수 조회 테스트")
    void countAllCommentedByUserId() {
        // when
        Long size1 = repository.countAllCommentedByUserId(user1.getId());
        Long size2 = repository.countAllCommentedByUserId(user2.getId());

        // then
        assertThat(size1).isEqualTo(2L);
        assertThat(size2).isEqualTo(3L);
    }
}