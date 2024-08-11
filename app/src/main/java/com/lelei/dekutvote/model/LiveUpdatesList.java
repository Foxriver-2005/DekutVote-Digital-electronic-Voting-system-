package com.lelei.dekutvote.model;

public class LiveUpdatesList {

    private String name,docu, votes,position;


    public LiveUpdatesList(String name,String docu,String votes,String position) {
        this.name = name;
        this.docu = docu;
        this.votes = votes;
        this.position = position;


    }



    public String getName() {
        return name;


    }
    public String getDocu() {
        return docu;


    }

    public String getVotes() {

        return votes;

    }

    public String getPosition() {
        return position;


    }





    public void setText(String name,String docu,String votes,String position) {
        this.name = name;
        this.docu = docu;
        this.votes = votes;
        this.position = position;


    }

}
