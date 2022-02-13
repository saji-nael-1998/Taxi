package com.example.taxi.modal;

import android.text.TextUtils;
import android.util.Patterns;

public class Driver {
    private String userID;
    private String driverName;
    private String email;
    private String pass;
    private String phoneNO;
    private String imagePath;
    private String birthdate;
    private String ID;
    private String city;
    private String street;
    private Taxi taxi;

    public Driver() {
    }

    public Driver(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhoneNO() {
        return phoneNO;
    }

    public void setPhoneNO(String phoneNO) {
        this.phoneNO = phoneNO;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Taxi getTaxi() {
        return taxi;
    }

    public void setTaxi(Taxi taxi) {
        this.taxi = taxi;
    }

    public int isValidEmail() {
        if (TextUtils.isEmpty(email)) {
            return 0;
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return -1;
            }
        }
        return 1;
    }

    public int isValidPassword() {
        if (TextUtils.isEmpty(pass)) {
            return 0;
        } else {
            if (pass.length() < 6) {
                return -1;
            }
        }
        return 1;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "userID='" + userID + '\'' +
                ", driverName='" + driverName + '\'' +
                ", email='" + email + '\'' +
                ", pass='" + pass + '\'' +
                ", phoneNO='" + phoneNO + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", ID='" + ID + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", taxi=" + taxi +
                '}';
    }
}
