package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.Appointment;
import com.appointmentscheduler.appointmentschedulingapp.models.Contact;
import com.appointmentscheduler.appointmentschedulingapp.models.Customer;
import com.appointmentscheduler.appointmentschedulingapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ResourceBundle;

public class AppointmentMainController implements Initializable {
    /***********************************************
     * FXML components instantiation
     ***********************************************/
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnAddAppointment, btnEditAppointment, btnDeleteAppointment, btnViewCustomers;
    @FXML
    private ComboBox cbxWeekMonthFilter;
    @FXML
    private RadioButton rbtnWeekFilter, rbtnMonthFilter;
    @FXML
    private TableView tblviewAppointments;
    @FXML
    private TableColumn tblColID, tblColTitle, tblColDescription, tblColLocation, tblColContact, tblColType,
            tblColStart, tblColEnd, tblColCustomer, tblColUser;

    /* tblColCreateDate, tblColLastUpdate, tblColCreatedBy, tblColUpdatedBy, */

    final ToggleGroup group = new ToggleGroup();

    /***********************************************
     * Private variable declarations
     ***********************************************/
    private User currentUser;
    private boolean fromLogin;
    private DatabaseService db = DatabaseService.getInstance();
    private ObservableList<Appointment> observableAppointments;

    private String[] weeks = new String[53];
    private String[] months = {"Select week by which to view", "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Retrieve appointments from the database
        try {
            observableAppointments = FXCollections.observableList(db.getAppointments());
        } catch (SQLException e) {
            Dialogs.messageDialog("Database Connection Error",
                    "Unable to retrieve appointments", true);
            System.out.println(e.getMessage());
        }

        bindAppointmentsTableView();

        if (fromLogin) {
            try {
                showCloseAppointment();
            } catch (SQLException e) {
                Dialogs.messageDialog("Database Connection Error",
                        "Unable to retrieve appointments", true);
                System.out.println(e.getMessage());
            }
            fromLogin = false;
        }

        createWeeks();

        rbtnWeekFilter.setToggleGroup(group);
        rbtnMonthFilter.setToggleGroup(group);

    }

    public void setFromLogin(boolean fromLogin){
        this.fromLogin = fromLogin;
    }

    /***********************************************
     * Handler functions for FXML components
     ***********************************************/
    @FXML
    protected void onAddAppointmentButtonClick(ActionEvent event) {
        openView(event,  "appointment-add-modify-view.fxml",  new AppointmentAddController());
    }

    @FXML
    protected void onEditAppointmentButtonClick(ActionEvent event) {
        // Get the selected index in the Appointments TableView 'tblviewAppointments'.
        Object appointment = tblviewAppointments.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            Dialogs.messageDialog("No Appointment Selected","You must select the appointment to modify", true);
            return;
        }
        AppointmentEditController appointmentEditController = new AppointmentEditController();
        appointmentEditController.setAppointment((Appointment)appointment);
        openView(event, "appointment-add-modify-view.fxml", appointmentEditController);
    }

    @FXML
    protected void onDeleteAppointmentButtonClick(ActionEvent event) {
        // Get the selected index in the Appointments TableView 'tblviewAppointments'.
        Object obj = tblviewAppointments.getSelectionModel().getSelectedItem();
        if (obj == null) {
            Dialogs.messageDialog("No Appointment Selected",
                    "You must select the appointment to delete", true);
            return;
        }

        Appointment appointment = (Appointment)obj;

        String message = "appointment of type '" + appointment.getType() + "' with the ID '" + appointment.getAppointmentId() + "'";

        if (Dialogs.confirmDialog("delete", message)){
            try {
                db.deleteAppointment(appointment);
                observableAppointments.remove(appointment);
            } catch (SQLException e) {
                Dialogs.messageDialog("Database Error","Unable to delete the appointment from database", true);            }
        }
    }

    @FXML
    protected void onViewCustomerBtnClick(ActionEvent event){
        openView(event, "customers-main-view.fxml", null);
    }

    @FXML
    protected void onReportsButtonClick(ActionEvent event){
        openView(event, "reports-view.fxml", null);
    }

    @FXML
    protected void onCloseButtonClick(ActionEvent event) {
        openView(event, "login-view.fxml", null);
    }

    /***********************************************
     * Private functions
     ***********************************************/

    private void showCloseAppointment() throws SQLException {
        String message = "There are no appointments within the next 15 minutes:";
        PreparedStatement preparedStatement = null;
        preparedStatement = db.getStatement("SELECT * FROM appointments WHERE " +
                " TIMESTAMPDIFF(MINUTE, ?, Start) > 0 AND " +
                " TIMESTAMPDIFF(MINUTE, ?, Start) < 15 ");

        // Get current UTC time based on system's settings.
        LocalDateTime currentTime = LocalDateTime.ofInstant(Instant.now(), Utility.getUTCOffset());
        preparedStatement.setTimestamp(1,  Timestamp.valueOf(currentTime));
        preparedStatement.setTimestamp(2, Timestamp.valueOf(currentTime));

        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()){
            message = "The following appointments are due in 15 minutes:\n";
            int id = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            Timestamp start = rs.getTimestamp("Start");
            Timestamp end = rs.getTimestamp("End");
            message += " - with ID " + Integer.toString(id) + ", titled '" + title + "': ";
            message += "starts at " + Utility.formatDate(Utility.utcToLocalOffsetTimeDate(start.toLocalDateTime()))  +
                    " & ends at " +  Utility.formatDate(Utility.utcToLocalOffsetTimeDate(end.toLocalDateTime()))  + ")\n";
            while(rs.next()){
                id = rs.getInt("Appointment_ID");
                title = rs.getString("Title");
                start = rs.getTimestamp("Start");
                end = rs.getTimestamp("End");
                message += " - with ID: " + Integer.toString(id) + ", titled '" + title + "': ";
                message += "starts at " + Utility.formatDate(Utility.utcToLocalOffsetTimeDate(start.toLocalDateTime())) +
                        " & ends at " + Utility.formatDate(Utility.utcToLocalOffsetTimeDate(end.toLocalDateTime())) + ")\n";
            }
        }
