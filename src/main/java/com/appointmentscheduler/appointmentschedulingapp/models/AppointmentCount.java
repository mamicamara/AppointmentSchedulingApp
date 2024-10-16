package com.appointmentscheduler.appointmentschedulingapp.models;

public class AppointmentCount {
    private String month;
    private String type;
    private int total;

    public AppointmentCount(String month, String type, int total) {
        this.month = month;
        this.type = type;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public String getType() {
        return type;
    }

    public int getTotal() {
        return total;
    }

}