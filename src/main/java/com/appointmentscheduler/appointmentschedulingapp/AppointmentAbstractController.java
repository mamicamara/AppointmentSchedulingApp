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
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * The AppointmentAbstractController
 */
public abstract class AppointmentAbstractController implements Initializable {
    private UserSingleton userSingleton = UserSingleton.getInstance();
    @FXML
    protected Button btnCancel, btnSave;
    @FXML
    protected Label lblStatus;
    @FXML
    protected TextField tbxAppointmentID, tbxTitle, tbxDescription, tbxLocation, tbxType;
    @FXML
    protected DatePicker dpStart, dpEnd;
    @FXML
    protected ComboBox<String> cbxStartHour, cbxStartMin, cbxStartSec;
    @FXML
    protected ComboBox<String> cbxEndHour, cbxEndMin, cbxEndSec;
    @FXML
    protected ComboBox<Customer> cbxCustomer;
    @FXML
    protected ComboBox<User> cbxUser;
    @FXML
    protected ComboBox<Contact> cbxContact;
    protected DatabaseService db = DatabaseService.getInstance();
    protected ObservableList<Customer> observableCustomers = null;
    protected ObservableList<User> observableUsers = null;
    protected ObservableList<Contact> observableContacts = null;
    protected User currentUser;

    /**
     * Initializes LoginController.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = userSingleton.getUser();
        tbxAppointmentID.setDisable(true);
        loadTimeCombBoxes();
        loadCustomerCbx();
        loadContactCbx();
        loadUserCbx();
    }

    @FXML
    protected void onSaveButtonClick(ActionEvent event) {
        Appointment appointment = getAppointment();
        if (appointment == null) {
            return;
        }

        appointment = populateAppointment(appointment);
        if (appointment == null) {
            return;
        }

        try {
            if (db.saveAppointment(appointment, getQuery(), isNewRecord())) {
                backToMain(event);
            } else {
                lblStatus.setText("Error: unable to " + getAction() + " record");
            }
        } catch (SQLException e) {
            lblStatus.setText("Error connecting to the database");
        } catch (Exception e) {
            lblStatus.setText("Error: invalid appointment values");
        }
    }

    protected Appointment populateAppointment(Appointment appointment) {

        String title = tbxTitle.getText().trim();
        if (title.isEmpty() || title.length() > 50) {
            Dialogs.messageDialog("Invalid Title", "Accepted title length is between 1 and 50", true);
            return null;
        }
        appointment.setTitle(title);

        String description = tbxDescription.getText().trim();
        if (description.isEmpty() || description.length() > 50) {
            Dialogs.messageDialog("Invalid Description", "Accepted description length is between 1 and 50", true);
            return null;
        }
        appointment.setDescription(description);

        String location = tbxLocation.getText().trim();
        if (location.isEmpty() || location.length() > 50) {
            Dialogs.messageDialog("Invalid Location", "Accepted location length is between 1 and 50", true);
            return null;
        }
        appointment.setLocation(location);

        String type = tbxType.getText().trim();
        if (type.isEmpty() || type.length() > 50) {
            Dialogs.messageDialog("Invalid Type", "Accepted type length is between 1 and 50", true);
            return null;
        }
        appointment.setType(type);

        LocalDateTime startDatetime = addTimeToDate(dpStart.getValue(), cbxStartHour.getValue(), cbxStartMin.getValue(), cbxStartSec.getValue());
        if (startDatetime == null) {
            Dialogs.messageDialog("Invalid Date/Time", "The start date/time is invalid", true);
            return null;
        }

        LocalDateTime endDatetime = addTimeToDate(dpEnd.getValue(), cbxEndHour.getValue(), cbxEndMin.getValue(), cbxEndSec.getValue());

        if (endDatetime == null) {
            Dialogs.messageDialog("Invalid Date/Time", "The end date/time is invalid", true);
            return null;
        }

        if (startDatetime.isAfter(endDatetime)) {
            Dialogs.messageDialog("Invalid appointment", "The end date is before start date", true);
            return null;
        }


        if (!timeIsWithinBusinessHours()){
            return null;
        }

        OffsetDateTime startDatetimeInSysOffset = startDatetime.atOffset(getSystemOffset());
        OffsetDateTime endDatetimeInSysOffset = endDatetime.atOffset(getSystemOffset());

        OffsetDateTime startDatetimeInUTC = startDatetimeInSysOffset.withOffsetSameInstant(ZoneOffset.UTC);

        OffsetDateTime endDatetimeInUTC = endDatetimeInSysOffset.withOffsetSameInstant(ZoneOffset.UTC);

        try {
            if (db.isOverlapping(startDatetimeInUTC, endDatetimeInUTC)) {
                Dialogs.messageDialog("Appointment Overlap", "The appointment will overlap an existing one", true);
                return null;
            }
        } catch (SQLException ex) {
            Dialogs.messageDialog("Database error", "Unable to access appointments in the database", true);
            return null;
        }

        appointment.setStart(startDatetimeInUTC);
        appointment.setEnd(endDatetimeInUTC);

        Customer customer = cbxCustomer.getValue();
        if (customer == null) {
            Dialogs.messageDialog("Invalid Customer", "You must select a valid customer", true);
            return null;
        }
        appointment.setCustomer(customer);

        User user = cbxUser.getValue();
        if (user == null) {
            Dialogs.messageDialog("Invalid User", "You must select a valid user", true);
            return null;
        }
        appointment.setUser(user);

        Contact contact = cbxContact.getValue();
        if (contact == null) {
            Dialogs.messageDialog("Invalid Contact", "You must select a valid contact", true);
            return null;
        }
        appointment.setContact(contact);

        return appointment;
    }

    /**
     * Checks whether the start time and end time are within accepted business hours (between 8:00 a.m.
     * and 10:00 p.m. Eastern Time (ET).
     *
     * @return true if the start time and end time are within accepted business hours; otherwise
     * returns false.
     */
    protected boolean timeIsWithinBusinessHours(){
        // Start time in system's default offset.
        OffsetTime startTime = createTimeInLocalOffset(cbxStartHour.getValue(), cbxStartMin.getValue(), cbxStartSec.getValue());
        if (!validateTimes(startTime)) {
            Dialogs.messageDialog("Invalid appointment Time", "The appointment end time is not valid", true);
            return false;
        }

        // End time in system's default offset.
        OffsetTime endTime = createTimeInLocalOffset(cbxEndHour.getValue(), cbxEndMin.getValue(), cbxEndSec.getValue());
        if (!validateTimes(endTime)) {
            Dialogs.messageDialog("Invalid appointment Time", "The appointment end time is not valid", true);
            return false;
        }

        return true;
    }

