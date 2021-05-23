package com.betimes.betext.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bts_topic_info")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long topic_id;
    @Column(name = "topic_name")
    private String topic_name;
    @Column(name = "keyword")
    private String keyword;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Column(name = "start_date")
    private String start_date;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @Column(name = "end_date")
    private String end_date;
    @Column(name = "created_time")
    private Date created_time;
    @Column(name = "created_by")
    private String created_by;
    @Column(name = "updated_time")
    private Date updated_time;
    @Column(name = "updated_by")
    private String updated_by;
    @Column(name = "record_status")
    private String record_status;

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

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public void setUpdated_time(Date updated_time) {
        this.updated_time = updated_time;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public void setRecord_status(String record_status) {
        this.record_status = record_status;
    }
}
