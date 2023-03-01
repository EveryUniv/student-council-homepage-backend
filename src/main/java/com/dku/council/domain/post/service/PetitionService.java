package com.dku.council.domain.post.service;

import com.dku.council.domain.category.repository.CategoryRepository;
import com.dku.council.domain.post.model.PetitionStatus;
import com.dku.council.domain.post.model.dto.response.ResponsePetitionDto;
import com.dku.council.domain.post.model.entity.posttype.Petition;
import com.dku.council.domain.post.repository.GenericPostRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class PetitionService extends GenericPostService<Petition> {

    public PetitionService(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ViewCountService viewCountService,
                           FileUploadService fileUploadService,
                           MessageSource messageSource,
                           GenericPostRepository<Petition> repository) {
        super(repository, userRepository, categoryRepository, viewCountService, fileUploadService, messageSource);
    }

    /**
     * 청원 게시글 단건 조회
     *
     * @param postId        조회할 게시글 id
     * @param remoteAddress 요청자 IP Address. 조회수 카운팅에 사용된다.
     * @return 게시글 정보
     */
    public ResponsePetitionDto findOnePetition(Long postId, Long userId, String remoteAddress) {
        Petition post = viewPost(postId, remoteAddress);
        return new ResponsePetitionDto(messageSource, fileUploadService.getBaseURL(), userId, post);
    }

    /**
     * 운영진 답변 등록
     *
     * @param postId 답변할 게시글 ID
     * @param answer 답변 본문
     */
    public void reply(Long postId, String answer) {
        Petition post = findPost(postId);
        post.replyAnswer(answer);
        post.updatePetitionStatus(PetitionStatus.ANSWERED);
    }
}
