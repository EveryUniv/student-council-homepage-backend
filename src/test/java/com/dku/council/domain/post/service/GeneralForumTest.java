package com.dku.council.domain.post.service;

import com.dku.council.domain.category.Category;
import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.exception.CategoryNotFoundException;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.PostStatus;
import com.dku.council.domain.post.model.dto.page.SummarizedGeneralForumDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateGeneralForumDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleGeneralForumDto;
import com.dku.council.domain.post.model.entity.Post;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.GeneralForumRepository;
import com.dku.council.domain.user.model.Major;
import com.dku.council.domain.user.model.UserRole;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.global.error.exception.NotGrantedException;
import com.dku.council.infra.nhn.service.DummyMultipartFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeneralForumTest {

    @Mock
    private GeneralForumRepository generalForumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileUploadService fileUploadService;
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ViewCountService viewCountService;

    @InjectMocks
    private GeneralForumService service;

    @Test
    @DisplayName("list가 잘 동작하는가?")
    public void list() throws NoSuchFieldException, IllegalAccessException {
        // given
        List<GeneralForum> categoryList = generateGeneralForumList("category-", "카테고리", 10);
        List<GeneralForum> generalList = generateGeneralForumList("generic-", "일반게시글", 20);

        Page<GeneralForum> category = new DummyPage<>(categoryList, 10);
        Page<GeneralForum> general = new DummyPage<>(generalList, 20);

        when(generalForumRepository.findAll((Specification<GeneralForum>) any(), (Pageable) any())).thenReturn(category);
        when(generalForumRepository.findAll((Pageable) any())).thenReturn(general);
        when(fileUploadService.getBaseURL()).thenReturn("http://base/");

        //when
        Page<SummarizedGeneralForumDto> categoryPage = service.list("keyword", "카테고리1", Pageable.unpaged());
        Page<SummarizedGeneralForumDto> generalPage = service.list(null, null, Pageable.unpaged());


        //then
        assertThat(categoryPage.getTotalElements()).isEqualTo(categoryList.size());
        assertSummarizedNewsDtoList(categoryPage.getContent(), categoryList);

        assertThat(generalPage.getTotalElements()).isEqualTo(generalList.size());
        assertSummarizedNewsDtoList(generalPage.getContent(), generalList);
    }
    
    @Test
    @DisplayName("create GeneralForum")
    public void create() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = generateUser(100L);
        Category category = generateCategory("카테고리", 3L);
        GeneralForum generalForum = generateGeneralForum(user, 10L, category);

        List<MultipartFile> multipartFiles = generateMultipartFiles();
        RequestCreateGeneralForumDto dto = new RequestCreateGeneralForumDto("title", "body", multipartFiles, 3L);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(generalForumRepository.save(any())).thenReturn(generalForum);
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));

        //when
        Long generalForumId = service.create(2L, dto);

        //then
        assertThat(generalForumId).isEqualTo(10L);

        verify(generalForumRepository).save(argThat(entity -> {
            assertThat(entity.getCategory()).isEqualTo(category);
            return true;
        }));

        verify(fileUploadService).uploadFiles(argThat(fileList ->{
            assertThat(fileList).isEqualTo(multipartFiles);
            return true;
        }), any());

    }

    @Test
    @DisplayName("생성할 때 user없으면 오류")
    public void failedCreateByNotFoundUser(){
        //given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> {
            service.create(2L, new RequestCreateGeneralForumDto("title", "body", List.of(), 10L));
        });
    }
    @Test
    @DisplayName("생성할 때 category 없으면 오류")
    public void failedCreateByNotFoundCategory() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = generateUser(100L);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(categoryRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class, () -> {
            service.create(2L, new RequestCreateGeneralForumDto("title", "body", List.of(), 10L));
        });
    }

    @Test
    @DisplayName("단건 조회가 잘 되는가?")
    public void findOne() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = generateUser(100L);
        Category category = generateCategory("카테고리", 5L);
        GeneralForum generalForum = generateGeneralForum(user, 10L, category);
        when(generalForumRepository.findById(any())).thenReturn(Optional.of(generalForum));

        //when
        ResponseSingleGeneralForumDto dto = service.findOne(10L, "Addr", 100L);

        //then
        assertThat(dto.getCategory()).isEqualTo("카테고리");
        assertThat(dto.isMine()).isEqualTo(true);
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getAuthor()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("단건 조회가 잘 되는가? - 내가 작성한 글이 아님.")
    public void findOneFailed() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = generateUser(100L);
        Category category = generateCategory("카테고리", 5L);
        GeneralForum generalForum = generateGeneralForum(user, 10L, category);
        when(generalForumRepository.findById(any())).thenReturn(Optional.of(generalForum));

        //when
        ResponseSingleGeneralForumDto dto = service.findOne(10L, "Addr", 10L);

        //then
        assertThat(dto.getCategory()).isEqualTo("카테고리");
        assertThat(dto.isMine()).isEqualTo(false);
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getAuthor()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("없는 게시글 단건 조회시 오류")
    public void failedFindOneByNotFound(){
        //given
        when(generalForumRepository.findById(any())).thenReturn(Optional.empty());

        //when&then
        assertThrows(PostNotFoundException.class, () ->{
            service.findOne(1L, "", 2L);
        });
    }

    @Test
    @DisplayName("삭제가 잘 동작하는가 - 내가 쓴 글 삭제")
    public void deleteByUser() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = generateUser(3L);
        Category category = generateCategory("카테고리", 10L);
        GeneralForum generalForum = generateGeneralForum(user, 100L, category);

        when(generalForumRepository.findById(any())).thenReturn(Optional.of(generalForum));

        //when
        service.delete(100L, 3L, user.getUserRole().getRole().contains("ADMIN"));

        //then
        assertThat(generalForum.getStatus()).isEqualTo(PostStatus.DELETED);
    }

    @Test
    @DisplayName("삭제가 잘 동작하는가 - 어드민 권한 삭제")
    public void deleteByAdmin() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = generateAdmin(3L);
        Category category = generateCategory("카테고리", 10L);
        GeneralForum generalForum = generateGeneralForum(user, 100L, category);

        when(generalForumRepository.findById(any())).thenReturn(Optional.of(generalForum));

        //when
        service.delete(100L, 3L, user.getUserRole().getRole().contains("ADMIN"));

        //then
        assertThat(generalForum.getStatus()).isEqualTo(PostStatus.DELETED_BY_ADMIN);
    }

    @Test
    @DisplayName("삭제오류")
    public void deleteFailed() throws NoSuchFieldException, IllegalAccessException {
        //given
        User user = generateUser(3L);
        Category category = generateCategory("카테고리", 10L);
        GeneralForum generalForum = generateGeneralForum(user, 100L, category);

        when(generalForumRepository.findById(any())).thenReturn(Optional.of(generalForum));

        //when & then
        assertThrows(NotGrantedException.class, () -> {
            service.delete(100L, 2L, user.getUserRole().getRole().contains("ADMIN"));
        });

    }




    private User generateUser(Long userId) throws NoSuchFieldException, IllegalAccessException {
        User build = User.builder()
                .classId("11111111")
                .password("pwd")
                .name("name")
                .major(Major.ADMIN)
                .phone("010-1111-2222")
                .role(UserRole.USER)
                .build();
        Field id = User.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(build, userId);
        return build;
    }

    private User generateAdmin(Long userId) throws NoSuchFieldException, IllegalAccessException {
        User build = User.builder()
                .classId("11111111")
                .password("pwd")
                .name("name")
                .major(Major.ADMIN)
                .phone("010-1111-2222")
                .role(UserRole.ADMIN)
                .build();
        Field id = User.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(build, userId);
        return build;
    }

    private GeneralForum generateGeneralForum(User user, Long generalForumId, Category category) throws NoSuchFieldException, IllegalAccessException {
        GeneralForum generalForum = GeneralForum.builder()
                .user(user)
                .title("")
                .body("")
                .category(category)
                .build();
        Field id = Post.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(generalForum, generalForumId);
        return generalForum;
    }

    private List<MultipartFile> generateMultipartFiles() {
        List<MultipartFile> files = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            files.add(new DummyMultipartFile("file", "myFile" + i + ".txt"));
        }
        return files;
    }

    private Category generateCategory(String category, Long generateId) throws NoSuchFieldException, IllegalAccessException {
        Category ret = new Category(category);
        Field id = Category.class.getDeclaredField("id");
        id.setAccessible(true);
        id.set(ret, generateId);
        return ret;
    }


    private List<GeneralForum> generateGeneralForumList(String keyword, String category, int size) throws NoSuchFieldException, IllegalAccessException {
        List<GeneralForum> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            GeneralForum generalForum = GeneralForum.builder()
                    .user(generateUser(1L))
                    .title(keyword + i)
                    .category(generateCategory(category, 100L))
                    .body("")
                    .build();
            result.add(generalForum);
        }

        return result;
    }

    private void assertSummarizedNewsDtoList(List<SummarizedGeneralForumDto> actual, List<GeneralForum> expected) {
        for (int i = 0; i < actual.size(); i++) {
            SummarizedGeneralForumDto dto = actual.get(i);
            GeneralForum generalForum = expected.get(i);
            assertThat(dto.getTitle()).isEqualTo(generalForum.getTitle());
        }
    }

}
