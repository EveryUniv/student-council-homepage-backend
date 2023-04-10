package com.dku.council.domain.post.service;

import com.dku.council.domain.like.model.LikeTarget;
import com.dku.council.domain.like.service.impl.CachedLikeServiceImpl;
import com.dku.council.domain.post.model.dto.list.SummarizedPetitionDto;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.statistic.PetitionStatistic;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.service.PetitionStatisticService;
import com.dku.council.domain.tag.model.dto.TagDto;
import com.dku.council.domain.tag.service.TagService;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.PetitionStatisticMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dku.council.domain.like.model.LikeTarget.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PetitionServiceTest {
    @Mock
    private GenericPostRepository<Petition> petitionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagService tagService;

    @Mock
    private ViewCountService viewCountService;

    @Mock
    private FileUploadService fileUploadService;

    @Mock
    private CachedLikeServiceImpl postLikeService;

    @Mock
    private PetitionStatisticService petitionStatisticService;

    private PetitionService petitionService;
    private GenericPostService<Petition> postService;


    @BeforeEach
    public void setup() {
        postService = new GenericPostService<>(petitionRepository, userRepository, tagService,
                viewCountService, fileUploadService, postLikeService);
        petitionService = new PetitionService(postService, petitionStatisticService, 150, Duration.ofDays(30));
    }

    @Test
    @DisplayName("list와 mapper가 잘 동작하는지?")
    public void listWithMapper() {
        // given
        List<Petition> allPostList = PetitionMock.createListDummy("generic-", 20);
        Page<Petition> allPost = new DummyPage<>(allPostList, 20);
        Duration expiresTime = Duration.ofDays(5);

        when(petitionRepository.findAll((Specification<Petition>) any(), (Pageable) any())).thenReturn(allPost);
        when(postLikeService.getCountOfLikes(any(), eq(POST))).thenReturn(15);

        // when
        Page<SummarizedPetitionDto> allPage = postService.list(null, Pageable.unpaged(), 500,
                (dto, post) -> new SummarizedPetitionDto(dto, post, expiresTime,10 ));

        // then
        assertThat(allPage.getTotalElements()).isEqualTo(allPostList.size());
        for (int i = 0; i < allPage.getTotalElements(); i++) {
            SummarizedPetitionDto dto = allPage.getContent().get(i);
            Petition post = allPostList.get(i);
            assertThat(dto.getId()).isEqualTo(post.getId());
            assertThat(dto.getTitle()).isEqualTo(post.getTitle());
            assertThat(dto.getBody()).isEqualTo(post.getBody());
            assertThat(dto.getAgreeCount()).isEqualTo(10);
            assertThat(dto.getStatus()).isEqualTo(post.getExtraStatus());
            assertThat(dto.getCreatedAt()).isEqualTo(post.getCreatedAt());
            assertThat(dto.getViews()).isEqualTo(post.getViews());
        }
    }

    @Test
    @DisplayName("Petition mapper 와 함께 단건 조회가 잘 동작하는지?")
    public void findOnePetitionWithMapper() {
        // given
        Petition petition = PetitionMock.createWithDummy();
        List<PetitionStatisticDto> list = PetitionStatisticMock.createList();

        when(petitionRepository.findById(petition.getId())).thenReturn(Optional.of(petition));
        when(postLikeService.isLiked(any(), any(), eq(LikeTarget.POST))).thenReturn(true);
        when(petitionStatisticService.findTop4Department(petition.getId())).thenReturn(list);
        when(petitionStatisticService.count(petition.getId())).thenReturn(list.size());

        //when
        ResponsePetitionDto dto = petitionService.findOnePetition(petition.getId(), 0L, "Addr");

        // then
        assertThat(dto.getId()).isEqualTo(petition.getId());
        assertThat(dto.getViews()).isEqualTo(petition.getViews());
        assertThat(dto.getAnswer()).isEqualTo(petition.getAnswer());
        assertThat(dto.isLiked()).isEqualTo(true);
        assertThat(dto.isMine()).isEqualTo(false);
        assertThat(dto.getExpiresAt()).isEqualTo(petition.getCreatedAt().plusDays(30).toLocalDate());
        assertThat(dto.getStatisticList()).isEqualTo(list);
        assertThat(dto.getAgreeCount()).isEqualTo(list.size());
    }


    @Test
    @DisplayName("Petition mapper 와 함께 단건 조회가 잘 동작하는지? - 기타 필드가 생성되는가?")
    public void findOnePetitionWithMapperAndCreateField() {
        // given
        Petition petition = PetitionMock.createWithDummy();
        List<PetitionStatisticDto> list = PetitionStatisticMock.createList();

        when(petitionRepository.findById(petition.getId())).thenReturn(Optional.of(petition));
        when(postLikeService.isLiked(any(), any(), eq(LikeTarget.POST))).thenReturn(true);
        when(petitionStatisticService.findTop4Department(petition.getId())).thenReturn(list);
        when(petitionStatisticService.count(petition.getId())).thenReturn(15);

        //when
        ResponsePetitionDto dto = petitionService.findOnePetition(petition.getId(), 0L, "Addr");

        // then
        assertThat(dto.getId()).isEqualTo(petition.getId());
        assertThat(dto.getViews()).isEqualTo(petition.getViews());
        assertThat(dto.getAnswer()).isEqualTo(petition.getAnswer());
        assertThat(dto.isLiked()).isEqualTo(true);
        assertThat(dto.isMine()).isEqualTo(false);
        assertThat(dto.getExpiresAt()).isEqualTo(petition.getCreatedAt().plusDays(30).toLocalDate());
        assertThat(dto.getStatisticList()).isEqualTo(list);
        assertThat(dto.getStatisticList().size()).isEqualTo(5);
        assertThat(dto.getStatisticList().stream().map(PetitionStatisticDto::getDepartment).collect(Collectors.toList()).contains("기타"));
        assertThat(dto.getAgreeCount()).isEqualTo(15);
    }

}