//        Dialogs.messageDialog("Appointments Alert", message, false);

        Dialog<String> dialog = new Dialog<String>();
        dialog.setTitle("Appointments Alert");
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        dialog.showAndWait();
    }

    private void openView(ActionEvent event, String viewName, Object controller) {
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(viewName));
            if (controller != null){
                loader.setController(controller);
            }

            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();
        } catch (IOException e) {
            lblStatus.setText("Unable to load view");
        }
    }

    public void bindAppointmentsTableView() {
        //Set the CellValueFactory for all columns of the tblviewAppointments TableView in appointment-main-view.fxml.
        tblColID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentId"));
        tblColTitle.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        tblColDescription.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        tblColLocation.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        tblColType.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        tblColStart.setCellValueFactory(new PropertyValueFactory<Appointment, String>("start"));
        tblColEnd.setCellValueFactory(new PropertyValueFactory<Appointment, String>("end"));
//        tblColCreateDate.setCellValueFactory(new PropertyValueFactory<Appointment, String>("createDate"));
//        tblColLastUpdate.setCellValueFactory(new PropertyValueFactory<Appointment, String>("lastUpdate"));
//        tblColCreatedBy.setCellValueFactory(new PropertyValueFactory<Appointment, String>("createdBy"));
//        tblColUpdatedBy.setCellValueFactory(new PropertyValueFactory<Appointment, String>("lastUpdatedBy"));
        tblColCustomer.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("customerId"));
        tblColUser.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("userId"));
        tblColContact.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("contactId"));

        FilteredList<Appointment> filteredAppointments = new FilteredList<>(observableAppointments, b -> true);

        cbxWeekMonthFilter.getSelectionModel().selectedIndexProperty().addListener((options, oldValue, newValue) -> {
            filteredAppointments.setPredicate(appointment -> {
                if (newValue == null){
                    return true;
                }

                int keyIndex = (int)newValue;

                if (keyIndex == 0) {
                    return true;
                }

                if (rbtnWeekFilter.isSelected()) {
                    int appointmentWeek = appointment.getStartAsOffsetDT().get(WeekFields.ISO.weekOfWeekBasedYear());

                    if (appointmentWeek == keyIndex){
                        return true;
                    }
                }
                else if (rbtnMonthFilter.isSelected()) {
                    int appointmentMonth = appointment.getStartAsOffsetDT().getMonthValue();

                    if (appointmentMonth == keyIndex    ){
                        return true;
                    }
                }else{
                    return true;
                }

                return false;
            });
        });

        SortedList<Appointment> sortedAppointments = new SortedList<>(filteredAppointments);

        // Bind sorted result with table view
        sortedAppointments.comparatorProperty().bind(tblviewAppointments.comparatorProperty());

        // Apply filtered and sorted appointments to the Tableview.
        tblviewAppointments.setItems(sortedAppointments);
    }

    @FXML
    protected void filterByWeek(ActionEvent event){
        setWeeksMonthsCbx("Select week by which to view");
    }

    @FXML
    protected void filterByMonth(ActionEvent event){
        setWeeksMonthsCbx("Select month by which to view");
    }

    protected void setWeeksMonthsCbx(String prompt) {
        if (cbxWeekMonthFilter.isDisable()){
            cbxWeekMonthFilter.setDisable(false);
        }

        if (rbtnWeekFilter.isSelected()) {
            cbxWeekMonthFilter.getItems().setAll(weeks);
        }
        else if (rbtnMonthFilter.isSelected()) {
            cbxWeekMonthFilter.getItems().setAll(months);
        }

        cbxWeekMonthFilter.getSelectionModel().selectFirst();
    }

    private void createWeeks(){
        weeks[0] = "Select week by which to view";

        for (int i=1; i<=52; i++){
            weeks[i] = "Week " + i;
        }
    }

}