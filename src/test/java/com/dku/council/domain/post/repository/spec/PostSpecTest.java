package com.dku.council.domain.post.repository.spec;

import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.NewsRepository;
import com.dku.council.domain.tag.model.entity.PostTag;
import com.dku.council.domain.tag.model.entity.Tag;
import com.dku.council.domain.tag.repository.PostTagRepository;
import com.dku.council.domain.tag.repository.TagRepository;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.mock.NewsMock;
import com.dku.council.mock.TagMock;
import com.dku.council.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostSpecTest {

    @Autowired
    private NewsRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostTagRepository postTagRepository;

    private User user1;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setup() {
        user1 = UserMock.create();
        user1 = userRepository.save(user1);

        User user2 = UserMock.create();
        user2 = userRepository.save(user2);

        List<News> news1 = NewsMock.createList("news-1-", user1, 5);
        postRepository.saveAll(news1);

        tag1 = TagMock.create();
        createPostsWithTag("news-2-", tag1, user1, 6);

        tag2 = TagMock.create();
        createPostsWithTag("news-3-", tag2, user2, 7);
    }

    private void createPostsWithTag(String prefix, Tag tag, User user, int size) {
        List<News> newsList = NewsMock.createList(prefix, user, size);
        tag = tagRepository.save(tag);
        newsList = postRepository.saveAll(newsList);

        for (News news : newsList) {
            PostTag relation = new PostTag(tag);
            relation.changePost(news);
            postTagRepository.save(relation);
        }
    }

    @Test
    void findByKeyword() {
        // given
        Specification<News> spec = PostSpec.withTitleOrBody("ews-1");

        // when
        List<News> all = postRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(5);
    }

    @Test
    void findBySingleTags() {
        // given
        Specification<News> spec = PostSpec.withTag(tag1.getId());

        // when
        List<News> all = postRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(6);
    }

    @Test
    void findByMultipleTags() {
        // given
        Specification<News> spec = PostSpec.withTags(List.of(tag1.getId(), tag2.getId()));

        // when
        List<News> all = postRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(13);
    }

    @Test
    void findByAuthor() {
        // given
        Specification<News> spec = PostSpec.withAuthor(user1.getId());

        // when
        List<News> all = postRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(11);
    }
}