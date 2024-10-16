package com.appointmentscheduler.appointmentschedulingapp.models;

import java.time.LocalDateTime;

public class Schedule {

    private String title;
    // the consultant is the user logged in; so the
    // consultantId is the same as the userId.
    private int consultantId;
    private LocalDateTime start;
    private LocalDateTime end;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getConsultantId() {
        return consultantId;
    }

    public void setConsultantId(int consultantId) {
        this.consultantId = consultantId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
