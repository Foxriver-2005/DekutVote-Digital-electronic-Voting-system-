package com.lelei.dekutvote.model;

public class CreatePollList {

    private String choice, time;

    public CreatePollList(String choice) {
        this.choice = choice;

    }



    public String getChoice() {
        return choice;


    }


    public void setText(String choice) {
        this.choice = choice;

    }

}
