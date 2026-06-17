package com.example.matchux.study;

import java.util.List;

public class Study {
    public String studyId;
    public String category;
    public boolean finished;      // DB의 finished와 일치
    public int maxPeople;         // DB의 maxPeople과 일치
    public String description;    // DB의 description와 일치
    public String studyName;      // DB의 studyName과 일치
    public List<String> members;  // DB의 members(배열)와 일치

    public Study() {}

    public Study(String studyId, String category, boolean finished, int maxPeople,
                 String description, String studyName, List<String> members) {
        this.studyId = studyId;
        this.category = category;
        this.finished = finished;
        this.maxPeople = maxPeople;
        this.description = description;
        this.studyName = studyName;
        this.members = members;
    }
}