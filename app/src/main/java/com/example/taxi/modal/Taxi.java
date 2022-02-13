package com.example.taxi.modal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Taxi {
    private String taxiID;
    private String taxiBrand;
    private String taxiYear;
    private String taxiPlateNO;
    private Date reqistrationDate;
    private Route route;

    public String getTaxiID() {
        return taxiID;
    }

    public void setTaxiID(String taxiID) {
        this.taxiID = taxiID;
    }

    public String getTaxiBrand() {
        return taxiBrand;
    }

    public void setTaxiBrand(String taxiBrand) {
        this.taxiBrand = taxiBrand;
    }

    public String getTaxiYear() {
        return taxiYear;
    }

    public void setTaxiYear(String taxiYear) {
        this.taxiYear = taxiYear;
    }

    public String getTaxiPlateNO() {
        return taxiPlateNO;
    }

    public void setTaxiPlateNO(String taxiPlateNO) {
        this.taxiPlateNO = taxiPlateNO;
    }

    public Date getReqistrationDate() {
        return reqistrationDate;
    }

    public void setReqistrationDate(String reqistrationDateString) {

        try {
            this.reqistrationDate = new SimpleDateFormat("yyyy-mm-dd").parse(reqistrationDateString);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
