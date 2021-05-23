package com.betimes.betext.json;

public class FeedSummaryJson {
    private FeedSummaryItemJson all;
    private FeedSummaryItemJson facebook;
    private FeedSummaryItemJson twitter;

    public FeedSummaryItemJson getAll() {
        return all;
    }

    public void setAll(FeedSummaryItemJson all) {
        this.all = all;
    }

    public FeedSummaryItemJson getFacebook() {
        return facebook;
    }

    public void setFacebook(FeedSummaryItemJson facebook) {
        this.facebook = facebook;
    }

    public FeedSummaryItemJson getTwitter() {
        return twitter;
    }

    public void setTwitter(FeedSummaryItemJson twitter) {
        this.twitter = twitter;
    }
}
