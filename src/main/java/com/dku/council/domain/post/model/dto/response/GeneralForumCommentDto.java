package com.dku.council.domain.post.model.dto.response;

import com.dku.council.domain.comment.model.dto.CommentDto;
import com.dku.council.domain.comment.model.entity.Comment;
import lombok.Getter;

@Getter
public class GeneralForumCommentDto extends CommentDto {

    private final String authorMajor;

    public GeneralForumCommentDto(Comment comment, CommentDto dto, String author, String authorMajor) {
        super(comment, author, dto.getLikes(), dto.isMine(), dto.isLiked());
        this.authorMajor = authorMajor;
    }
}
