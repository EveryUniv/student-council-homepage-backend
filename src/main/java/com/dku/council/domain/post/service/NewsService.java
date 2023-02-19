package com.dku.council.domain.post.service;

import com.dku.council.domain.post.dto.page.SummarizedNewsDto;
import com.dku.council.domain.post.dto.request.RequestCreateNewsDto;
import com.dku.council.domain.post.dto.response.ResponseSingleNewsDto;
import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.NewsRepository;
import com.dku.council.domain.post.repository.spec.PostSpec;
import com.dku.council.domain.user.model.entity.User;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final FileUploadService fileUploadService;

    // TODO 이런 정보는 모두 s3 service로 옮길 예정
    @Value("${nhn.os.domain}")
    private final String s3Domain;

    @Value("${nhn.os.storageAccount}")
    private final String storageAccount;

    @Value("${nhn.os.storageName}")
    private final String storageName;


    public Page<SummarizedNewsDto> list(String keyword, Pageable pageable) {
        Page<News> page;

        if (keyword != null) {
            page = newsRepository.findAll(PostSpec.withTitleOrBody(keyword), pageable);
        } else {
            page = newsRepository.findAll(pageable);
        }

        // TODO 이런 정보는 모두 s3 service로 옮길 예정
        String baseFileUrl = s3Domain + storageAccount + "/" + storageName + "/";

        return page.map(news -> new SummarizedNewsDto(baseFileUrl, news));
    }

    @Transactional
    public Long create(Long userId, RequestCreateNewsDto dto) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        News news = dto.toEntity(user);

        ArrayList<PostFile> postFiles = fileUploadService.uploadFiles(dto.getFiles(), "news");
        postFiles.forEach(file -> file.changePost(news));

        News save = newsRepository.save(news);
        return save.getId();
    }

    public ResponseSingleNewsDto findOne(Long postId, String remoteAddress) {
        News news = newsRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        postService.increasePostViews(news, remoteAddress);

        // TODO 이런 정보는 모두 s3 service로 옮길 예정
        String baseFileUrl = s3Domain + storageAccount + "/" + storageName + "/";

        return new ResponseSingleNewsDto(baseFileUrl, news);
    }

    @Transactional
    public void delete(Long postId) {
        News news = newsRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        fileUploadService.deletePostFiles(news.getFiles());
        newsRepository.delete(news);
    }
}
