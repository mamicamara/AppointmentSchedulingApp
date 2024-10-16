package com.appointmentscheduler.appointmentschedulingapp;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.util.Optional;

/**
 * The Dialog class contains two static methods; one display a message dialog, and the other
 * shows a confirm dialog.
 */
public class Dialogs {


    /**
     * Display a message with a header via a JavaFX's Alert pop-up.

     * @param message the message to display.
     * @param header the message header or sub-tittle.
     * @param isError indicates whether it is an error or not.
     */
    public static void messageDialog(String header, String message, boolean isError){
        Alert alert;
        if (isError) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ERROR");
        }else{
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("SUCCESS");
        }

        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a confirm dialog. Returns true if the Yes button is clicked or false when the
     * No button is clicked.
     *
     * @param action a string that identifies delete or cancel action.
     * @return true if the Yes button is clicked and false if the No button is clicked.
     */
    public static boolean confirmDialog(String action) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirm " + action);
        dialog.setContentText("Are you sure you want to " + action);
        ButtonType cancelButtonType = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType deleteButtonType = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        dialog.getDialogPane().getButtonTypes().add(cancelButtonType);
        dialog.getDialogPane().getButtonTypes().add(deleteButtonType);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES;
    }

    /**
     * Shows a confirm dialog. Returns true if the Yes button is clicked or false when the
     * No button is clicked.
     *
     * @param action a string that identifies delete or cancel action.
     * @return true if the Yes button is clicked and false if the No button is clicked.
     */
    public static boolean confirmDialog(String action, String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirm " + action);
        dialog.setContentText("Are you sure you want to " + action + " " + message);
        ButtonType cancelButtonType = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType deleteButtonType = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        dialog.getDialogPane().getButtonTypes().add(cancelButtonType);
        dialog.getDialogPane().getButtonTypes().add(deleteButtonType);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES;
    }

}
