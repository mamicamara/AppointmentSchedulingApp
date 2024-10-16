package com.appointmentscheduler.appointmentschedulingapp.models;

/**
 * Class encapsulates count statistic for a division.
 */
public class CustomerCount {
    private String division;
    private int total;

    public CustomerCount(String division, int total){
        this.division = division;
        this.total = total;
    }
    public String getDivision() {
        return division;
    }

    public int getTotal() {
        return total;
    }

}
