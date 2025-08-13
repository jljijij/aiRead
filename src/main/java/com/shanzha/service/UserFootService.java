package com.shanzha.service;

import com.shanzha.domain.Comment;
import com.shanzha.domain.UserFoot;
import com.shanzha.domain.enumeration.DocumentTypeEnum;
import com.shanzha.domain.enumeration.NotifyTypeEnum;
import com.shanzha.domain.enumeration.OperateTypeEnum;
import com.shanzha.domain.enumeration.PraiseStatEnum;
import com.shanzha.repository.UserFootRepository;
import com.shanzha.service.dto.*;
import com.shanzha.service.mapper.UserFootMapper;
import com.shanzha.utils.JsonUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing {@link com.shanzha.domain.UserFoot}.
 */
@Service
@Transactional
public class UserFootService {

    private static final Logger LOG = LoggerFactory.getLogger(UserFootService.class);
    public static final String TOPIC_PRAISE = "praise";
    public static final String TAG_PRAISE = "praise";
    private static final int ARTICLE = DocumentTypeEnum.ARTICLE.getCode();
    private final UserFootRepository userFootRepository;

    private final UserFootMapper userFootMapper;
    @Autowired
    RocketMQTemplate rocketMQTemplate;
    public UserFootService(UserFootRepository userFootRepository, UserFootMapper userFootMapper) {
        this.userFootRepository = userFootRepository;
        this.userFootMapper = userFootMapper;
    }

    /**
     * Save a userFoot.
     *
     * @param userFootDTO the entity to save.
     * @return the persisted entity.
     */
    public UserFootDTO save(UserFootDTO userFootDTO) {
        LOG.debug("Request to save UserFoot : {}", userFootDTO);
        UserFoot userFoot = userFootMapper.toEntity(userFootDTO);
        userFoot = userFootRepository.save(userFoot);
        return userFootMapper.toDto(userFoot);
    }

    /**
     * Update a userFoot.
     *
     * @param userFootDTO the entity to save.
     * @return the persisted entity.
     */
    public UserFootDTO update(UserFootDTO userFootDTO) {
        LOG.debug("Request to update UserFoot : {}", userFootDTO);
        UserFoot userFoot = userFootMapper.toEntity(userFootDTO);
        userFoot = userFootRepository.save(userFoot);
        return userFootMapper.toDto(userFoot);
    }

