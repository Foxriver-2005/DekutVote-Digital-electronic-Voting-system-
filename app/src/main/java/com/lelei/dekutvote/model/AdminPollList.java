package com.lelei.dekutvote.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminPollList {

    private  String question;
    private Integer limit;
    private ArrayList<String> choices;
    private HashMap<String,Integer> limitFinal,tally,total;

    public AdminPollList(String question,Integer limit, ArrayList<String> choices,HashMap<String,Integer> limitFinal,HashMap<String,Integer> tally,HashMap<String,Integer> total) {
        this.question = question;
        this.limit = limit;
        this.choices = choices;
        this.limitFinal = limitFinal;
        this.tally = tally;
        this.total = total;
    }

    public String getQuestion() {
        return question;
    }

    public Integer getLimit(){
        return limit;
    }

    public void setPosition(String position) {
        this.question = question;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }
    public HashMap<String,Integer> getLimitFinal() {
        return limitFinal;
    }
    public HashMap<String,Integer>  getTally() {
        return tally;
    }
    public Map<String,Integer>  getTotal() {
        return total;
    }

    public void setCandidates(Map<String,String> candidates) {
        this.choices = choices;
    }
}
