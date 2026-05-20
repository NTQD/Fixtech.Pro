package vn.aeoc.base.config.extention.paging;

import lombok.Generated;

public class Pageable {
    public static final Integer DEFAULT_LIMIT = 10;
    public static final Integer DEFAULT_PAGE = 1;
    public static final Integer MAXIMUM_LIMIT = 200;
    private Integer page;
    private Integer offset;
    private Integer limit;
    private Long total;

    public Pageable() {
        this.page = DEFAULT_PAGE;
        this.limit = DEFAULT_LIMIT;
        this.offset = 0;
    }

    public Integer getOffset() {
        // Nếu offset được set thủ công (ví dụ từ client), thì dùng
        if (this.offset != null && this.offset > 0) {
            return this.offset;
        }

        int currentPage = (this.page == null || this.page <= 0) ? DEFAULT_PAGE : this.page;
        int currentLimit = (this.limit == null || this.limit <= 0) ? DEFAULT_LIMIT : this.limit;

        int calculatedOffset = (currentPage - 1) * currentLimit;
        return Math.max(calculatedOffset, 0);
    }

    @Generated
    public Integer getPage() {
        return this.page;
    }

    @Generated
    public Integer getLimit() {
        return this.limit;
    }

    @Generated
    public Long getTotal() {
        return this.total;
    }

    @Generated
    public Pageable setPage(final Integer page) {
        this.page = page;
        return this;
    }

    @Generated
    public Pageable setOffset(final Integer offset) {
        this.offset = offset;
        return this;
    }

    @Generated
    public Pageable setLimit(final Integer limit) {
        this.limit = limit;
        return this;
    }

    @Generated
    public Pageable setTotal(final Long total) {
        this.total = total;
        return this;
    }
}
