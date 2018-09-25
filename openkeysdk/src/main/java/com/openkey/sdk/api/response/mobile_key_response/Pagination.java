package com.openkey.sdk.api.response.mobile_key_response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pagination {

    @SerializedName("page_count")
    @Expose
    private Integer pageCount;
    @SerializedName("current_page")
    @Expose
    private Integer currentPage;
    @SerializedName("has_next_page")
    @Expose
    private Boolean hasNextPage;
    @SerializedName("has_prev_page")
    @Expose
    private Boolean hasPrevPage;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("limit")
    @Expose
    private Object limit;

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Boolean getHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(Boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public Boolean getHasPrevPage() {
        return hasPrevPage;
    }

    public void setHasPrevPage(Boolean hasPrevPage) {
        this.hasPrevPage = hasPrevPage;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Object getLimit() {
        return limit;
    }

    public void setLimit(Object limit) {
        this.limit = limit;
    }

}