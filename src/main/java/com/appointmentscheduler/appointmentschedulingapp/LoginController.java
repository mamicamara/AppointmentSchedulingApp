package com.appointmentscheduler.appointmentschedulingapp;

import com.appointmentscheduler.appointmentschedulingapp.models.DataSingleton;
import com.appointmentscheduler.appointmentschedulingapp.models.Division;
import com.appointmentscheduler.appointmentschedulingapp.models.User;
import com.appointmentscheduler.appointmentschedulingapp.models.UserSingleton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.sql.*;

import java.net.URL;
import javafx.util.Callback;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class LoginController implements Initializable {
    /***********************************************
     * FXML components instantiation
     ***********************************************/
    @FXML
    private Label lblStatus, lblLocation, lblTitle,
            lblTimePrefix, lblUsername, lblPassword;
    @FXML
    private TextField tbxUsername;
    @FXML
    private PasswordField pbxPassword;
//    @FXML
//    private ComboBox<Locale> cbxSelectLanguage;
    @FXML
    private Button btnLogin;


    /***********************************************
     * Private variable declarations
     ***********************************************/
    private DatabaseService db = DatabaseService.getInstance();
    private Translator translator = new Translator();


    /***********************************************
     * Handler functions for FXML components
     ***********************************************/
    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        boolean loggedin = false;
        String username = tbxUsername.getText().trim();
        String password = pbxPassword.getText();
        try {
            User user = db.getUser(username, password);
            if (user != null) {
                loggedin = true;
                UserSingleton userSingleton = UserSingleton.getInstance();
                userSingleton.setUser(user);
                openView(event);
            }
            else {
                lblStatus.textProperty().bind(Translator.getStringBinding(Translator.LOGIN_FAIL));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            lblStatus.textProperty().bind(Translator.getStringBinding(Translator.LOGIN_SQL_ERROR));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            lblStatus.textProperty().bind(Translator.getStringBinding(Translator.LOGIN_IO_ERROR));
        }

        if (!LogWriter.saveLogin(loggedin, username, password)) {
            Dialogs.messageDialog("Logging Error", "Unable to save logs to file", true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String timezoneId = TimeZone.getDefault().getID();
        ZoneId zoneId = ZoneId.of(timezoneId);
        Locale locale = Translator.getDefaultLocale();
        lblLocation.setText( "" + zoneId.getDisplayName(TextStyle.SHORT, locale));
        bindLanguage();
    }

    /***********************************************
     * Private functions
     ***********************************************/
    private void openView(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments-main-view.fxml"));
        AppointmentMainController appointmentController = new AppointmentMainController();
        appointmentController.setFromLogin(true);
        loader.setController(appointmentController);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void bindLanguage(){
        lblTitle.textProperty().bind(Translator.getStringBinding(Translator.LOGIN_TITLE));
        lblTimePrefix.textProperty().bind(Translator.getStringBinding(Translator.TIME_PREFIX));
        lblUsername.textProperty().bind(Translator.getStringBinding(Translator.USERNAME_LABEL));
        lblPassword.textProperty().bind(Translator.getStringBinding(Translator.PASSWORD_LABEL));
        btnLogin.textProperty().bind(Translator.getStringBinding(Translator.LOGIN_TITLE));

    }

}