package com.dku.council.domain.post.service;

import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.post.repository.NewsRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class NewsService extends GenericPostService<News> {

    private final NewsRepository repository;

    public NewsService(UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       ViewCountService viewCountService,
                       FileUploadService fileUploadService,
                       MessageSource messageSource,
                       NewsRepository repository) {
        super(userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
        this.repository = repository;
    }

    @Override
    protected GenericPostRepository<News> getRepository() {
        return repository;
    }
}
