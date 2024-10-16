package com.appointmentscheduler.appointmentschedulingapp.models;

import com.appointmentscheduler.appointmentschedulingapp.Utility;

import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class Appointment extends Metadata {
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String type;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private int customerId;
    private int userId;
    private int contactId;
    private Customer customer;
    private User user;
    private Contact contact;

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStart() {
        return Utility.formatDate(start);
    }

    public OffsetDateTime getStartAsOffsetDT() {
        return start;
    }

    public String getEnd() {
        return Utility.formatDate(end);
    }

    public OffsetDateTime getEndAsOffsetDT() {
        return end;
    }

    public void setStart(OffsetDateTime start) {
        this.start = start;
    }

    public void setEnd(OffsetDateTime end) {
        this.end = end;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer.getId();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
        this.contactId = contact.getContactId();
    }

}
