package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.Appointment;
import com.appointmentscheduler.appointmentschedulingapp.models.Contact;
import com.appointmentscheduler.appointmentschedulingapp.models.Customer;
import com.appointmentscheduler.appointmentschedulingapp.models.User;

import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ResourceBundle;

public class AppointmentEditController extends AppointmentAbstractController {
    private Appointment appointment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);

        tbxAppointmentID.setText("" + appointment.getAppointmentId());
        tbxTitle.setText(appointment.getTitle());
        tbxDescription.setText(appointment.getDescription());
        tbxLocation.setText(appointment.getLocation());
        tbxType.setText(appointment.getType());
        dpStart.setValue(appointment.getStartAsOffsetDT().toLocalDate());
        dpEnd.setValue(appointment.getEndAsOffsetDT().toLocalDate());

        cbxStartHour.setValue(Integer.toString(appointment.getStartAsOffsetDT().getHour()));
        cbxStartMin.setValue(Integer.toString(appointment.getStartAsOffsetDT().getMinute()));
        cbxStartSec.setValue(Integer.toString(appointment.getStartAsOffsetDT().getSecond()));
        cbxEndHour.setValue(Integer.toString(appointment.getEndAsOffsetDT().getHour()));
        cbxEndMin.setValue(Integer.toString(appointment.getEndAsOffsetDT().getMinute()));
        cbxEndSec.setValue(Integer.toString(appointment.getEndAsOffsetDT().getSecond()));

        for (Customer customer : observableCustomers) {
            if (customer.getId() == appointment.getCustomerId()) {
                appointment.setCustomer(customer);
            }
        }

        for (User user : observableUsers) {
            if (user.getId() == appointment.getUserId()) {
                appointment.setUser(user);
            }
        }

        for (Contact contact : observableContacts) {
            if (contact.getContactId() == appointment.getContactId()) {
                appointment.setContact(contact);
            }
        }

        cbxCustomer.setValue(appointment.getCustomer());
        cbxUser.setValue(appointment.getUser());
        cbxContact.setValue(appointment.getContact());

    }

    protected void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    protected Appointment getAppointment() {
        try {
            appointment.setAppointmentId(Integer.parseInt(tbxAppointmentID.getText()));
        } catch (NumberFormatException | NullPointerException ex) {
            Dialogs.messageDialog("Invalid ID", "Appointment ID is invalid", true);
            return null;
        }

        appointment.setLastUpdatedBy(currentUser.getUsername());
        appointment.setLastUpdate(Instant.now().atOffset(ZoneOffset.UTC));
        return appointment;
    }

    protected boolean isNewRecord() {
        return true;
    }

    protected String getAction() {
        return "modify";
    }

    protected String getQuery() {
        return "UPDATE Appointments SET Title = ?, Description = ?, Location = ?, " +
                " Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, " +
                " Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
    }
}