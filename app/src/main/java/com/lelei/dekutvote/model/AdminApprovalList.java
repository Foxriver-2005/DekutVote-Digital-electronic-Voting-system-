package com.lelei.dekutvote.model;

import java.util.ArrayList;
import java.util.Date;

public class AdminApprovalList {

    private String title, id,description,type,creator;
    private Date time;
    // private

    public AdminApprovalList(String title, Date time, String id, String type, String creator) {
        this.title = title;
        this.time = time;
        this.id = id;
        this.type = type;
        this.creator = creator;


    }


    public String getId() {
        return id;


    }

    public String getTitle() {
        return title;


    }


    public Date getTime() {

        return time;

    }



    public String getType() {

        return type;
    }

    public String getCreator() {
        return creator;


    }

    public void setText(String title, Date time, String id, String description, ArrayList<String> polls_participated, ArrayList<String> allowed) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.id = id;

    }

}
