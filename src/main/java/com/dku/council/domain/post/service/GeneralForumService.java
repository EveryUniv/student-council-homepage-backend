package com.dku.council.domain.post.service;

import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.repository.GeneralForumRepository;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class GeneralForumService extends GenericPostService<GeneralForum> {

    private final GeneralForumRepository repository;

    public GeneralForumService(UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               ViewCountService viewCountService,
                               FileUploadService fileUploadService,
                               MessageSource messageSource,
                               GeneralForumRepository repository) {
        super(userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
        this.repository = repository;
    }

    @Override
    protected GenericPostRepository<GeneralForum> getRepository() {
        return repository;
    }
}
