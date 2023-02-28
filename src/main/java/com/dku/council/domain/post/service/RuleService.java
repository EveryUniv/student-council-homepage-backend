package com.dku.council.domain.post.service;

import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.post.repository.RuleRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class RuleService extends GenericPostService<Rule> {

    private final RuleRepository repository;

    public RuleService(UserRepository userRepository,
                       CategoryRepository categoryRepository,
                       ViewCountService viewCountService,
                       FileUploadService fileUploadService,
                       MessageSource messageSource,
                       RuleRepository repository) {
        super(userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
        this.repository = repository;
    }

    @Override
    protected GenericPostRepository<Rule> getRepository() {
        return repository;
    }
}
