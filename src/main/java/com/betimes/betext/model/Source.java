package com.betimes.betext.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bts_data_source")
@IdClass(SourceId.class)
public class Source {
    @Id
    @Column(name = "source_id")
    private String source_id;
    @Column(name = "source_type")
    private String source_type;
    @Column(name = "created_time")
    private Date created_time;
    @Id
    @Column(name = "created_by")
    private String created_by;
    @Column(name = "updated_time")
    private Date updated_time;
    @Column(name = "updated_by")
    private String updated_by;
    @Column(name = "record_status")
    private String record_status;

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
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
