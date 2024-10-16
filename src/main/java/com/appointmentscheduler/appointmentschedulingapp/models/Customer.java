package com.appointmentscheduler.appointmentschedulingapp.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Customer extends Metadata {
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;
    private Division division;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
        this.divisionId = division.getDivisionId();
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }
}
