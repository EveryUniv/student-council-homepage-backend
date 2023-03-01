package com.dku.council.domain.post.config;

import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.entity.posttype.Conference;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import com.dku.council.domain.post.model.entity.posttype.News;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import com.dku.council.domain.post.repository.ConferenceRepository;
import com.dku.council.domain.post.repository.GeneralForumRepository;
import com.dku.council.domain.post.repository.NewsRepository;
import com.dku.council.domain.post.repository.RuleRepository;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.domain.post.service.ViewCountService;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PostConfig {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ViewCountService viewCountService;
    private final FileUploadService fileUploadService;
    private final MessageSource messageSource;

    @Bean
    public GenericPostService<Conference> conferenceService(ConferenceRepository repository) {
        return new GenericPostService<>(repository, userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
    }

    @Bean
    public GenericPostService<GeneralForum> generalForumService(GeneralForumRepository repository) {
        return new GenericPostService<>(repository, userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
    }

    @Bean
    public GenericPostService<News> newsService(NewsRepository repository) {
        return new GenericPostService<>(repository, userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
    }

    @Bean
    public GenericPostService<Rule> ruleService(RuleRepository repository) {
        return new GenericPostService<>(repository, userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
    }
}
