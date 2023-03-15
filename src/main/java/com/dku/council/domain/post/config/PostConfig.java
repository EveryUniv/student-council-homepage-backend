package com.dku.council.domain.post.config;

import com.dku.council.domain.like.PostLikeService;
import com.dku.council.domain.post.model.entity.posttype.*;
import com.dku.council.domain.post.repository.*;
import com.dku.council.domain.post.service.GenericPostService;
import com.dku.council.domain.post.service.ViewCountService;
import com.dku.council.domain.tag.service.TagService;
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
    private final TagService tagService;
    private final ViewCountService viewCountService;
    private final FileUploadService fileUploadService;
    private final PostLikeService postLikeService;
    private final MessageSource messageSource;

    // TODO 이거 좀 더 깔끔하게 하고싶다 (자동화)
    @Bean
    public GenericPostService<Conference> conferencePostService(ConferenceRepository repository) {
        return new GenericPostService<>(repository, userRepository, tagService, viewCountService, fileUploadService, postLikeService);
    }

    @Bean
    public GenericPostService<GeneralForum> generalForumPostService(GeneralForumRepository repository) {
        return new GenericPostService<>(repository, userRepository, tagService, viewCountService, fileUploadService, postLikeService);
    }

    @Bean
    public GenericPostService<News> newsPostService(NewsRepository repository) {
        return new GenericPostService<>(repository, userRepository, tagService, viewCountService, fileUploadService, postLikeService);
    }

    @Bean
    public GenericPostService<Rule> rulePostService(RuleRepository repository) {
        return new GenericPostService<>(repository, userRepository, tagService, viewCountService, fileUploadService, postLikeService);
    }

    @Bean
    public GenericPostService<Petition> petitionPostService(PetitionRepository repository) {
        return new GenericPostService<>(repository, userRepository, tagService, viewCountService, fileUploadService, postLikeService);
    }

    @Bean
    public GenericPostService<Voc> vocPostService(VocRepository repository) {
        return new GenericPostService<>(repository, userRepository, tagService, viewCountService, fileUploadService, postLikeService);
    }
}
