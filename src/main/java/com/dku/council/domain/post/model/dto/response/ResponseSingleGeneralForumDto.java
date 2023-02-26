package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.post.model.dto.PostFileDto;
import com.dku.council.domain.post.model.entity.posttype.GeneralForum;
import lombok.Getter;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ResponseSingleGeneralForumDto {
    private final Long id;
    private final String title;
    private final String body;
    private final String author;
    private final String category;
    private final LocalDateTime createdAt;
    private final List<PostFileDto> files;
    private final List<CommentDto> commentList;
    private final boolean isMine;


    public ResponseSingleGeneralForumDto(MessageSource messageSource, String baseFileUrl, GeneralForum generalForum, Long userId) {
        this.id = generalForum.getId();
        this.title = generalForum.getTitle();
        this.body = generalForum.getBody();
        this.author = generalForum.getUser().getName();
        this.category = generalForum.getCategory().getName();
        this.createdAt = generalForum.getCreatedAt();
        this.files = PostFileDto.listOf(baseFileUrl, generalForum.getFiles());
        this.commentList = CommentDto.listOf(messageSource, generalForum.getComments());
        this.isMine = generalForum.getUser().getId().equals(userId);
    }
}
