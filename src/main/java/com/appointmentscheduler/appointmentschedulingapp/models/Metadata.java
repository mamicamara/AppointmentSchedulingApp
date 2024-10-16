package com.appointmentscheduler.appointmentschedulingapp.models;

import com.appointmentscheduler.appointmentschedulingapp.Utility;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class Metadata {
    private OffsetDateTime createDate;
    private String createdBy;
    private OffsetDateTime lastUpdate;
    private String lastUpdatedBy;

    public String getCreateDate() {
        return Utility.formatDate(createDate);
    }

    public String getLastUpdate() {
        return Utility.formatDate(lastUpdate);
    }

    public OffsetDateTime getDateCreatedAsOffsetDT() {
        return createDate;
    }

    public OffsetDateTime getLastUpdatesAsOffsetDT() {
        return lastUpdate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setCreateDate(OffsetDateTime createDate) {
        this.createDate = createDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastUpdate(OffsetDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public void setMetadata(Metadata metadata) {
        createDate = metadata.getDateCreatedAsOffsetDT();
        lastUpdate = metadata.getLastUpdatesAsOffsetDT();
        createdBy = metadata.getCreatedBy();
        lastUpdatedBy = metadata.getLastUpdatedBy();
    }


}
