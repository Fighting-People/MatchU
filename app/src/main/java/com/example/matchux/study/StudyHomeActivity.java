package com.example.matchux.study;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchux.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StudyHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudyAdapter adapter;
    private List<Study> myStudyList;

    // 🌟 각 스터디 객체와 매칭되는 문서 ID들을 순서대로 저장할 리스트
    private List<String> studyIdList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 실제 파일명이 activity_my_meeting 일 때
        setContentView(R.layout.activity_study_home);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // 1. 리사이클러뷰 및 리스트 초기화
        recyclerView = findViewById(R.id.recyclerViewMyStudies); // 💡 XML의 리사이클러뷰 ID와 매칭
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myStudyList = new ArrayList<>();
        studyIdList = new ArrayList<>(); // 문서 ID 저장용 리스트 초기화

        adapter = new StudyAdapter(myStudyList);
        recyclerView.setAdapter(adapter);

        // 2. 🌟 리사이클러뷰 아이템 클릭 이벤트 리스너 설정
        adapter.setOnItemClickListener(new StudyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Study study, String documentId) {
                // 클릭한 아이템의 인덱스를 통해 정확한 Firestore 문서 ID를 가져옵니다.
                int position = myStudyList.indexOf(study);
                String clickedStudyId = studyIdList.get(position);

                // 상세 정보 화면(또는 모임 대시보드)으로 이동
                Intent intent = new Intent(StudyHomeActivity.this, StudyDetailActivity.class);

                // 중요: 3, 4번 단계(게시물/일정 쿼리)를 위해 고유 문서 ID를 무조건 넘겨줍니다.
                intent.putExtra("studyId", clickedStudyId);
                intent.putExtra("studyName", study.getStudyName());
                intent.putExtra("description", study.getDescription());
                intent.putExtra("category", study.getCategory());
                intent.putExtra("maxPeople", study.getMaxPeople());

                startActivity(intent);
            }
        });

        // 3. 내가 가입한 모임 데이터 로드
        loadMyStudies();
    }

    private void loadMyStudies() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 로그인한 사용자의 UID 확보
        String myUid = auth.getCurrentUser().getUid();

        // 🌟 핵심 쿼리: "Study" 컬렉션의 "members" 배열 필드 안에 내 UID가 들어있는 문서만 쏙 필터링
        db.collection("Study")
                .whereArrayContains("members", myUid)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        myStudyList.clear();  // 기존 데이터 리셋
                        studyIdList.clear();  // 기존 ID 리셋

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 1) 문서 ID(랜덤 문자열)를 추출하여 리스트에 순서대로 저장
                            String studyId = document.getId();
                            studyIdList.add(studyId);

                            // 2) 문서를 Study 객체 데이터 모델로 전환하여 리스트에 추가
                            Study study = document.toObject(Study.class);
                            myStudyList.add(study);
                        }

                        // 4. 어댑터에 데이터가 동기화되었음을 알리고 화면을 새로고침
                        adapter.notifyDataSetChanged();

                        // 참여 중인 모임방이 아예 없을 때의 예외 처리
                        if (myStudyList.isEmpty()) {
                            Toast.makeText(StudyHomeActivity.this, "참여 중인 모임이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("FirestoreError", "내 모임 로드 실패: ", task.getException());
                        Toast.makeText(StudyHomeActivity.this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}