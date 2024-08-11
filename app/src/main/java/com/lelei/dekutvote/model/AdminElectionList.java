package com.lelei.dekutvote.model;

import java.util.Map;

public class AdminElectionList {

    private  String position;
    private Integer limit;
    private Map<String,String> candidates,candidatesId,candidatesParty;
    private Map<String,Integer> limitFinal,tally,total;


    public AdminElectionList(String position,Integer limit, Map<String,String> candidates,Map<String,String> candidatesId,Map<String,String> candidatesParty,Map<String,Integer> limitFinal,Map<String,Integer> tally,Map<String,Integer> total) {
        this.position = position;
        this.limit = limit;
        this.candidates = candidates;
        this.candidatesId = candidatesId;
        this.candidatesParty = candidatesParty;
        this.limitFinal = limitFinal;
        this.tally = tally;
        this.total = total;
    }

    public String getPosition() {
        return position;
    }

    public Integer getLimit(){
        return limit;
    }


    public void setPosition(String position) {
        this.position = position;
    }


    public Map<String,String>  getCandidatesParty() {
        return candidatesParty;
    }
    public Map<String,String>  getCandidates() {
        return candidates;
    }
    public Map<String,String>  getCandidatesId() {
        return candidatesId;
    }
    public Map<String,Integer>  getLimitFinal() {
        return limitFinal;
    }
    public Map<String,Integer>  getTally() {
        return tally;
    }
    public Map<String,Integer>  getTotal() {
        return total;
    }

    public void setCandidates(Map<String,String> candidates) {
        this.candidates = candidates;
    }
}
