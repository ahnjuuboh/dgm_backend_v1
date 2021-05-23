package com.betimes.betext.json;

import java.time.LocalDate;
import java.util.List;

public class TopicJson {
    private Long topic_id;
    private String topic_name;
    private String keyword;
    private String start_date;
    private String end_date;
    private String date_range;
    private List<String> condition_and;
    private List<String> condition_or;
    private List<String> condition_not;

    public Long getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(Long topic_id) {
        this.topic_id = topic_id;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDate_range() {
        return date_range;
    }

    public void setDate_range(String date_range) {
        this.date_range = date_range;
    }

    public List<String> getCondition_and() {
        return condition_and;
    }

    public void setCondition_and(List<String> condition_and) {
        this.condition_and = condition_and;
    }

    public List<String> getCondition_or() {
        return condition_or;
    }

    public void setCondition_or(List<String> condition_or) {
        this.condition_or = condition_or;
    }

    public List<String> getCondition_not() {
        return condition_not;
    }

    public void setCondition_not(List<String> condition_not) {
        this.condition_not = condition_not;
    }
}
