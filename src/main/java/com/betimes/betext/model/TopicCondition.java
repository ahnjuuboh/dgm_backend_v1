package com.betimes.betext.model;

import javax.persistence.*;

@Entity
@Table(name = "bts_topic_conditional")
public class TopicCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id")
    private Long condition_id;
    @Column(name = "topic_id")
    private Long topic_id;
    @Column(name = "condition_symbol")
    private String condition_symbol;
    @Column(name = "condition_text")
    private String condition_text;

    public Long getCondition_id() {
        return condition_id;
    }

    public void setCondition_id(Long condition_id) {
        this.condition_id = condition_id;
    }

    public Long getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(Long topic_id) {
        this.topic_id = topic_id;
    }

    public String getCondition_symbol() {
        return condition_symbol;
    }

    public void setCondition_symbol(String condition_symbol) {
        this.condition_symbol = condition_symbol;
    }

    public String getCondition_text() {
        return condition_text;
    }

    public void setCondition_text(String condition_text) {
        this.condition_text = condition_text;
    }
}
