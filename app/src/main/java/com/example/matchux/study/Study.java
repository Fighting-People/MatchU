package com.example.matchux.study;

public class Study {
    public String category;
    public boolean is_finished;
    public int maxMember;
    public String studyDescription;
    public String studyTitle;

    public Study() {}

    public Study(String category, boolean is_finished, int maxMember,
                 String studyDescription, String studyTitle) {
        this.category = category;
        this.is_finished = is_finished;
        this.maxMember = maxMember;
        this.studyDescription = studyDescription;
        this.studyTitle = studyTitle;
    }
}