    protected boolean validateTimes(OffsetTime time) {
        if (time == null){
            return false;
        }

        // Valid Business hours is 8:00 a.m. to 10:00 p.m. Eastern Time (ET).
        OffsetTime minTime = OffsetTime.of(LocalTime.of(8, 0, 0), ZoneOffset.of("-05:00"));
        OffsetTime maxTime = OffsetTime.of(LocalTime.of(22, 0, 0), ZoneOffset.of("-05:00"));

        // Start time in eastern time zone.
        OffsetTime timeInET = time.withOffsetSameInstant(ZoneOffset.of("-05:00"));

        // Check if start time is within business hours.
        if (timeInET.isBefore(minTime) || timeInET.isAfter(maxTime)){
            Dialogs.messageDialog("Invalid appointment Time", "The appointment is outside business hours", true);
            return false;
        }

        return true;
    }

    protected OffsetTime createTimeInLocalOffset(String hr, String min, String sec) {
        try {
            if (sec.length() == 1){
                sec = "0" + sec;
            }
            if (min.length() == 1){
                min = "0" + min;
            }
            LocalTime localTime = LocalTime.parse(hr + ":" + min + ":" + sec);
            return localTime.atOffset(getSystemOffset());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    protected ZoneOffset getSystemOffset(){
        return ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
    }


//    protected boolean withinBusinessHours(LocalTime time) {
//        LocalTime.of(8, 0, 0);
//        LocalTime.of(22, 0, 0);
//
//
//    }

    @FXML
    protected void onCancelButtonClick(ActionEvent event) {
        backToMain(event);
    }

    protected abstract boolean isNewRecord();

    protected abstract String getAction();

    protected abstract String getQuery();

    protected abstract Appointment getAppointment();

    private LocalDateTime addTimeToDate(LocalDate date, String hr, String min, String sec) {
        if (date == null || hr == null || min == null || sec == null) {
            return null;
        }

        try {
            return date.atStartOfDay().plusHours(Integer.parseInt(hr)).plusMinutes(Integer.parseInt(min)).plusSeconds(Integer.parseInt(sec));
        } catch (Exception ex) {
            return null;
        }
    }

    protected void loadTimeCombBoxes() {
        String hours[] = new String[24];
        String minutes[] = new String[60];
        String seconds[] = new String[60];
        for (int i = 0; i < 24; i++) {
            String iStr = Integer.toString(i);
            if (i < 10) {
                iStr = "0" + iStr;
            }
            hours[i] = iStr;
            minutes[i] = iStr;
            seconds[i] = iStr;
        }
        for (int i = 24; i < 60; i++) {
            String iStr = Integer.toString(i);
            minutes[i] = iStr;
            seconds[i] = iStr;
        }

        cbxStartHour.getItems().addAll(hours);
        cbxStartMin.getItems().addAll(minutes);
        cbxStartSec.getItems().addAll(seconds);

        cbxEndHour.getItems().addAll(hours);
        cbxEndMin.getItems().addAll(minutes);
        cbxEndSec.getItems().addAll(seconds);
    }

    protected void loadCustomerCbx() {
        // Create a factory for the ComboBox to be populated with given objects.
        Callback<ListView<Customer>, ListCell<Customer>> customerCbxFactory = lv -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                setText(empty ? "" : customer.getName());
            }
        };
        cbxCustomer.setCellFactory(customerCbxFactory);
        cbxCustomer.setButtonCell(customerCbxFactory.call(null));

        try {
            observableCustomers = FXCollections.observableList(db.getCustomers());
            cbxCustomer.getItems().addAll(observableCustomers);
        } catch (SQLException e) {
            lblStatus.setText("Error connecting to the database");
        }
    }

