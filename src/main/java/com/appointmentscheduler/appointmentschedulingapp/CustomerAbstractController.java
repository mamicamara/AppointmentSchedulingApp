package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public abstract class CustomerAbstractController implements Initializable {
    private UserSingleton userSingleton = UserSingleton.getInstance();

    @FXML
    protected Label lblStatus;
    @FXML
    protected TextField tbxCustomerID, tbxName, tbxAddress, tbxPhone, tbxPostalCode;
    @FXML
    protected Button btnCancel, btnSave;
    @FXML
    protected ComboBox<Country> cbxCountry;
    @FXML
    protected ComboBox<Division> cbxDivision;

    protected ObservableList<Country> observableCountries = null;
    protected ObservableList<Division> observableDivisions = null;

    protected DatabaseService db = DatabaseService.getInstance();

    protected User currentUser;

    /**
     * Initializes LoginController.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create a factory for the ComboBox so that the cbxCountry ComboBox can be
        // populated with Country objects.
        Callback<ListView<Country>, ListCell<Country>> countryFactory = lv -> new ListCell<Country>() {
            @Override
            protected void updateItem(Country country, boolean empty) {
                super.updateItem(country, empty);
                setText(empty ? "" : country.getCountry());
            }
        };
        cbxCountry.setCellFactory(countryFactory);
        cbxCountry.setButtonCell(countryFactory.call(null));

        try {
            observableCountries = FXCollections.observableList(db.getCountries());
            cbxCountry.getItems().addAll(observableCountries);
        } catch (SQLException e) {
            lblStatus.setText("Error connecting to the database");
        }

        Country country = cbxCountry.getValue();
        if (country != null) {
            initializeDivisions(country.getCountryId());
        }

        // Set the current user.
        currentUser = userSingleton.getUser();

        // Disable customer ID input field.
        tbxCustomerID.setDisable(true);
    }

    @FXML
    protected void onCountrySelection(ActionEvent event) {
        Country country = cbxCountry.getValue();
        if (country != null) {
            initializeDivisions(country.getCountryId());
        }
    }

    @FXML
    protected void onSaveButtonClick(ActionEvent event) {
        Customer customer = getCustomer();
        if (customer == null) {
            return;
        }

        customer = populateCustomer(customer);
        if (customer == null) {
            return;
        }

        try {
            if (db.saveCustomer(customer, getQuery(), isNewRecord())) {
                backToMain(event);
            } else {
                lblStatus.setText("Error: unable to " + (isNewRecord() ? "Add" : "Edit") + " record");
            }
        } catch (SQLException e) {
            lblStatus.setText("Error saving to the database");
        }
    }

    protected void initializeDivisions(int countryId) {
        // Create a factory for the ComboBox so that the cbxDivision ComboBox can be
        // populated with Division objects.
        Callback<ListView<Division>, ListCell<Division>> cbxFactory = lv -> new ListCell<Division>() {
            @Override
            protected void updateItem(Division division, boolean empty) {
                super.updateItem(division, empty);
                setText(empty ? "" : division.getDivision());
            }
        };
        cbxDivision.setCellFactory(cbxFactory);
        cbxDivision.setButtonCell(cbxFactory.call(null));

        try {
            cbxDivision.getItems().clear();
            observableDivisions = FXCollections.observableList(db.getDivisions(countryId));
            cbxDivision.getItems().addAll(observableDivisions);
        } catch (SQLException e) {
            lblStatus.setText("Error connecting to the database");
        }
    }

    protected Customer populateCustomer(Customer customer) {
        String name = tbxName.getText().trim();
        if (name.isEmpty() || name.length() > 50) {
            Dialogs.messageDialog("Invalid name",
                    "Accepted name length is between 1 and 50", true);
            return null;
        }
        customer.setName(name);

        String address = tbxAddress.getText().trim();
        if (address.isEmpty() || address.length() > 100) {
            Dialogs.messageDialog("Invalid address",
                    "Accepted address length is between 1 and 50", true);
            return null;
        }
        customer.setAddress(address);

        String postalCode = tbxPostalCode.getText().trim();
        if (postalCode.isEmpty() || postalCode.length() > 50) {
            Dialogs.messageDialog("Invalid postal code",
                    "Accepted postal code length is between 1 and 50", true);
            return null;
        }
        customer.setPostalCode(postalCode);

        String phone = tbxPhone.getText().trim();
        if (phone.isEmpty() || phone.length() > 50) {
            Dialogs.messageDialog("Invalid phone",
                    "Accepted phone length is between 1 and 50", true);
            return null;
        }
        customer.setPhone(phone);

        Division division = cbxDivision.getValue();
        if (division == null) {
            Dialogs.messageDialog("Invalid division",
                    "You must select a valid division", true);
            return null;
        }
        customer.setDivision(division);

        return customer;
    }

    @FXML
    protected void onCancelButtonClick(ActionEvent event) {
        backToMain(event);
    }

    protected abstract boolean isNewRecord();

    protected abstract Customer getCustomer();

    protected abstract String getQuery();

    protected void backToMain(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("customers-main-view.fxml"));
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
        } catch (IOException e) {
            lblStatus.setText("Unable to load view");
        }
    }

}