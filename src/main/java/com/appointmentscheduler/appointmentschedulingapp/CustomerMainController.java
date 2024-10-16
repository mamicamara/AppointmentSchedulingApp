package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.Customer;
import com.appointmentscheduler.appointmentschedulingapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerMainController implements Initializable {
    /***********************************************
     * FXML components instantiation
     ***********************************************/
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnAddCustomer, btnEditCustomer, btnDeleteCustomer;
    @FXML
    private TableView tblviewCustomers;
    @FXML
    private TableColumn tblColID, tblColName, tblColAddress, tblColPostalCode,
            tblColPhone,tblColCreateDate, tblColCreatedBy, tblColLastUpdate,
            tblColUpdatedBy, tblColDivisionID;

    /***********************************************
     * Private variable declarations
     ***********************************************/
    private User currentUser;
    private DatabaseService db = DatabaseService.getInstance();
    private ObservableList<Customer> observableCustomers;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Retrieve customers from the database
        try {
            List<Customer> customers = db.getCustomers();

            observableCustomers = FXCollections.observableList(db.getCustomers());
        } catch (SQLException e) {
            Dialogs.messageDialog("Database Connection Error",
                    "Unable to retrieve customers", true);        }
        bindCustomersTableView();
    }

    /***********************************************
     * Handler functions for FXML components
     ***********************************************/
    @FXML
    protected void onAddCustomerButtonClick(ActionEvent event) {
        openView(event,  "customer-add-modify-view.fxml",  new CustomerAddController());
    }

    @FXML
    protected void onEditCustomerButtonClick(ActionEvent event) {

        // Get the selected index in the Customers TableView 'tblviewCustomers'.
        Object customer = tblviewCustomers.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Dialogs.messageDialog("No Customer Selected","You must select the customer to modify", true);
            return;
        }
        CustomerEditController customerEditController = new CustomerEditController();
        customerEditController.setCustomer((Customer)customer);
        openView(event, "customer-add-modify-view.fxml", customerEditController);
    }

    @FXML
    protected void onDeleteCustomerButtonClick(ActionEvent event) {
        // Get the selected index in the Customers TableView 'tblviewCustomers'.
        Object customer = tblviewCustomers.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Dialogs.messageDialog("No Customer Selected",
                    "You must select the customer to delete", true);
            return;
        }

        if (Dialogs.confirmDialog("delete")){
            try {
                db.deleteCustomer((Customer) customer);
                observableCustomers.remove(customer);
            } catch (SQLIntegrityConstraintViolationException e) {
                Dialogs.messageDialog("Constraint Error","The customer has an appointment record", true);
            } catch (SQLException e) {
                Dialogs.messageDialog("Database Error","Unable to delete the customer from database", true);
            }
        }



    }

    @FXML
    protected void onCloseButtonClick(ActionEvent event) {
        AppointmentMainController appointmentController = new AppointmentMainController();
        appointmentController.setFromLogin(false);
        openView(event, "appointments-main-view.fxml", appointmentController);
    }

    /***********************************************
     * Private functions
     ***********************************************/
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

    private void bindCustomersTableView() {
        //Set the CellValueFactory for all columns of the tblviewCustomers TableView in customer-main-view.fxml.
        tblColID.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        tblColName.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        tblColAddress.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        tblColPostalCode.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
        tblColPhone.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));
        tblColCreateDate.setCellValueFactory(new PropertyValueFactory<Customer, String>("createDate"));
        tblColCreatedBy.setCellValueFactory(new PropertyValueFactory<Customer, Date>("createdBy"));
        tblColLastUpdate.setCellValueFactory(new PropertyValueFactory<Customer, String>("lastUpdate"));
        tblColUpdatedBy.setCellValueFactory(new PropertyValueFactory<Customer, Timestamp>("lastUpdatedBy"));
        tblColDivisionID.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("divisionId"));

        // Link the customers observable list to the customers TableView.
        tblviewCustomers.setItems(observableCustomers);
    }

}