package com.shanzha.service.mapper;

import com.shanzha.domain.Comment;
import com.shanzha.domain.CommentSaveReq;
import com.shanzha.service.dto.BaseCommentDTO;
import com.shanzha.service.dto.CommentDTO;
import com.shanzha.service.dto.SubCommentDTO;
import com.shanzha.service.dto.TopCommentDTO;
import java.util.ArrayList;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    public static Comment toDo(CommentSaveReq req) {
        if (req == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(req.getCommentId());
        comment.setArticleId(req.getArticleId());
        comment.setUserId(req.getUserId());
        comment.setContent(req.getCommentContent());
        comment.setParentCommentId(req.getParentCommentId() == null ? 0L : req.getParentCommentId());
        comment.setTopCommentId(req.getTopCommentId() == null ? 0L : req.getTopCommentId());
        return comment;
    }

    private static <T extends BaseCommentDTO> void parseDto(Comment comment, T sub) {
        sub.setCommentId(comment.getId());
        sub.setUserId(comment.getUserId());
        sub.setCommentContent(comment.getContent());
        sub.setPraiseCount(0);
    }

    public static TopCommentDTO toTopDto(Comment commentDO) {
        TopCommentDTO dto = new TopCommentDTO();
        parseDto(commentDO, dto);
        dto.setChildComments(new ArrayList<>());
        return dto;
    }

    public static SubCommentDTO toSubDto(Comment comment) {
        SubCommentDTO sub = new SubCommentDTO();
        parseDto(comment, sub);
        return sub;
    }
}
