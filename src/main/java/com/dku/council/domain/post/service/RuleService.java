package com.dku.council.domain.post.service;

import com.dku.council.domain.post.model.dto.page.SummarizedRuleDto;
import com.dku.council.domain.post.repository.RuleRepository;
import com.dku.council.domain.user.repository.UserRepository;
import com.dku.council.infra.nhn.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RuleService {
    private final RuleRepository ruleRepository;
    private final UserRepository userRepository;
    private final ViewCountService viewCountService;
    private final FileUploadService fileUploadService;

//    public Page<SummarizedRuleDto> list(String keyword, Pageable pageable){
//
//    }
}
