package com.dku.council.domain.post.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.dto.page.SummarizedRuleDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateRuleDto;
import com.dku.council.domain.post.model.dto.response.ResponseSingleRuleDto;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.model.entity.posttype.Rule;
import com.dku.council.domain.post.repository.RuleRepository;
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
public class RuleService {
    private final RuleRepository ruleRepository;
    private final UserRepository userRepository;
    private final ViewCountService viewCountService;
    private final FileUploadService fileUploadService;

    /**
     * 게시글 목록으로 조회
     * @param keyword  검색 키워드
     * @param pageable 페이징 size, sort, page
     * @return         페이징 된 총학 회칙 목록
     */
    public Page<SummarizedRuleDto> list(String keyword, Pageable pageable){
        Page<Rule> page = ruleRepository.findAll(PostSpec.keywordCondition(keyword), pageable);
        return page.map(rule -> new SummarizedRuleDto(fileUploadService.getBaseURL(), rule));
    }

    /**
     * 게시글 등록
     * @param userId 등록한 사용자 id
     * @param dto    게시글 dto
     * @return       등록된 게시글 id
     */
    @Transactional
    public Long create(Long userId, RequestCreateRuleDto dto){
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Rule rule = dto.toEntity(user);

        fileUploadService.uploadFiles(dto.getFiles(), "rule")
                .forEach(file -> new PostFile(file).changePost(rule));

        Rule save = ruleRepository.save(rule);
        return save.getId();
    }

    /**
     * 게시글 단건 조회
     * @param postId        조회할 게시글 id
     * @param remoteAddress 요청자 IP Address. 조회수 카운팅에 사용된다.
     * @return              총학 회칙 게시글 정보
     */
    public ResponseSingleRuleDto findOne(Long postId, String remoteAddress) {
        Rule rule = ruleRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        viewCountService.increasePostViews(rule, remoteAddress);
        return new ResponseSingleRuleDto(fileUploadService.getBaseURL(), rule);
    }

    /**
     * 게시글 삭제 (ONLY FOR ADMIN)
     * @param postId  삭제할 게시글 id
     */
    @Transactional
    public void delete(Long postId){
        Rule rule = ruleRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        List<UploadedFile> uploadedFiles = rule.getFiles().stream()
                .map(UploadedFile::of)
                .collect(Collectors.toList());
        fileUploadService.deletePostFiles(uploadedFiles);
        ruleRepository.delete(rule);
    }



}
