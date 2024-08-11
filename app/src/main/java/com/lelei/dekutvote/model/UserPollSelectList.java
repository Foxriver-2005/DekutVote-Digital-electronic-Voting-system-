package com.lelei.dekutvote.model;

public class UserPollSelectList {

    private String name ,docu1,docu2;
    private Integer limit,counts;

    public UserPollSelectList(String name,Integer counts,String docu1,String docu2,Integer limit) {
        this.name = name;
        this.counts = counts;
        this.docu1 = docu1;
        this.docu2 = docu2;
        this.limit = limit;

    }



    public String getName() {
        return name;


    }

    public Integer getCounts() {

        return counts;

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



    public void setText(String name,Integer counts) {
        this.name = name;
        this.counts = counts;


    }

}
