package com.lelei.dekutvote.model;

import java.util.ArrayList;
import java.util.Date;

public class UserVoteList {

    private String title, id,allowed,creator_id,creator_name;
    private Date time,start,end;
    private ArrayList<String> elections_participated;

    public UserVoteList(String title,String allowed,Date time,Date start,Date end, String id, ArrayList<String> elections_participated,String creator_id,String creator_name) {
        this.title = title;
        this.allowed = allowed;
        this.time = time;
        this.start = start;
        this.end = end;
        this.id = id;
        this.elections_participated = elections_participated;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
    }

    public ArrayList<String> getList() {
        return elections_participated;


    }

    public String getTitle() {
        return title;


    }

    public String getAllowed() {
        return allowed;


    }

    public Date getStart() {

        return start;

    }

    public Date getEnd() {

        return end;

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

    public void setText(String title,Date time,Date start,Date end,String id, ArrayList<String> elections_participated) {
        this.title = title;
        this.time = time;
        this.start = start;
        this.end = end;
        this.id = id;
        this.elections_participated = elections_participated;
    }

}
