package com.example.taxi.modal;

public class Trip {
    private String date;
    private String start;
    private String end;
    private String dest;
    private String src;
    private String totalRider;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTotalRider() {
        return totalRider;
    }

    public void setTotalRider(String totalRider) {
        this.totalRider = totalRider;
    }
}
