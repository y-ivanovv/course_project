package ru.edu.project.control.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Стабильный формат страничного ответа (вместо прямой сериализации Spring Page).
 */
public class PageResponse<T> {

    private final List<T> content;
    private final long total;
    private final int offset;
    private final int limit;

    public PageResponse(List<T> content, long total, int offset, int limit) {
        this.content = content;
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public static <T> PageResponse<T> of(Page<T> page, int offset, int limit) {
        return new PageResponse<>(page.getContent(), page.getTotalElements(), offset, limit);
    }

    public List<T> getContent() { return content; }
    public long getTotal() { return total; }
    public int getOffset() { return offset; }
    public int getLimit() { return limit; }
}
