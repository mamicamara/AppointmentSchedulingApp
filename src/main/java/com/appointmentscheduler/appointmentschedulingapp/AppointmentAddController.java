package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.Appointment;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.*;
import java.util.ResourceBundle;

public class AppointmentAddController extends AppointmentAbstractController {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            super.initialize(url, resourceBundle);
            tbxAppointmentID.setText("Auto-Generated");

        cbxStartHour.setValue("00");
        cbxStartMin.setValue("00");
        cbxStartSec.setValue("00");
        cbxEndHour.setValue("00");
        cbxEndMin.setValue("00");
        cbxEndSec.setValue("00");
    }

    protected Appointment getAppointment(){
        Appointment newAppointment= new Appointment();
        int appointmentID;
        try {
            // Generate customer ID
            appointmentID =  db.generateID("appointments", "Appointment_ID");
            newAppointment.setAppointmentId(appointmentID);
            newAppointment.setCreatedBy(currentUser.getUsername());
            newAppointment.setCreateDate(Instant.now().atOffset(ZoneOffset.UTC));
        } catch (SQLException e) {
            lblStatus.setText("Error: unable to generate ID");
            return null;
        }
        return newAppointment;
    }
    protected boolean isNewRecord(){
        return true;
    }
    protected String getAction(){
        return "add";
    }
    @Override
    protected String getQuery(){
        return "INSERT INTO appointments ( Title, Description,  Location, Type, Start, End, " +
                " Create_Date, Created_By, Customer_ID,  User_ID, Contact_ID, Appointment_ID ) " +
                " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
    }

}