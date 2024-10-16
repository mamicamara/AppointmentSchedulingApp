package com.appointmentscheduler.appointmentschedulingapp.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Division extends Metadata {
    private int divisionId;
    private String division;
    private int countryId;

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String toString(){
        return String.format("%d. %s",divisionId, division);
    }

}
