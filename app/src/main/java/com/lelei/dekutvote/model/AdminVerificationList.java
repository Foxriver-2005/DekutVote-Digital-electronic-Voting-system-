package com.lelei.dekutvote.model;
import java.util.Date;

public class AdminVerificationList {
    private String name;
    private  String id;
    private  String type;
    private  String cloudID;
    private String email;
    private Date date_registed;

    public AdminVerificationList(String cloudID, Date date_registed, String name, String id, String type, String email) {
        this.cloudID = cloudID;
        this.date_registed = date_registed;
        this.name = name;
        this.id = id;
        this.type = type;
        this.email = email;
    }
    public String getCloudID() {
        return cloudID;
    }

    public Date getDate_registed() {
        return date_registed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public void setText(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
}