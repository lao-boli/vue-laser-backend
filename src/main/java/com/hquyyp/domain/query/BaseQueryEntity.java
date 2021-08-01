
package com.hquyyp.domain.query;

public class BaseQueryEntity {
    private Integer page;
    private Integer pageSize;

    public void setPage(Integer page) {
        this.page = page;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public BaseQueryEntity() {
    }

    public BaseQueryEntity(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return this.page;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }
}
