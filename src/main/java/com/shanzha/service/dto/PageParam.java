package com.shanzha.service.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 数据库分页参数
 *
 * @author louzai
 * @date 2024-07-120
 */
@Data
public class PageParam {

    public static final Long DEFAULT_PAGE_NUM = 1L;
    public static final Long DEFAULT_PAGE_SIZE = 10L;

    public static final Long TOP_PAGE_SIZE = 4L;

    private long pageNum;

    private long pageSize;
    private long offset;
    private long limit;

    public static PageParam newPageInstance() {
        return newPageInstance(DEFAULT_PAGE_NUM, DEFAULT_PAGE_SIZE);
    }

    public static PageParam newPageInstance(Integer pageNum, Integer pageSize) {
        return newPageInstance(pageNum.longValue(), pageSize.longValue());
    }

    public static PageParam newPageInstance(Long pageNum, Long pageSize) {
        if (pageNum == null || pageSize == null) {
            return null;
        }

        final PageParam pageParam = new PageParam();
        pageParam.pageNum = pageNum;
        pageParam.pageSize = pageSize;

        pageParam.offset = (pageNum - 1) * pageSize;
        pageParam.limit = pageSize;

        return pageParam;
    }

    public static String getLimitSql(PageParam pageParam) {
        return String.format("limit %s,%s", pageParam.offset, pageParam.limit);
    }

    public static Pageable toPageable(PageParam pageParam) {
        int page = Math.max((int) pageParam.getPageNum() - 1, 0); // PageRequest 页码从 0 开始
        int size = (int) pageParam.getPageSize();
        return PageRequest.of(page, size);
    }

    /**
     * 将 PageParam 转换为 Pageable，并支持排序
     */
    public static Pageable toPageable(PageParam pageParam, Sort sort) {
        int page = Math.max((int) pageParam.getPageNum() - 1, 0);
        int size = (int) pageParam.getPageSize();
        return PageRequest.of(page, size, sort);
    }

    /**
     * 快捷方式：按某字段降序（常用于 createdDate/id）
     */
    public static Pageable toPageableDesc(PageParam pageParam, String sortBy) {
        return toPageable(pageParam, Sort.by(Sort.Direction.DESC, sortBy));
    }

    /**
     * 快捷方式：按某字段升序
     */
    public static Pageable toPageableAsc(PageParam pageParam, String sortBy) {
        return toPageable(pageParam, Sort.by(Sort.Direction.ASC, sortBy));
    }
}
