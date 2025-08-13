package com.shanzha.web.rest.controller;


import com.shanzha.domain.CommentSaveReq;
import com.shanzha.domain.CommonCodeMsg;
import com.shanzha.domain.Novel;
import com.shanzha.domain.UserFoot;
import com.shanzha.domain.enumeration.DocumentTypeEnum;
import com.shanzha.domain.enumeration.OperateTypeEnum;
import com.shanzha.repository.NovelRepository;
import com.shanzha.security.SecurityUtils;
import com.shanzha.service.*;
import com.shanzha.service.dto.*;
import com.shanzha.service.mapper.NovelMapper;
import com.shanzha.utils.IdGenerator;
import com.shanzha.utils.NumUtil;
import com.shanzha.web.rest.errors.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
@RequestMapping("/api/novels")
@Tag(name = "小说管理", description = "小说CRUD")
public class NovelController {

    @Autowired
    NovelService novelService;

    @Autowired
    UserFootService userFootService;
    @Autowired
    NovelRepository novelRepository;
    @Autowired
    MinioService minioService;
    @Autowired
    UserService userService; // JHipster自带服务，可获取当前登录用户
    @Autowired
    NovelMapper novelMapper;
    @Autowired
    CommentService commentService;
    @Autowired
    ChapterService chapterService;

