package com.betimes.betext.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "bts_time_schedule")
public class Schedule {
    @Id
    @Column(name = "schedule_id")
    private String schedule_id;
    @Column(name = "minutes")
    private Long minutes;
    @Column(name = "created_by")
    private String created_by;
    @Column(name = "updated_time")
    private Date updated_time;
    @Column(name = "updated_by")
    private String updated_by;
    @Column(name = "record_status")
    private String record_status;

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public Long getMinutes() {
        return minutes;
    }

    public void setMinutes(Long minutes) {
        this.minutes = minutes;
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
