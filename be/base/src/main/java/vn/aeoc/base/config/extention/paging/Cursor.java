package vn.aeoc.base.config.extention.paging;

import lombok.Getter;
import java.util.List;

@Getter
public class Cursor<T> {
    private final List<T>  data;
    private final Integer  nextCursor;  // id of last item, null = no more pages
    private final boolean  hasMore;

    public Cursor(List<T> data, int limit, java.util.function.Function<T, Integer> idExtractor) {
        this.hasMore    = data.size() > limit;
        this.data       = hasMore ? data.subList(0, limit) : data;
        this.nextCursor = hasMore ? idExtractor.apply(this.data.getLast()) : null;
    }
}