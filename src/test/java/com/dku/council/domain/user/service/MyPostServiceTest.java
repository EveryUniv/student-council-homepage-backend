package com.dku.council.domain.user.service;

import com.dku.council.domain.comment.repository.CommentRepository;
import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.LikeService;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.repository.post.PostRepository;
import com.dku.council.mock.NewsMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyPostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private LikeService likeService;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private MyPostService myPostService;


    @Test
    @DisplayName("내가 좋아요한 게시글 목록 조회")
    void listMyLikedPosts() {
        // given
        Pageable pageable = Pageable.unpaged();
        Page<Long> likedIds = new PageImpl<>(List.of(1L, 2L, 3L, 4L));
        Page<Post> dtos = new PageImpl<>(NewsMock.createListDummy("news", 4))
                .map(post -> post);

        when(likeService.getLikedElementIds(1L, pageable, LikeTarget.POST))
                .thenReturn(likedIds);
        when(postRepository.findPageById(likedIds.getContent(), pageable))
                .thenReturn(dtos);
        when(likeService.getCountOfLikes(any(), eq(LikeTarget.POST)))
                .thenReturn(5);

        // when
        Page<SummarizedGenericPostDto> posts = myPostService.listMyLikedPosts(1L, pageable, 100);

        // then
        isSamePost(posts, dtos);
    }

    @Test
    @DisplayName("내가 댓글단 게시글 목록 조회")
    void listMyCommentedPosts() {
        // given
        Page<Post> dtos = new PageImpl<>(NewsMock.createListDummy("news", 4))
                .map(post -> post);

        when(commentRepository.findAllCommentedByUserId(1L, Pageable.unpaged()))
                .thenReturn(dtos);

        // when
        Page<SummarizedGenericPostDto> posts = myPostService.listMyCommentedPosts(1L, Pageable.unpaged(), 100);

        // then
        isSamePost(posts, dtos);
    }

    private void isSamePost(Page<SummarizedGenericPostDto> dtos, Page<Post> posts) {
        assertThat(posts.getTotalElements()).isEqualTo(dtos.getTotalElements());
        for (int i = 0; i < posts.getTotalElements(); i++) {
            SummarizedGenericPostDto dto = dtos.getContent().get(i);
            Post post = posts.getContent().get(i);
            assertThat(dto.getId()).isEqualTo(post.getId());
            assertThat(dto.getTitle()).isEqualTo(post.getTitle());
            assertThat(dto.getBody()).isEqualTo(post.getBody());
            assertThat(dto.getCreatedAt()).isEqualTo(post.getCreatedAt());
            assertThat(dto.getViews()).isEqualTo(post.getViews());
        }
    }
}