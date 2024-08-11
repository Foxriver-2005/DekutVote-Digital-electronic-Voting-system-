package com.lelei.dekutvote.model;

public class CreateElectionList {

    private String candidName, candidID,candidInfo, candidParty;

    public CreateElectionList(String candidName,String candidID,String candidInfo,String candidParty) {
        this.candidName = candidName;
        this.candidID = candidID;
        this.candidInfo = candidInfo;
        this.candidParty = candidParty;

    }



    public String getCandidName() {
        return candidName;


    }

    public String getCandidID() {
        return candidID;


    }
    public String getCandidInfo() {
        return candidInfo;


    }
    public String getCandidParty() {
        return candidParty;


    }


    public void setText(String choice) {
        // this.choice = choice;

    }

}
