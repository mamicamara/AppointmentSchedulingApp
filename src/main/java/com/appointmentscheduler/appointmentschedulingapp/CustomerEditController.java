package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.Country;
import com.appointmentscheduler.appointmentschedulingapp.models.Customer;
import com.appointmentscheduler.appointmentschedulingapp.models.Division;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.ResourceBundle;

public class CustomerEditController extends CustomerAbstractController {
    private Customer customer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        tbxCustomerID.setText("" + customer.getId());
        tbxName.setText(customer.getName());
        tbxAddress.setText(customer.getAddress());
        tbxPhone.setText(customer.getPhone());
        tbxPostalCode.setText(customer.getPostalCode());

        // Selected in the ComboBox the country associated with the customer's
        // division the division associated with the customer.

        if (customer != null) {
            Division tempDivision = null;

            try {
                tempDivision = db.getDivision(customer.getDivisionId());
            } catch (SQLException e) {
                lblStatus.setText("Error connecting to the database");
            }

            if (tempDivision != null) {
                for (Country country : observableCountries) {
                    if (country.getCountryId() == tempDivision.getCountryId()) {
                        cbxCountry.getSelectionModel().select(country);

                        initializeDivisions(country.getCountryId());

                        for (Division division : observableDivisions) {
                            if (division.getDivisionId() == customer.getDivisionId()) {
                                cbxDivision.getSelectionModel().select(division);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    protected void setCustomer(Customer customer) {
        this.customer = customer;
    }

    protected Customer getCustomer() {

        // Generate customer ID
        try {
            customer.setId(Integer.parseInt(tbxCustomerID.getText()));
            customer.setLastUpdatedBy(currentUser.getUsername());
            customer.setLastUpdate(Instant.now().atOffset(ZoneOffset.UTC));
            return customer;
        } catch (NumberFormatException | NullPointerException e) {
            lblStatus.setText("Error: the customer ID is invalid");
            return null;
        }
    }

    protected boolean isNewRecord() {
        return false;
    }

    protected String getQuery() {
        return "UPDATE Customers SET Customer_Name = ?, Address = ?,  " +
                " Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?," +
                " Division_ID = ? WHERE Customer_ID = ?";
    }

}