    protected void loadContactCbx() {
        // Create a factory for the ComboBox to be populated with given objects.
        Callback<ListView<Contact>, ListCell<Contact>> contactCbxFactory = lv -> new ListCell<Contact>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                setText(empty ? "" : contact.getName());
            }
        };
        cbxContact.setCellFactory(contactCbxFactory);
        cbxContact.setButtonCell(contactCbxFactory.call(null));

        try {
            observableContacts = FXCollections.observableList(db.getContacts());
            cbxContact.getItems().addAll(observableContacts);
        } catch (SQLException e) {
            lblStatus.setText("Error connecting to the database");
        }
        currentUser = userSingleton.getUser();

        tbxAppointmentID.setDisable(true);
    }

    protected void loadUserCbx() {
        // Create a factory for the ComboBox to be populated with given objects.
        Callback<ListView<User>, ListCell<User>> userCbxFactory = lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                setText(empty ? "" : user.getUsername());
            }
        };
        cbxUser.setCellFactory(userCbxFactory);
        cbxUser.setButtonCell(userCbxFactory.call(null));

        try {
            observableUsers = FXCollections.observableList(db.getUsers());
            cbxUser.getItems().addAll(observableUsers);
        } catch (SQLException e) {
            lblStatus.setText("Error connecting to the database");
        }
    }


    private void backToMain(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments-main-view.fxml"));
            AppointmentMainController appointmentController = new AppointmentMainController();
            appointmentController.setFromLogin(false);
            loader.setController(appointmentController);
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
        } catch (IOException e) {
            lblStatus.setText("Unable to load view");
        }
    }

}