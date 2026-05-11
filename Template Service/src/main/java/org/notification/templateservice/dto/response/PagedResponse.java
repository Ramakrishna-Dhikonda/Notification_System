package org.notification.templateservice.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PagedResponse<T> {
    private List<T> content;
    private int     page;
    private int     size;
    private long    totalElements;
    private int     totalPages;
    private boolean first;
    private boolean last;

    public static <T> PagedResponse<T> from(Page<T> page) {
        PagedResponse<T> r = new PagedResponse<>();
        r.content       = page.getContent();
        r.page          = page.getNumber();
        r.size          = page.getSize();
        r.totalElements = page.getTotalElements();
        r.totalPages    = page.getTotalPages();
        r.first         = page.isFirst();
        r.last          = page.isLast();
        return r;
    }
}
