package com.lelei.dekutvote.model;

import java.util.ArrayList;
import java.util.Date;

public class UserPollList {

    private String title, id,description,creator_id,creator_name,allowed;
    private Date time;
    private ArrayList <String> polls_participated;

    public UserPollList(String title, Date time, String id, String description, ArrayList<String> polls_participated,String allowed,String creator_id, String creator_name) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.id = id;
        this.polls_participated = polls_participated;
        this.allowed = allowed;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
    }

    public ArrayList<String> getList() {
        return polls_participated;


    }
    public String getAllowed() {
        return allowed;

    }

    public String getTitle() {
        return title;


    }
    public String getDescription() {

        return description;

    }



    public Date getTime() {

        return time;

    }

    public String getId() {

        return id;
    }

    public String getCreator_id() {
        return creator_id;


    }
    public String getCreator_name() {

        return creator_name;

    }

    public void setText(String title, Date time, String id, String description, ArrayList<String> polls_participated, String allowed) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.id = id;
        this.polls_participated = polls_participated;
        this.allowed = allowed;
    }

}
