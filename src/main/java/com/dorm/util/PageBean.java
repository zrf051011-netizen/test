package com.dorm.util;

import java.util.List;

public class PageBean<T> {
    private List<T> list;
    private int totalCount;
    private int page;
    private int size;
    private int totalPage;

    public PageBean(List<T> list, int totalCount, int page, int size) {
        this.list = list;
        this.totalCount = totalCount;
        this.page = Math.max(page, 1);
        this.size = Math.max(size, 1);
        this.totalPage = (int) Math.ceil(totalCount * 1.0 / this.size);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public boolean getFirstPage() {
        return page <= 1;
    }

    public boolean getLastPage() {
        return totalPage == 0 || page >= totalPage;
    }

    public boolean getHasPreviousPage() {
        return page > 1;
    }

    public boolean getHasNextPage() {
        return page < totalPage;
    }
}
