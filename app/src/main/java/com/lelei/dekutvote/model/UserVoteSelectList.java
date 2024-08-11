package com.lelei.dekutvote.model;

public class UserVoteSelectList {

    private String position,name, details,docu1,docu2,party;
    private Integer limit;
    Boolean hasVoted;

    public UserVoteSelectList(String position,String name,String details,String docu1,String docu2,Integer limit,Boolean hasVoted) {
        this.position = position;
        this.name = name;
        this.details = details;
        this.party = party;
        this.docu1 = docu1;
        this.docu2 = docu2;
        this.limit = limit;
        this.hasVoted = hasVoted;

    }


    public String getPosition() {
        return position;


    }


    public String getName() {
        return name;


    }

    public String getDetails() {

        return details;

    }

    public String getParty() {

        return party;

    }

    public String getDocu1() {
        return docu1;


    }

    public String getDocu2() {
        return docu2;


    }

    public Integer getLimit() {
        return limit;


    }
    public Boolean getHasVoted() {
        return hasVoted;


    }




    public void setHasVoted(Boolean hasVoted) {
        this.hasVoted = hasVoted;

    }

}
