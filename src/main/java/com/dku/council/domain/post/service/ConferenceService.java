package com.dku.council.domain.post.service;

import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.entity.posttype.Conference;
import com.dku.council.domain.post.repository.ConferenceRepository;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class ConferenceService extends GenericPostService<Conference> {

    private final ConferenceRepository repository;

    public ConferenceService(UserRepository userRepository,
                             CategoryRepository categoryRepository,
                             ViewCountService viewCountService,
                             FileUploadService fileUploadService,
                             MessageSource messageSource,
                             ConferenceRepository repository) {
        super(userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
        this.repository = repository;
    }

    @Override
    protected GenericPostRepository<Conference> getRepository() {
        return repository;
    }
}
