package com.shanzha.service;

import cn.hutool.extra.spring.SpringUtil;
import com.shanzha.domain.*;
import com.shanzha.domain.enumeration.DocumentTypeEnum;
import com.shanzha.domain.enumeration.NotifyTypeEnum;
import com.shanzha.domain.enumeration.PraiseStatEnum;
import com.shanzha.repository.CommentRepository;
import com.shanzha.repository.UserRepository;
import com.shanzha.security.SecurityUtils;
import com.shanzha.service.dto.*;
import com.shanzha.service.mapper.CommentMapper;
import java.util.*;
import java.util.stream.Collectors;

import com.shanzha.utils.NumUtil;
import com.shanzha.web.rest.errors.BusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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

    private final RocketMQTemplate rocketMQTemplate;
    private final UserRepository userRepository;
    private final NovelService novelService;
    public CommentService(
        CommentRepository commentRepository,
        CommentMapper commentMapper,
        UserFootService userFootService,
        RocketMQTemplate rocketMQTemplate, UserRepository userRepository,
        NovelService novelService) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userFootService = userFootService;
        this.rocketMQTemplate = rocketMQTemplate;
        this.userRepository = userRepository;
        this.novelService = novelService;
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
        Long loginUserId = SecurityUtils.getCurrentUserId().get();
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
    @Transactional(rollbackFor = Exception.class)
    public Long saveComment(CommentSaveReq commentSaveReq) {
        // 保存评论
        Comment comment;
        if (NumUtil.nullOrZero(commentSaveReq.getCommentId())) {
            comment = addComment(commentSaveReq);
        } else {
            comment = updateComment(commentSaveReq);
        }
        return comment.getId();
    }

    private Comment addComment(CommentSaveReq commentSaveReq) {
        // 0.获取父评论信息，校验是否存在
        Comment parentComment = getParentCommentUser(commentSaveReq.getParentCommentId());
        Long parentUser = parentComment == null ? null : parentComment.getUserId();

        // 1. 保存评论内容
        Comment comment = CommentMapper.toDo(commentSaveReq);
        Date now = new Date();
        commentRepository.save(comment);

        // 2. 保存足迹信息 : 文章的已评信息 + 评论的已评信息
        Optional<NovelDTO> novelOpt= novelService.findOne(comment.getArticleId());
        novelOpt.ifPresent(article -> userFootService.saveCommentFoot(commentMapper.toDto(comment), article.getAuthorId(), parentUser));

        // 3. 发布添加/回复评论事件
        rocketMQTemplate.syncSend("notify:COMMENT", comment);
        if (NumUtil.upZero(parentUser)) {
            rocketMQTemplate.syncSend("notify:REPLY",comment);
        }
        return comment;
    }

    private Comment updateComment(CommentSaveReq commentSaveReq) {
        // 更新评论
        Comment comment = commentRepository.findById(commentSaveReq.getCommentId()).orElse(null);
        if (comment == null) {
            throw new BusinessException(CommonCodeMsg.COMMENT_NOT_EXIST);
        }
        comment.setContent(commentSaveReq.getCommentContent());
        commentRepository.save(comment);

        return comment;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        Comment commentDO = commentRepository.findById(commentId).orElse(null);
        // 1.校验评论，是否越权，文章是否存在
        if (commentDO == null) {
            throw new BusinessException(CommonCodeMsg.COMMENT_NOT_EXIST);
        }
        if (Objects.equals(commentDO.getUserId(), userId)) {
            throw new BusinessException(CommonCodeMsg.NOT_AUTH_DELETE);
        }
        // 获取文章信息
        NovelDTO article = novelService.findOne(commentDO.getArticleId()).orElse(null);
        if (article == null) {
            throw new BusinessException(CommonCodeMsg.NOVEL_NOT_EXIST);
        }

        // 2.删除评论、足迹
        commentDO.setDeleted(1);
        commentRepository.save(commentDO);
        Comment parentComment = getParentCommentUser(commentDO.getParentCommentId());
        userFootService.removeCommentFoot(commentDO, article.getAuthorId(), parentComment == null ? null : parentComment.getUserId());

        // 3. 发布删除评论事件
        rocketMQTemplate.syncSend("notify:COMMENT", commentDO);
        if (NumUtil.upZero(commentDO.getParentCommentId())) {
            rocketMQTemplate.syncSend("notify:REPLY",commentDO);
        }
    }


    private Comment getParentCommentUser(Long parentCommentId) {
        if (NumUtil.nullOrZero(parentCommentId)) {
            return null;
        }
        Comment parent = commentRepository.findById(parentCommentId).orElse(null);
        if (parent == null) {
            throw new BusinessException(CommonCodeMsg.NOT_AUTH_DELETE);
        }
        return parent;
    }


//    /**
//     * 机器人回复
//     *
//     * @param comment 当前评论内容
//     * @param parent  当前评论的父评论
//     */
//    private void haterBotTrigger(Comment comment, Comment parent) {
//        boolean trigger = false;
//        Long haterBotUserId = haterBot.getBotUser().getUserId();
//        Long topCommentId = 0L;
//        if (parent == null) {
//            // 当前的评论就是顶级评论，根据回复内容是否有触发词来决定是否需要进行触发
//            String tag = "@" + AiBotEnum.HATER_BOT.getNickName();
//            if (comment.getContent().contains(tag)) {
//                comment.setContent(StringUtils.replace(comment.getContent(), tag, ""));
//                trigger = true;
//            }
//            topCommentId = comment.getId();
//        } else {
//            // 回复内容，根据回复的用户是否为机器人，来判定是否需要进行触发
//            if (Objects.equals(haterBotUserId, parent.getUserId())) {
//                trigger = true;
//            }
//            topCommentId = comment.getTopCommentId();
//        }
//
//        // 评论中，@了机器人，那么开启评论对线模式
//        if (trigger) {
//            log.info("评论「{}」 开启了在线互怼模式", comment);
//            // sourceBizId: 主要用于构建聊天对话，以顶级评论 + 用户id作为唯一标识
//            // 避免出现一个顶级评论开启对线，后续的回复中有其他用户参与进来时，因为用户id不同，这样传递给大模型的上下文就不会出现交叉
//            haterBot.trigger(comment.getContent(), "comment:" + topCommentId + "_" + comment.getUserId(), reply -> {
//                aiReply(haterBotUserId, reply, comment);
//            });
//        }
//    }

    private void aiReply(Long aiUserId, String replyContent, Comment parentComment) {
        CommentSaveReq save = new CommentSaveReq();
        save.setArticleId(parentComment.getArticleId());
        save.setCommentContent(replyContent);
        save.setUserId(aiUserId);
        save.setParentCommentId(parentComment.getId());
        save.setTopCommentId(NumUtil.upZero(parentComment.getTopCommentId()) ? parentComment.getTopCommentId() : parentComment.getId());
        saveComment(save);
    }
}
