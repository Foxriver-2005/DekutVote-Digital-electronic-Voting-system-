package com.lelei.dekutvote.model;

public class ManageUsersList {
    private String name;
    private  String id;
    private  String type;
    private  String cloudID;
    private String email;

    public ManageUsersList(String cloudID,String name,String id,String type,String email) {
        this.cloudID = cloudID;
        this.name = name;
        this.id = id;
        this.type = type;
        this.email = email;
    }
    public String getCloudID() {
        return cloudID;
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