package com.example.matchux.study;

import java.util.List;

public class Study {
    // 1. Firestore DB 필드와 일치하는 멤버 변수 선언
    public String studyId;
    public String category;
    public boolean finished;      // DB의 finished와 일치
    public int maxPeople;         // DB의 maxPeople과 일치
    public String description;    // DB의 description와 일치
    public String studyName;      // DB의 studyName과 일치
    public List<String> members;  // DB의 members(배열)와 일치

    // 2. 빈 생성자 (Firestore 객체 매핑용 필수)
    public Study() {}

    // 3. 매개변수가 있는 생성자
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

    // 4. Getter 및 Setter (안전한 데이터 접근용)
    public String getStudyId() { return studyId; }
    public void setStudyId(String studyId) { this.studyId = studyId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) { this.finished = finished; }

    public int getMaxPeople() { return maxPeople; }
    public void setMaxPeople(int maxPeople) { this.maxPeople = maxPeople; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStudyName() { return studyName; }
    public void setStudyName(String studyName) { this.studyName = studyName; }

    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
}