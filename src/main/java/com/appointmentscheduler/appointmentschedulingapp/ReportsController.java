package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
//
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.ListView;


public class ReportsController implements Initializable {

    /***********************************************
     * FXML components instantiation
     ***********************************************/

    @FXML
    private Button btnBack;

    @FXML
    private Label lblStatus;

    // --------------------------------------------------
    // Contact schedules ComboBox, Tableview and columns.
    // --------------------------------------------------

    @FXML
    private ComboBox<Contact> cbxConsultants;

    @FXML
    private TableView tblviewSchedules;

    @FXML
    private TableColumn tblColTitle, tblColID, tblColType, tblColDescription, tblColStart, tblColEnd, tblColCustomerID;

    // -------------------------------------------------
    // Customer appointments Tableview and columns
    // --------------------------------------------------

    @FXML
    private TableView tblviewAppointments;

    @FXML
    private TableColumn<AppointmentCount, String>tblColAppointmentMonth, tblColAppointmentType;

    @FXML
    private TableColumn<AppointmentCount, Integer> tblColTotalAppointments;

    // -------------------------------------------------
    // Division's customers Tableview and columns
    // --------------------------------------------------

    @FXML
    private TableView tblviewDivisionCustomers;

    @FXML
    private TableColumn<CustomerCount, String> tblColDivision;

    @FXML
    private TableColumn<CustomerCount, Integer> tblColTotalCustomers;

    // Database service for retrieving records from database.
    private DatabaseService db = DatabaseService.getInstance();

    /**
     * Initializes the components on the reports screen; fills contacts ComboBox,
     * binds and populate tableviews.
     *
     * @param url The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resourceBundle The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            fillContactsComboBox();
            bindContactTableView();
            customerAppointmentsReport();
            divisionCustomersReport();
        } catch (SQLException ex) {
            lblStatus.setText("Error connecting to the database");
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    protected void onBackButtonClick(ActionEvent event) {
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments-main-view.fxml"));
            AppointmentMainController appointmentController = new AppointmentMainController();
            appointmentController.setFromLogin(false);
            loader.setController(appointmentController);
            stage.setScene(new Scene(loader.load()));
            stage.centerOnScreen();

        } catch (IOException e) {
            lblStatus.setText("Unable to load view");
            System.out.println(e.getMessage());
        }
    }

    private void fillContactsComboBox() throws SQLException {
        // Create a factory for the user ComboBox for populating the combobox.
        Callback<ListView<Contact>, ListCell<Contact>> cbxFactory = lv -> new ListCell<Contact>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                setText(empty ? "" : contact.getName());
            }
        };

        cbxConsultants.setCellFactory(cbxFactory);
        cbxConsultants.setButtonCell(cbxFactory.call(null));
        cbxConsultants.getItems().addAll(FXCollections.observableList(db.getContacts()));
    }

    private void bindContactTableView() throws SQLException {

        List<Appointment> appointments = db.getAppointments();

        ObservableList<Appointment> observableSchedules = FXCollections.observableList(appointments);

        tblColID.setCellValueFactory(new PropertyValueFactory<Schedule, String>("appointmentId"));
        tblColTitle.setCellValueFactory(new PropertyValueFactory<Schedule, LocalDateTime>("title"));
        tblColType.setCellValueFactory(new PropertyValueFactory<Schedule, String>("type"));
        tblColDescription.setCellValueFactory(new PropertyValueFactory<Schedule, String>("description"));
        tblColStart.setCellValueFactory(new PropertyValueFactory<Schedule, String>("start"));
        tblColEnd.setCellValueFactory(new PropertyValueFactory<Schedule, String>("end"));
        tblColCustomerID.setCellValueFactory(new PropertyValueFactory<Schedule, String>("customerId"));

        FilteredList<Appointment> filteredAppointments = new FilteredList<>(observableSchedules, b -> false);

        cbxConsultants.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            filteredAppointments.setPredicate(appointment -> {
                if (newValue == null){
                    return false;
                }

                Contact contact = newValue;

                if (contact.getContactId() == appointment.getContactId()){
                    return true;
                }

                return false;
            });
        });

        // Link the filtered appointments list to the appointments TableView.
        tblviewSchedules.setItems(filteredAppointments);

    }

    public void customerAppointmentsReport() throws SQLException {
        List<AppointmentCount> appointmentsByMonthType =  db.countAppointmentsByTypeMonth();

        ObservableList<AppointmentCount> observableAppointmentsByMT = FXCollections.observableList(appointmentsByMonthType);

        tblColAppointmentMonth.setCellValueFactory(new PropertyValueFactory<AppointmentCount, String>("Month"));
        tblColAppointmentType.setCellValueFactory(new PropertyValueFactory<AppointmentCount, String>("Type"));
        tblColTotalAppointments.setCellValueFactory(new PropertyValueFactory<AppointmentCount, Integer>("Total"));

        // Link the appointments observable list to the appointments TableView.
        tblviewAppointments.setItems(observableAppointmentsByMT);

    }


    public void divisionCustomersReport() throws SQLException {
        List<CustomerCount> divisionCustomers = db.countDivisionCustomers();

        ObservableList<CustomerCount> observableDivisionCustomers = FXCollections.observableList(divisionCustomers);

        tblColDivision.setCellValueFactory(new PropertyValueFactory<CustomerCount, String>("Division"));
        tblColTotalCustomers.setCellValueFactory(new PropertyValueFactory<CustomerCount, Integer>("Total"));

        // Link the division customer's observable list to the division customers TableView.
        tblviewDivisionCustomers.setItems(observableDivisionCustomers);

    }


}