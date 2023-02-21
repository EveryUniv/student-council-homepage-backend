package com.dku.council.domain.post.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.dto.page.SummarizedNewsDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleNewsDto;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.NewsRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.model.UploadedFile;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final FileUploadService fileUploadService;

    /**
     * 게시글 목록으로 조회
     * @param keyword 검색 키워드
     * @param pageable 페이징 size, sort, page
     * @return 페이징된 총학 소식 목록
     */
    public Page<SummarizedNewsDto> list(String keyword, Pageable pageable) {
        Page<News> page;

        if (keyword != null) {
            page = newsRepository.findAll(PostSpec.withTitleOrBody(keyword), pageable);
        } else {
            page = newsRepository.findAll(pageable);
        }

        return page.map(news -> new SummarizedNewsDto(fileUploadService.getBaseURL(), news));
    }

    /**
     * 게시글 등록
     *
     * @param userId 등록한 사용자 id
     * @param dto 게시글 dto
     */
    @Transactional
    public Long create(Long userId, RequestCreateNewsDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        News news = dto.toEntity(user);

        fileUploadService.uploadFiles(dto.getFiles(), "news")
                .forEach((file) -> new PostFile(file).changePost(news));

        News save = newsRepository.save(news);
        return save.getId();
    }

    /**
     * 게시글 단건 조회
     *
     * @param postId 조회할 게시글 id
     * @param remoteAddress 요청자 IP Address. 조회수 카운팅에 사용된다.
     * @return 총학소식 게시글 정보
     */
    public ResponseSingleNewsDto findOne(Long postId, String remoteAddress) {
        News news = newsRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        postService.increasePostViews(news, remoteAddress);
        return new ResponseSingleNewsDto(fileUploadService.getBaseURL(), news);
    }

    /**
     * 게시글 삭제
     *
     * @param postId 삭제할 게시글 id
     */
    @Transactional
    public void delete(Long postId) {
        News news = newsRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        List<UploadedFile> uploadedFiles = news.getFiles().stream()
                .map(UploadedFile::of)
                .collect(Collectors.toList());
        fileUploadService.deletePostFiles(uploadedFiles);
        newsRepository.delete(news);
    }
}
