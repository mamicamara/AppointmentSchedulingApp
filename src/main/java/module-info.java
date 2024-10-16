module com.appointmentscheduler.appointmentschedulingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.appointmentscheduler.appointmentschedulingapp to javafx.fxml;
    exports com.appointmentscheduler.appointmentschedulingapp;
    exports com.appointmentscheduler.appointmentschedulingapp.models;
    opens com.appointmentscheduler.appointmentschedulingapp.models to javafx.fxml;
}