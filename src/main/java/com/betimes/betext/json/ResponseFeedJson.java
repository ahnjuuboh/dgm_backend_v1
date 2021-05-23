package com.betimes.betext.json;

import java.util.List;

public class ResponseFeedJson {
    private List<FeedJson> data;
    private PagingJson paging;

    public List<FeedJson> getData() {
        return data;
    }

    public void setData(List<FeedJson> data) {
        this.data = data;
    }

    public PagingJson getPaging() {
        return paging;
    }

    public void setPaging(PagingJson paging) {
        this.paging = paging;
    }
}
