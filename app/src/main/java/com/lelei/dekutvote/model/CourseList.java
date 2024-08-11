package com.lelei.dekutvote.model;

public class CourseList {

    private  String course;
    private String department;
    private Boolean isChecked;


    public CourseList(String course,String department, Boolean isChecked) {
        this.course = course;
        this.department = department;
        this.isChecked = isChecked;
    }

    public String getCourse() {
        return course;
    }

    public String getDepartment() {
        return department;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }


    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }


}
