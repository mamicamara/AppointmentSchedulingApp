package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.ResourceBundle;

public class CustomerAddController extends CustomerAbstractController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        tbxCustomerID.setText("Auto-Generated");
    }

    protected Customer getCustomer() {
        Customer newCustomer = new Customer();

        // Generate customer ID
        try {
            newCustomer.setId(db.generateID("customers", "Customer_ID"));
            newCustomer.setCreatedBy(currentUser.getUsername());
            newCustomer.setCreateDate(Instant.now().atOffset(ZoneOffset.UTC));
            return newCustomer;
        } catch (SQLException e) {
            lblStatus.setText("Error: unable to generate ID");
            return null;
        }

    }

    protected boolean isNewRecord() {
        return true;
    }

    @Override
    protected String getQuery() {
        return "INSERT INTO Customers (Customer_Name, Address,  Postal_Code, Phone," +
                " Create_Date, Created_By, Division_ID, Customer_ID) " +
                " VALUES( ?, ?, ?, ?, ?, ?, ?, ? )";
    }

}