    // 创建小说（含封面上传）
    @Operation(summary = "创建小说", description = "支持上传封面图，保存小说基本信息")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,value = "/create_novel")
    public ResponseEntity<NovelDTO> createNovel(
        @RequestParam("title") String title,
        @RequestParam("description") String description,
        @RequestParam("categoryId") Integer categoryId,
        @RequestParam(value = "cover", required = false) MultipartFile coverFile
    ) throws Exception {

        Long currentUserId = userService.getUserWithAuthorities().get().getId();

        String coverUrl = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            String fileName = minioService.uploadFile(coverFile);
            coverUrl = minioService.getPublicUrl(fileName);
        }

        Novel novel = new Novel();
        novel.setTitle(title);
        novel.setDescription(description);
        novel.setCategoryId(categoryId);
        novel.setAuthorId(currentUserId);
        novel.setCoverUrl(coverUrl);
        novel.setStatus(1);
        novel.setCreateTime(Instant.now());
        novel.setUpdateTime(Instant.now());

        Novel result = novelRepository.save(novel);
        return ResponseEntity.ok(novelMapper.toDto(result));
    }
    @GetMapping("/all")
    @Operation(summary = "获取全部小说列表", description = "返回所有小说的简要信息，包括标题、简介、封面")
    public ResponseEntity<List<NovelSimpleDTO>> getAllNovels() {
        List<Novel> novels = novelRepository.findAll();

        List<NovelSimpleDTO> result = novels.stream()
            .map(novel -> {
                String signedCoverUrl = null;
                try {
                    String originalCoverUrl = novel.getCoverUrl();
                    if (originalCoverUrl != null && !originalCoverUrl.isBlank()) {
                        String fileName = minioService.extractFileName(originalCoverUrl);
                        signedCoverUrl = minioService.getSignedUrl(fileName, 3600);
                    }
                } catch (Exception e) {
                    log.warn("封面生成失败", e);
                }
                return new NovelSimpleDTO(novel.getId(),novel.getTitle(), novel.getDescription(), signedCoverUrl);
            })
            .toList();

        return ResponseEntity.ok(result);
    }

    // 按分类获取小说（只返回部分字段）
    @GetMapping("/by-category/{categoryId}")
    @Operation(summary = "根据分类查询小说", description = "通过分类 ID 获取小说列表（只返回简要信息）")
    public ResponseEntity<List<NovelSimpleDTO>> getByCategory(@PathVariable Integer categoryId) {
        List<Novel> novels = novelRepository.findByCategoryId(categoryId);

        List<NovelSimpleDTO> result = novels.stream()
            .map(n -> new NovelSimpleDTO(n.getId(),n.getTitle(), n.getDescription(), n.getCoverUrl()))
            .toList();
        return ResponseEntity.ok(result);
    }

    // 获取当前登录用户的小说列表
    @GetMapping("/mine")
    @Operation(summary = "获取我的小说列表", description = "返回当前登录用户创建的小说（简要信息，封面为1小时有效的签名URL）")
    public ResponseEntity<List<NovelSimpleDTO>> getMyNovels() {
        Long currentUserId = userService.getUserWithAuthorities().get().getId();
        List<Novel> list = novelRepository.findByAuthorId(currentUserId);

        List<NovelSimpleDTO> result = list.stream().map(novel -> {
            String signedCoverUrl = null;

            try {
                String originalCoverUrl = novel.getCoverUrl();
                if (originalCoverUrl != null && !originalCoverUrl.isBlank()) {
                    String fileName = minioService.extractFileName(originalCoverUrl);
                    signedCoverUrl = minioService.getSignedUrl(fileName, 3600); // 1 小时有效
                }
            } catch (Exception e) {
                // 可以选择记录日志，不影响整个列表返回
            }

            return new NovelSimpleDTO(novel.getId(),novel.getTitle(), novel.getDescription(), signedCoverUrl);
        }).toList();

        return ResponseEntity.ok(result);
    }
    /**
     * 收藏、点赞等相关操作
     *
     * @param type      取值来自于 OperateTypeEnum#code
     * @return
     */
    @GetMapping(path = "/favor")
    @Operation(summary = "收藏/点赞小说", description = "根据 type 参数执行收藏或点赞等用户行为")
    public ResponseEntity<Boolean> favor(@RequestParam(name = "novelId") Long novelId,
                                         @Parameter(description = "操作类型：1-收藏，2-点赞，3-取消收藏，4-取消点赞", example = "1")
                                         @RequestParam(name = "type") Integer type) throws IOException, TimeoutException {
        OperateTypeEnum operate = OperateTypeEnum.fromCode(type);
        if (operate == OperateTypeEnum.EMPTY) {
            throw new BusinessException(CommonCodeMsg.PARAM_INVALID);
        }

        // 要求文章必须存在
        Optional<NovelDTO> novelOpt = novelService.findOne(novelId);

        if (novelOpt.isEmpty()) {
            throw new BusinessException(CommonCodeMsg.NOVEL_NOT_EXIST);
        }
        NovelDTO novelDTO = novelOpt.get();

        // 更新用户与文章的点赞/收藏状态
        userFootService.favorArticleComment(DocumentTypeEnum.ARTICLE, novelId, novelDTO.getAuthorId(),
            SecurityUtils.getCurrentUserId().orElseThrow(() -> new RuntimeException("User not logged in")),
            operate);
        return ResponseEntity.ok(true);
    }

    /**
     * 评论列表页
     *
     * @param articleId
     * @return
     */
    @RequestMapping(path = "/common/list")
    @Operation(summary = "获取文章评论列表", description = "分页获取某篇文章的顶级评论（含子评论）")
    public ResponseEntity<List<TopCommentDTO>> list(Long articleId, Long pageNum, Long pageSize) {
        if (NumUtil.nullOrZero(articleId)) {
            throw new BusinessException(CommonCodeMsg.ID_IS_NULL);
        }
        pageNum = Optional.ofNullable(pageNum).orElse(PageParam.DEFAULT_PAGE_NUM);
        pageSize = Optional.ofNullable(pageSize).orElse(PageParam.DEFAULT_PAGE_SIZE);
        List<TopCommentDTO> result = commentService.getArticleComments(articleId, PageParam.newPageInstance(pageNum, pageSize));
        return ResponseEntity.ok(result);
    }

    /**
     * 保存评论
     *
     * @param req
     * @return
     */
    @PostMapping(path = "/comment/save")
    @Operation(summary = "保存评论", description = "保存文章的评论，支持回复评论")
    public ResponseEntity<Long> save(@RequestBody CommentSaveReq req) {
        if (req.getArticleId() == null) {
            throw new BusinessException(CommonCodeMsg.ID_IS_NULL);
        }
        NovelDTO novelDTO = novelService.findOne(req.getArticleId()).orElse(null);
        if (novelDTO == null) {
            throw new BusinessException(CommonCodeMsg.NOVEL_NOT_EXIST);
        }

        // 保存评论
        req.setUserId(SecurityUtils.getCurrentUserId().get());
        req.setCommentContent(StringEscapeUtils.escapeHtml3(req.getCommentContent()));
        Long commentId = commentService.saveComment(req);
        return ResponseEntity.ok(commentId);
    }

    /**
     * 删除评论
     *
     * @param commentId
     * @return
     */

    @Operation(summary = "删除评论", description = "删除用户自己发表的评论")
    @DeleteMapping("/comment/delete")
    public ResponseEntity<Boolean> delete(Long commentId) {
        commentService.deleteComment(commentId, SecurityUtils.getCurrentUserId().get());
        return ResponseEntity.ok(true);
    }

    @Operation(summary = "根据小说 ID 获取所有章节")
    @GetMapping("/list/{novelId}")
    public ResponseEntity<List<ChapterDTO>> listChaptersByNovelId(@PathVariable Long novelId) {
        List<ChapterDTO> chapters = chapterService.findByNovelId(novelId);
        return ResponseEntity.ok(chapters);
    }

}