    /**
     * Partially update a userFoot.
     *
     * @param userFootDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UserFootDTO> partialUpdate(UserFootDTO userFootDTO) {
        LOG.debug("Request to partially update UserFoot : {}", userFootDTO);

        return userFootRepository
            .findById(userFootDTO.getId())
            .map(existingUserFoot -> {
                userFootMapper.partialUpdate(existingUserFoot, userFootDTO);

                return existingUserFoot;
            })
            .map(userFootRepository::save)
            .map(userFootMapper::toDto);
    }

    /**
     * Get all the userFoots.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<UserFootDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all UserFoots");
        return userFootRepository.findAll(pageable).map(userFootMapper::toDto);
    }

    /**
     * Get one userFoot by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserFootDTO> findOne(Long id) {
        LOG.debug("Request to get UserFoot : {}", id);
        return userFootRepository.findById(id).map(userFootMapper::toDto);
    }

    /**
     * Delete the userFoot by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UserFoot : {}", id);
        userFootRepository.deleteById(id);
    }

    public void removeCommentFoot(Comment comment, Long articleAuthor, Long parentCommentAuthor) {
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        if (comment.getParentCommentId() != null) {
            // 如果需要展示父评论的子评论数量，authorId 需要传父评论的 userId
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.DELETE_COMMENT);
        }
    }
    public UserFootDTO saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum) {
        UserFoot userFoot = userFootRepository.findByDocumentIdAndDocumentTypeAndUserId(documentId, documentType.getCode(), userId)
            .orElseGet(() -> new UserFoot().documentId(documentId).documentType(documentType.getCode()).userId(userId).documentUserId(authorId));

        boolean changed = setUserFootStat(userFoot, operateTypeEnum);
        if (changed) {
            userFoot.setUpdateTime(new Date().toInstant());
            userFootRepository.save(userFoot);
        }
        return userFootMapper.toDto(userFoot);
    }


    public void favorArticleComment(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum) {
        UserFoot userFoot = userFootRepository.findByDocumentIdAndDocumentTypeAndUserId(documentId, documentType.getCode(), userId)
            .orElseGet(() -> new UserFoot().documentId(documentId).documentType(documentType.getCode()).userId(userId).documentUserId(authorId));

        boolean changed = setUserFootStat(userFoot, operateTypeEnum);
        if (!changed) {
            return;
        }

        userFoot.setUpdateTime(new Date().toInstant());
        userFootRepository.save(userFoot);

        NotifyTypeEnum notifyType = OperateTypeEnum.getNotifyType(operateTypeEnum);
        if (notifyType == null) {
            return;
        }

        String destination = TOPIC_PRAISE + ":" + TAG_PRAISE;
        rocketMQTemplate.syncSend(destination, JsonUtil.toStr(userFoot));
    }

    private boolean setUserFootStat(UserFoot userFoot, OperateTypeEnum operate) {
        return switch (operate) {
            case READ -> {
                userFoot.setReadStat(1);
                yield true;
            }
            case PRAISE, CANCEL_PRAISE ->
                    compareAndUpdate(userFoot::getPraiseStat, userFoot::setPraiseStat, operate.getDbStatCode());
            case COLLECTION, CANCEL_COLLECTION ->
                    compareAndUpdate(userFoot::getCollectionStat, userFoot::setCollectionStat, operate.getDbStatCode());
            case COMMENT, DELETE_COMMENT ->
                    compareAndUpdate(userFoot::getCommentStat, userFoot::setCommentStat, operate.getDbStatCode());
            default -> false;
        };
    }

    private <T> boolean compareAndUpdate(Supplier<T> getter, Consumer<T> setter, T input) {
        if (Objects.equals(getter.get(), input)) return false;
        setter.accept(input);
        return true;
    }

    public void saveCommentFoot(CommentDTO comment, Long articleAuthor, Long parentCommentAuthor) {
        // 保存文章对应的评论足迹
        saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, comment.getArticleId(), articleAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        // 如果是子评论，则找到父评论的记录，然后设置为已评
        if (comment.getParentCommentId() != null && comment.getParentCommentId() != 0) {
            // 如果需要展示父评论的子评论数量，authorId 需要传父评论的 userId
            saveOrUpdateUserFoot(DocumentTypeEnum.COMMENT, comment.getParentCommentId(), parentCommentAuthor, comment.getUserId(), OperateTypeEnum.COMMENT);
        }
    }

    public Page<Long> queryUserReadArticleList(Long userId, PageParam pageParam) {
        return userFootRepository.findReadArticleIdsByUserId(userId, PageParam.toPageableDesc(pageParam,"uf.updateTime"));
    }


    public Page<Long> queryUserCollectionArticleList(Long userId, PageParam pageParam) {
        return userFootRepository.findCollectedArticleIdsByUserId(userId, PageParam.toPageableDesc(pageParam,"uf.updateTime"));
    }

    public Page<SimpleUserInfoDTO> queryArticlePraisedUsers(Long articleId, PageParam pageParam) {
            Pageable pageable = PageParam.toPageableDesc(pageParam, "uf.updateTime");
            return userFootRepository.findPraisedUsers(articleId, DocumentTypeEnum.ARTICLE.getCode(), pageable);
        }


    public UserFootDTO queryUserFoot(Long documentId, Integer type, Long userId) {
        return userFootRepository.findByDocumentIdAndDocumentTypeAndUserId(documentId, type, userId)
            .map(userFootMapper::toDto).orElse(null);
    }
    public UserFootStatisticDTO getFootCount() {
        return userFootRepository.getFootCount();
    }

    public Long queryCommentPraiseCount(Long commentId) {
        return userFootRepository.countByDocumentIdAndDocumentTypeAndPraiseStat(
            commentId,
            DocumentTypeEnum.COMMENT.getCode(),
            PraiseStatEnum.PRAISE.getCode());
    }



    public boolean hasPraised(Long userId, Long articleId) {
        return userFootRepository
            .findByUserIdAndDocumentIdAndDocumentType(userId, articleId, ARTICLE)
            .map(f -> f.getPraiseStat() != null && f.getPraiseStat() == 1)
            .orElse(false);
    }

    public boolean hasCollected(Long userId, Long articleId) {
        return userFootRepository
            .findByUserIdAndDocumentIdAndDocumentType(userId, articleId, ARTICLE)
            .map(f -> f.getCollectionStat() != null && f.getCollectionStat() == 1)
            .orElse(false);
    }

    public List<Long> listPraisedArticleIds(Long userId) {
        return userFootRepository
            .findByUserIdAndPraiseStatAndDocumentType(userId, 1, ARTICLE)
            .stream()
            .map(UserFoot::getDocumentId)
            .collect(Collectors.toList());
    }

    public List<Long> listCollectedArticleIds(Long userId) {
        return userFootRepository
            .findByUserIdAndCollectionStatAndDocumentType(userId, 1, ARTICLE)
            .stream()
            .map(UserFoot::getDocumentId)
            .collect(Collectors.toList());
    }

    public NovelUserFootVO getUserArticleFoot(Long userId, Long articleId) {
        Optional<UserFoot> opt = userFootRepository.findByUserIdAndDocumentIdAndDocumentType(userId, articleId, ARTICLE);
        NovelUserFootVO vo = new NovelUserFootVO();
        opt.ifPresent(f -> {
            vo.setPraised(f.getPraiseStat() != null && f.getPraiseStat() == 1);
            vo.setCollected(f.getCollectionStat() != null && f.getCollectionStat() == 1);
        });
        return vo;
    }
}
