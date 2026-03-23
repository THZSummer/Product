package com.openapp.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 DTO（泛型）
 * 
 * @param <T> 数据类型
 * @author open-app
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * 数据列表
     */
    private List<T> items;

    /**
     * 总数
     */
    private long total;

    /**
     * 当前页码（从 1 开始）
     */
    private int page;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 创建分页响应
     * 
     * @param items 数据列表
     * @param total 总数
     * @param page 当前页码
     * @param size 每页大小
     * @param <T> 数据类型
     * @return 分页响应
     */
    public static <T> PageResponse<T> of(List<T> items, long total, int page, int size) {
        int totalPages = (int) Math.ceil((double) total / size);
        return PageResponse.<T>builder()
                .items(items)
                .total(total)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .build();
    }
}
