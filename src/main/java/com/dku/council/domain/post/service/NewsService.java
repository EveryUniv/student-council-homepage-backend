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

// TODO Test it
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final FileUploadService fileUploadService;

    public Page<SummarizedNewsDto> list(String keyword, Pageable pageable) {
        Page<News> page;

        if (keyword != null) {
            page = newsRepository.findAll(PostSpec.withTitleOrBody(keyword), pageable);
        } else {
            page = newsRepository.findAll(pageable);
        }

        return page.map(news -> new SummarizedNewsDto(fileUploadService.getBaseURL(), news));
    }

    @Transactional
    public Long create(Long userId, RequestCreateNewsDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        News news = dto.toEntity(user);

        fileUploadService.uploadFiles(dto.getFiles(), "news")
                .forEach((file) -> new PostFile(file).changePost(news));

        News save = newsRepository.save(news);
        return save.getId();
    }

    public ResponseSingleNewsDto findOne(Long postId, String remoteAddress) {
        News news = newsRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        postService.increasePostViews(news, remoteAddress);
        return new ResponseSingleNewsDto(fileUploadService.getBaseURL(), news);
    }

    @Transactional
    public void delete(Long postId) {
        News news = newsRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        List<UploadedFile> uploadedFiles = news.getFiles().stream()
                .map(PostFile::toUploadedFile)
                .collect(Collectors.toList());
        fileUploadService.deletePostFiles(uploadedFiles);
        newsRepository.delete(news);
    }
}
