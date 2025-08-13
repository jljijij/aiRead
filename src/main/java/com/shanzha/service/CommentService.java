package com.shanzha.service;

import com.shanzha.domain.Comment;
import com.shanzha.domain.User;
import com.shanzha.domain.UserFoot;
import com.shanzha.domain.enumeration.DocumentTypeEnum;
import com.shanzha.domain.enumeration.PraiseStatEnum;
import com.shanzha.repository.CommentRepository;
import com.shanzha.repository.UserRepository;
import com.shanzha.security.SecurityUtils;
import com.shanzha.service.dto.*;
import com.shanzha.service.mapper.CommentMapper;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.shanzha.domain.Comment}.
 */
@Service
@Transactional
public class CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserFootService userFootService;

    private final UserRepository userRepository;

    public CommentService(
        CommentRepository commentRepository,
        CommentMapper commentMapper,
        UserFootService userFootService,
        UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userFootService = userFootService;
        this.userRepository = userRepository;
    }

    /**
     * Save a comment.
     *
     * @param commentDTO the entity to save.
     * @return the persisted entity.
     */
    public CommentDTO save(CommentDTO commentDTO) {
        LOG.debug("Request to save Comment : {}", commentDTO);
        Comment comment = commentMapper.toEntity(commentDTO);
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    /**
     * Update a comment.
     *
     * @param commentDTO the entity to save.
     * @return the persisted entity.
     */
    public CommentDTO update(CommentDTO commentDTO) {
        LOG.debug("Request to update Comment : {}", commentDTO);
        Comment comment = commentMapper.toEntity(commentDTO);
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    /**
     * Partially update a comment.
     *
     * @param commentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CommentDTO> partialUpdate(CommentDTO commentDTO) {
        LOG.debug("Request to partially update Comment : {}", commentDTO);

        return commentRepository
            .findById(commentDTO.getId())
            .map(existingComment -> {
                commentMapper.partialUpdate(existingComment, commentDTO);

                return existingComment;
            })
            .map(commentRepository::save)
            .map(commentMapper::toDto);
    }

    /**
     * Get all the comments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CommentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Comments");
        return commentRepository.findAll(pageable).map(commentMapper::toDto);
    }

    /**
     * Get one comment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CommentDTO> findOne(Long id) {
        LOG.debug("Request to get Comment : {}", id);
        return commentRepository.findById(id).map(commentMapper::toDto);
    }

    /**
     * Delete the comment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Comment : {}", id);
        commentRepository.deleteById(id);
    }

    public List<TopCommentDTO> getArticleComments(Long articleId, PageParam page) {
        // 1.查询一级评论
        List<Comment> comments = commentRepository.findTopComments(articleId, PageParam.toPageable(page));
        if (CollectionUtils.isEmpty(comments)) {
            return Collections.emptyList();
        }
        // map 存 commentId -> 评论
        Map<Long, TopCommentDTO> topComments = comments.stream().collect(Collectors.toMap(Comment::getId, CommentMapper::toTopDto));

        // 2.查询非一级评论
        List<Comment> subComments = commentRepository.findByArticleIdAndTopCommentIdInAndDeleted(articleId, topComments.keySet(), 0);

        // 3.构建一级评论的子评论
        buildCommentRelation(subComments, topComments);

        // 4.挑出需要返回的数据，排序，并补齐对应的用户信息，最后排序返回
        List<TopCommentDTO> result = new ArrayList<>();
        comments.forEach(comment -> {
            TopCommentDTO dto = topComments.get(comment.getId());
            fillTopCommentInfo(dto);
            result.add(dto);
        });

        // 返回结果根据时间进行排序
        Collections.sort(result);
        return result;
    }

    /**
     * 构建父子评论关系
     */
    private void buildCommentRelation(List<Comment> subComments, Map<Long, TopCommentDTO> topComments) {
        Map<Long, SubCommentDTO> subCommentMap = subComments.stream().collect(Collectors.toMap(Comment::getId, CommentMapper::toSubDto));
        subComments.forEach(comment -> {
            TopCommentDTO top = topComments.get(comment.getTopCommentId());
            if (top == null) {
                return;
            }
            SubCommentDTO sub = subCommentMap.get(comment.getId());
            top.getChildComments().add(sub);
            if (Objects.equals(comment.getTopCommentId(), comment.getParentCommentId())) {
                return;
            }

            SubCommentDTO parent = subCommentMap.get(comment.getParentCommentId());
            sub.setParentContent(parent == null ? "~~已删除~~" : parent.getCommentContent());
        });
    }

    /**
     * 填充评论对应的信息
     *
     * @param comment
     */
    private void fillTopCommentInfo(TopCommentDTO comment) {
        fillCommentInfo(comment);
        comment.getChildComments().forEach(this::fillCommentInfo);
        Collections.sort(comment.getChildComments());
    }

    /**
     * 填充评论对应的信息，如用户信息，点赞数等
     *
     * @param comment
     */
    private void fillCommentInfo(BaseCommentDTO comment) {
        User userInfoDO = userRepository.findById(comment.getUserId()).orElse(null);
        if (userInfoDO == null) {
            // 如果用户注销，给一个默认的用户
            comment.setUserName("默认用户");
            comment.setUserPhoto("");
            if (comment instanceof TopCommentDTO) {
                ((TopCommentDTO) comment).setCommentCount(0);
            }
        } else {
            comment.setUserName(userInfoDO.getLogin());
            comment.setUserPhoto(userInfoDO.getImageUrl());
            if (comment instanceof TopCommentDTO) {
                ((TopCommentDTO) comment).setCommentCount(((TopCommentDTO) comment).getChildComments().size());
            }
        }

        // 查询点赞数
        Long praiseCount = userFootService.queryCommentPraiseCount(comment.getCommentId());
        comment.setPraiseCount(praiseCount.intValue());

        // 查询当前登录用于是否点赞过
        Long loginUserId = SecurityUtils.getCurrentUserId().orElse(null);
        if (loginUserId != null) {
            // 判断当前用户是否点过赞
            UserFootDTO foot = userFootService.queryUserFoot(comment.getCommentId(), DocumentTypeEnum.COMMENT.getCode(), loginUserId);
            comment.setPraised(foot != null && Objects.equals(foot.getPraiseStat(), PraiseStatEnum.PRAISE.getCode()));
        } else {
            comment.setPraised(false);
        }
    }

    /**
     * 查询回帖最多的评论
     *
     * @param articleId
     * @return
     */

    public TopCommentDTO queryHotComment(Long articleId) {
        Comment comment = commentRepository.findHotTopComment(articleId);
        if (comment == null) {
            return null;
        }

        TopCommentDTO result = CommentMapper.toTopDto(comment);
        // 查询子评论
        List<Comment> subComments = commentRepository.findByArticleIdAndTopCommentIdInAndDeleted(
            articleId,
            Collections.singletonList(comment.getId()),
            0
        );
        List<SubCommentDTO> subs = subComments.stream().map(CommentMapper::toSubDto).collect(Collectors.toList());
        result.setChildComments(subs);

        // 填充评论信息
        fillTopCommentInfo(result);
        return result;
    }

    public int queryCommentCount(Long articleId) {
        return commentRepository.countByArticleIdAndDeleted(articleId, 0);
    }
}
