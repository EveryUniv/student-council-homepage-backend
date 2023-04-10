package com.dku.council.domain.post.service.post;

import com.dku.council.domain.like.service.impl.CachedLikeServiceImpl;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.post.GeneralForumRepository;
import com.dku.council.domain.post.service.DummyPage;
import com.dku.council.domain.post.service.ThumbnailService;
import com.dku.council.domain.post.service.ViewCountService;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.infra.nhn.service.ObjectUploadContext;
import com.dku.council.mock.GeneralForumMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.dku.council.domain.like.model.LikeTarget.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneralForumServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagService tagService;

    @Mock
    private ViewCountService viewCountService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private ObjectUploadContext uploadContext;

    @Mock
    private ThumbnailService thumbnailService;

    @Mock
    private CachedLikeServiceImpl postLikeService;

    @Mock
    private GeneralForumRepository generalForumRepository;

    @InjectMocks
    private GenericPostService<GeneralForum> generalForumService;


    @Test
    @DisplayName("list에 작성자가 잘 들어가는지?")
    public void list() {
        // given
        List<GeneralForum> allForumList = GeneralForumMock.createListDummy("generic-", 20);
        Page<GeneralForum> allGeneralForum = new DummyPage<>(allForumList, 20);


        when(generalForumRepository.findAll((Specification<GeneralForum>) any(), (Pageable) any())).thenReturn(allGeneralForum);
        when(postLikeService.getCountOfLikes(any(), eq(POST))).thenReturn(15);

        // when
        Page<SummarizedGenericPostDto> allPage = generalForumService.list(generalForumRepository, null, Pageable.unpaged(), 500);

        // then
        assertThat(allPage.getTotalElements()).isEqualTo(allForumList.size());
        for (int i = 0; i < allPage.getTotalElements(); i++) {
            SummarizedGenericPostDto dto = allPage.getContent().get(i);
            GeneralForum generalForum = allForumList.get(i);
            assertThat(dto.getId()).isEqualTo(generalForum.getId());
            assertThat(dto.getTitle()).isEqualTo(generalForum.getTitle());
            assertThat(dto.getBody()).isEqualTo(generalForum.getBody());
            assertThat(dto.getLikes()).isEqualTo(15);
            assertThat(dto.getViews()).isEqualTo(generalForum.getViews());
            assertThat(dto.getAuthor()).isEqualTo(generalForum.getUser().getNickname());
        }
    }
}