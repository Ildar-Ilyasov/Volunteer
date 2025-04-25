package com.example.volunteer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class User {

    private String firstName;
    private String lastName;
    private String dob;
    private String email;
    private String login;
    private String password;
    private String registrationDate;
    private String profileImageBase64;  // Новое поле для хранения аватарки в Base64

    public User() {
    }

    public User(String firstName, String lastName, String dob, String email, String login, String password, String registrationDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.email = email;
        this.login = login;
        this.password = password;
        this.registrationDate = registrationDate;
    }

    // Геттеры и сеттеры
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    // Новый геттер и сеттер для аватарки
    public String getProfileImageBase64() {
        return profileImageBase64;
    }

    public void setProfileImageBase64(String profileImageBase64) {
        this.profileImageBase64 = profileImageBase64;
    }
}