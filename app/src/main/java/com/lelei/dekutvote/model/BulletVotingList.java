package com.lelei.dekutvote.model;

public class BulletVotingList {

    private String party_name,id,election_name;
    private Boolean isPartylist;

    public BulletVotingList(String id,String party_name,String election_name,Boolean isPartylist) {
        this.id = id;
        this.party_name = party_name;
        this.isPartylist = isPartylist;

    }


    public String getId() {
        return id;


    }

    public String getParty_name() {
        return party_name;


    }
    public String getElection_name(){
        return election_name;
    }

    public Boolean getIsPartylist() {
        return isPartylist;

    }


    public void setParty_name(String party_name) {
        this.party_name = party_name;

    }

}
