package vn.aeoc.base.config.extention.paging;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

    private Long total;
    private Integer page;
    private List<T> items;
    private Boolean loadMoreAble;
    private Boolean preLoadAble;

    public Page() {
    }

    public Page(Pageable pageable, List<T> items) {
        this.total = pageable.getTotal();
        this.page = pageable.getPage();
        this.items = items;
        this.loadMoreAble = pageable.getTotal() != null
                && pageable.getTotal().intValue() > (pageable.getOffset() + pageable.getLimit());
    }

    public Page(Long total, Integer page, List<T> items) {
        this.page = page;
        this.total = total;
        this.items = items;
    }

    public List<T> getPagableList(Pageable pageable, List<T> objects) {
        int from = (pageable.getPage() - 1) * pageable.getLimit() < 0 ? 0 : (pageable.getPage() - 1) * pageable.getLimit();
        int to = (from + pageable.getLimit()) > objects.size() ? objects.size() : (from + pageable.getLimit());
        if (from < objects.size()) objects = objects.subList(from, to);
        else objects = new ArrayList<>();
        return objects;
    }

    public Long getTotal() {
        return total == null ? 0L : total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
}
