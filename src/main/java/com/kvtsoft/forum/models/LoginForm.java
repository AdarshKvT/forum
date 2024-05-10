package com.kvtsoft.forum.models;

public class LoginForm {

    private String username;
    private String password;

    // Default constructor
    public LoginForm() {
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
