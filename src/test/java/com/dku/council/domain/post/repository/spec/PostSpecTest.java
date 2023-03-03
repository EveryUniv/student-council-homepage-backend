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

    private User user;
    private List<News> news1;

    private Tag tag2;
    private List<News> news2;

    private Tag tag3;
    private List<News> news3;

    @BeforeEach
    void setup() {
        user = UserMock.create();
        user = userRepository.save(user);

        news1 = NewsMock.createList("news-1-", user, 5);
        news1 = postRepository.saveAll(news1);

        tag2 = TagMock.create();
        news2 = createPostsWithTag("news-2-", tag2, 6);

        tag3 = TagMock.create();
        news3 = createPostsWithTag("news-3-", tag3, 7);
    }

    private List<News> createPostsWithTag(String prefix, Tag tag, int size) {
        List<News> newsList = NewsMock.createList(prefix, user, size);
        tag = tagRepository.save(tag);
        newsList = postRepository.saveAll(newsList);

        for (News news : newsList) {
            PostTag relation = new PostTag(tag);
            relation.changePost(news);
            postTagRepository.save(relation);
        }

        return newsList;
    }

    @Test
    void findByKeyword() {
        // given
        Specification<News> spec = PostSpec.genericPostCondition("ews-1", null);

        // when
        List<News> all = postRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(5);
    }

    @Test
    void findBySingleTags() {
        // given
        Specification<News> spec = PostSpec.genericPostCondition(null, List.of(tag2.getId()));

        // when
        List<News> all = postRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(6);
    }

    @Test
    void findByMultipleTags() {
        // given
        Specification<News> spec = PostSpec.genericPostCondition(null, List.of(tag2.getId(), tag3.getId()));

        // when
        List<News> all = postRepository.findAll(spec);

        // then
        assertThat(all.size()).isEqualTo(13);
    }
}