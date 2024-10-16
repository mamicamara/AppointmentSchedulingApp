package com.appointmentscheduler.appointmentschedulingapp;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;

public class Translator {
    public static final String STAGE_TITLE = "stage.title";
    public static final String LOGIN_FAIL = "login.fail";
    public static final String LOGIN_SUCCESS = "login.success";
    public static final String LOGIN_SQL_ERROR = "login.sql.error";
    public static final String LOGIN_IO_ERROR = "login.io.error";
    public static final String LOGIN_TITLE = "login.title";
    public static final String TIME_PREFIX = "login.time.prefix";
    public static final String  USERNAME_LABEL = "login.username.label";
    public static final String PASSWORD_LABEL = "login.password.label";
    public static final String SELECT_LANG= "login.select.lang";

    /** Stores the currently selected Locale. */
    private static final ObjectProperty<Locale> locale;

    static {
        //Initialize locale as the system's default Locale.
        locale = new SimpleObjectProperty<>(getDefaultLocale());
        //Add listener to the SimpleObjectProperty object locale so that the default
        //Locale is changed when user changes the locale.
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static List<Locale> getSupportedLocales(){
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.FRANCE));
    }

    /**
     * Returns the default locale; the systems default is used if it either ENGLISH or FRENCH;
     * otherwise ENGLISH is returned.
     *
     * @return default locale.
     */
    public static Locale getDefaultLocale() {
        return getSupportedLocales().contains(Locale.getDefault()) ? Locale.getDefault() : Locale.ENGLISH;
    }

    public static void setLocale(Locale _locale) {
        locale.set(_locale);
        Locale.setDefault(_locale);
    }


    public static Locale getLocale() {
        return locale.get();
    }

    /**
     * Returns a string of the message pointed to by the key 'MessageKey' for the
     * current locale.
     * @param MessageKey the message key
     * @return a localized string
     */
    public static String get(final String MessageKey) {
        return ResourceBundle.getBundle("messages", getLocale()).getString(MessageKey);
    }

    /**
     * Returns String binding of a localized string for the key 'messageKey'
     * which points to a specific message.
     */
    public static StringBinding getStringBinding(final String messageKey) {
        return Bindings.createStringBinding(() -> get(messageKey), locale);
    }

}
