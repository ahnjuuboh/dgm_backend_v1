package com.betimes.betext.json;

import java.util.List;

public class TopicEsBoolJson {
    private List<Object> should;
    private List<Object> must_not;
    private Long minimum_should_match;
    private List<Object> filter;

    public List<Object> getShould() {
        return should;
    }

    public void setShould(List<Object> should) {
        this.should = should;
    }

    public List<Object> getMust_not() {
        return must_not;
    }

    public void setMust_not(List<Object> must_not) {
        this.must_not = must_not;
    }

    public Long getMinimum_should_match() {
        return minimum_should_match;
    }

    public void setMinimum_should_match(Long minimum_should_match) {
        this.minimum_should_match = minimum_should_match;
    }

    public List<Object> getFilter() {
        return filter;
    }

    public void setFilter(List<Object> filter) {
        this.filter = filter;
    }
}
