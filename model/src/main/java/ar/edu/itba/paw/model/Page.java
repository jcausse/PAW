package ar.edu.itba.paw.model;

import java.util.List;
import lombok.Getter;

@Getter
public final class Page<T> {

    private final List<T> content;
    private final int page;
    private final int pageSize;
    private final long totalCount;

    public Page(final List<T> content, final int page, final int pageSize, final long totalCount) {
        this.content = List.copyOf(content);
        this.page = page;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    public boolean isHasPrevious() {
        return page > 1;
    }

    public boolean isHasNext() {
        return page < getTotalPages();
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
