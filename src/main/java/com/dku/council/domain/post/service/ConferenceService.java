package com.dku.council.domain.post.service;

import com.dku.council.domain.post.exception.PostNotFoundException;
import com.dku.council.domain.post.exception.UserNotFoundException;
import com.dku.council.domain.post.model.dto.page.SummarizedConferenceDto;
import com.dku.council.domain.post.model.dto.request.RequestCreateConferenceDto;
import com.dku.council.domain.post.model.entity.PostFile;
import com.dku.council.domain.post.model.entity.posttype.Conference;
import com.dku.council.domain.post.repository.ConferenceRepository;
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
public class ConferenceService {

    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;


    /**
     * 게시글 목록으로 조회
     * @param keyword   검색 키워드
     * @param pageable  페이징 size, sort, page
     * @return          페이징된 회칙 목록
     */
    public Page<SummarizedConferenceDto> list(String keyword, Pageable pageable){
        Page<Conference> page = conferenceRepository.findAll(PostSpec.keywordCondition(keyword), pageable);
        return page.map(conference -> new SummarizedConferenceDto(fileUploadService.getBaseURL(), conference));
    }

    /**
     * 게시글 등록
     * @param userId    등록한 사용자 id
     * @param dto       게시글 dto
     * @return
     */
    @Transactional
    public Long create(Long userId, RequestCreateConferenceDto dto){
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Conference conference = dto.toEntity(user);

        fileUploadService.uploadFiles(dto.getFiles(), "conference")
                .forEach(file -> new PostFile(file).changePost(conference));

        Conference save = conferenceRepository.save(conference);
        return save.getId();
    }

    // conference 는 단건조회 없음.

    /**
     * 게시글 삭제 (ONLY FOR ADMIN)
     * @param postId 삭제할 게시글 id
     */
    @Transactional
    public void delete(Long postId){
        Conference conference = conferenceRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        List<UploadedFile> uploadedFiles = conference.getFiles().stream()
                .map(UploadedFile::of)
                .collect(Collectors.toList());
        fileUploadService.deletePostFiles(uploadedFiles);
        conferenceRepository.delete(conference);
    }
}
