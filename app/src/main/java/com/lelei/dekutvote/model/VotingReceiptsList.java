package com.lelei.dekutvote.model;

import java.util.ArrayList;
import java.util.Date;

public class VotingReceiptsList {

    private String name, id;
    private Date time;


    public VotingReceiptsList(String id,String name, Date time) {
        this.id = id;

        this.name = name;
        this.time = time;

    }

    public String getId() {

        return id;
    }


    public String getName() {
        return name;

    }



    public Date getTime() {

        return time;

    }







}
