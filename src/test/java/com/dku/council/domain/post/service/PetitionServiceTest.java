package com.dku.council.domain.post.service;

import com.dku.council.domain.like.service.impl.CachedLikeServiceImpl;
import com.dku.council.domain.post.exception.PostCooltimeException;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.model.dto.list.SummarizedGenericPostDto;
import com.dku.council.domain.post.model.dto.list.SummarizedPetitionDto;
import com.dku.council.domain.post.model.dto.request.RequestCreatePetitionDto;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGenericPostDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.PostTimeMemoryRepository;
import com.dku.council.domain.post.repository.post.GenericPostRepository;
import com.dku.council.domain.post.service.GenericPostService.PostResultMapper;
import com.dku.council.domain.statistic.model.dto.PetitionStatisticDto;
import com.dku.council.domain.statistic.service.PetitionStatisticService;
import com.dku.council.mock.PetitionMock;
import com.dku.council.mock.PetitionStatisticMock;
import com.dku.council.util.ClockUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.dku.council.domain.post.service.PetitionService.PETITION_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetitionServiceTest {

    private final Clock clock = ClockUtil.create();
    private final Duration writeCooltime = Duration.ofDays(1);

    @Mock
    private GenericPostRepository<Petition> petitionRepository;

    @Mock
    private CachedLikeServiceImpl postLikeService;

    @Mock
    private PetitionStatisticService petitionStatisticService;

    @Mock
    private PostTimeMemoryRepository postTimeMemoryRepository;

    @Mock
    private GenericPostService<Petition> postService;

    private PetitionService petitionService;


    @BeforeEach
    public void setup() {
        petitionService = new PetitionService(postService, petitionStatisticService,
                postTimeMemoryRepository, clock, 150, Duration.ofDays(30),
                writeCooltime);
    }

    @Test
    @DisplayName("list와 mapper가 잘 동작하는지?")
    public void listWithMapper() {
        // given
        List<Petition> allPostList = PetitionMock.createListDummy("generic-", 20);
        Page<Petition> allPost = new DummyPage<>(allPostList, 20);
        Duration expiresTime = Duration.ofDays(5);

        // when
        Page<SummarizedPetitionDto> allPage = allPost.map((ent) -> {
            SummarizedGenericPostDto dto = new SummarizedGenericPostDto("", 100, 15, ent);
            return new SummarizedPetitionDto(dto, ent, expiresTime, 10);
        });

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

        when(petitionStatisticService.findTop4Department(petition.getId())).thenReturn(list);
        when(petitionStatisticService.count(petition.getId())).thenReturn(list.size());
        when(postService.findOne(eq(petition.getId()), eq(0L), eq("Addr"), any()))
                .thenAnswer(ino -> {
                    ResponseSingleGenericPostDto dto =
                            new ResponseSingleGenericPostDto("", 0, false, true, petition);
                    PostResultMapper<ResponsePetitionDto, ResponseSingleGenericPostDto, Petition> mapper =
                            ino.getArgument(3);
                    return mapper.map(dto, petition);
                });

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
    @DisplayName("글 작성 - 쿨타임 이전에 작성한 적 없는 경우")
    public void create() {
        // given
        RequestCreatePetitionDto dto = new RequestCreatePetitionDto("title", "body",
                List.of(), List.of());
        Instant now = Instant.now(clock);

        when(postTimeMemoryRepository.isAlreadyContains(PETITION_KEY, 1L, now))
                .thenReturn(false);
        when(postService.create(1L, dto)).thenReturn(5L);

        // when
        Long result = petitionService.create(1L, dto);

        // then
        assertThat(result).isEqualTo(5L);
        verify(postTimeMemoryRepository).put(PETITION_KEY, 1L, writeCooltime, now);
    }

    @Test
    @DisplayName("글 작성 - 이미 작성한 경우")
    public void createAlreadyPostJustNow() {
        // given
        RequestCreatePetitionDto dto = new RequestCreatePetitionDto("title", "body",
                List.of(), List.of());
        Instant now = Instant.now(clock);

        when(postTimeMemoryRepository.isAlreadyContains(PETITION_KEY, 1L, now))
                .thenReturn(true);

        // when
        Assertions.assertThrows(PostCooltimeException.class,
                () -> petitionService.create(1L, dto));
    }

    @Test
    @DisplayName("글 작성 - 생성 도중 exception 발생시 쿨타임 적용 안하기")
    public void noCooltimeCreatingException() {
        // given
        RequestCreatePetitionDto dto = new RequestCreatePetitionDto("title", "body",
                List.of(), List.of());
        Instant now = Instant.now(clock);

        when(postTimeMemoryRepository.isAlreadyContains(PETITION_KEY, 1L, now))
                .thenReturn(false);
        when(postService.create(1L, dto))
                .thenThrow(new PostNotFoundException());

        // when
        try {
            petitionService.create(1L, dto);
        } catch (PostNotFoundException ignored) {
        }
        verify(postTimeMemoryRepository, never()).put(PETITION_KEY, 1L, writeCooltime, now);
    }
}
