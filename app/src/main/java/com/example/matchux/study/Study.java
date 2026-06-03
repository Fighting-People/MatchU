package com.example.matchux.study;

import java.util.List;

public class Study {
    private String category;
    private boolean isFinished; // 혹은 기존 상태 변수
    private int maxPeople;
    private String description;
    private String studyName;
    private List<String> members; // 🌟 1. 참여 멤버 UID 리스트 필드 추가

    // 빈 생성자 (Firestore가 데이터를 읽어올 때 필수)
    public Study() {}

    // 🌟 2. 생성자에 List<String> members 항목을 추가합니다.
    public Study(String category, boolean isFinished, int maxPeople, String description, String studyName, List<String> members) {
        this.category = category;
        this.isFinished = isFinished;
        this.maxPeople = maxPeople;
        this.description = description;
        this.studyName = studyName;
        this.members = members;
    }

    // 🌟 3. Getter와 Setter 추가
    public String getCategory() { return category; }
    public boolean isFinished() { return isFinished; }
    public int getMaxPeople() { return maxPeople; }
    public String getDescription() { return description; }
    public String getStudyName() { return studyName; }
    public List<String> getMembers() { return members; }

    public void setMembers(List<String> members) { this.members = members; }
}