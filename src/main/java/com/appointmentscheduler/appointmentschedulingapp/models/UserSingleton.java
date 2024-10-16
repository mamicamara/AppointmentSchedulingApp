package com.appointmentscheduler.appointmentschedulingapp.models;

public class UserSingleton {
    private User user;

    private final static UserSingleton INSTANCE = new UserSingleton();
    private UserSingleton(){}

    public static UserSingleton getInstance(){
        return INSTANCE;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
