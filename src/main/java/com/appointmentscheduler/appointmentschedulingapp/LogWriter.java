package com.appointmentscheduler.appointmentschedulingapp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LogWriter {

    public static boolean saveLogin(boolean success, String username, String password) {
        FileWriter writer;
        try {
            writer = new FileWriter("login_activity.txt", true);
            String status = "LOGIN FAILED";
            if (success){
                status = "LOGIN SUCCESS";
            }

            writer.write((new Date()) + ", " + status + ", Username: " + username + ", Password: " + password + "\n");
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
