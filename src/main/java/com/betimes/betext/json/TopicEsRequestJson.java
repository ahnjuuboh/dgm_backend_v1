package com.betimes.betext.json;

public class TopicEsRequestJson {
    private Long size;
    private Long from;
    private Object sort;
    private Object query;
    private Object aggs;
//    private Object post_filter;

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Object getSort() {
        return sort;
    }

    public void setSort(Object sort) {
        this.sort = sort;
    }

    public Object getQuery() {
        return query;
    }

    public void setQuery(Object query) {
        this.query = query;
    }

    public Object getAggs() {
        return aggs;
    }

    public void setAggs(Object aggs) {
        this.aggs = aggs;
    }

//    public Object getPost_filter() {
//        return post_filter;
//    }
//
//    public void setPost_filter(Object post_filter) {
//        this.post_filter = post_filter;
//    }
}
