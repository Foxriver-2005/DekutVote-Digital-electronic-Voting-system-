package com.lelei.dekutvote.model;

public class LogsList {
    private String date;
    private  String description;
    private Boolean isHeader;

    public LogsList(String date,String description,Boolean isHeader) {
        this.date = date;
        this.description = description;
        this.isHeader = isHeader;
    }
    public Boolean getIsHeader() {
        return isHeader;
    }


    public String getDate() {
        return date;
    }



    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String date,String description) {

        this.description = description;
    }
}