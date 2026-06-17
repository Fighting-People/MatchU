package com.example.matchux.study;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.MainActivity;
import com.example.matchux.R;
import com.example.matchux.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyStudyActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    List<Study> myStudyList = new ArrayList<>();
    StudyAdapter adapter;
    ListView myStudyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_study); // activity_main.xml 구조와 동일하게 바텀네비와 리스트뷰 매핑 필요

        myStudyListView = findViewById(R.id.myStudyListView);
        adapter = new StudyAdapter(this, myStudyList);
        myStudyListView.setAdapter(adapter);

        // 내 모임 리스트 클릭 시 상세 데이터 바인딩을 위해 StudyHomeActivity로 이동
        myStudyListView.setOnItemClickListener((parent, view, position, id) -> {
            Study clickedStudy = myStudyList.get(position);
            Intent intent = new Intent(MyStudyActivity.this, StudyHomeActivity.class);
            // 클릭 리스너 부분 studyTitle -> studyName으로 수정
            intent.putExtra("studyId", clickedStudy.studyId);
            intent.putExtra("title", clickedStudy.studyName); // clickedStudy.studyTitle에서 변경
            startActivity(intent);
        });

        getMyStudiesFromFirestore();

        // 하단 네비게이션 처리
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_my_meeting);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_my_meeting) {
                return true; // 현재 화면 유지
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void getMyStudiesFromFirestore() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        // 사용자가 가입할 때 'members'라는 ArrayList 필드에 UID를 넣었다고 가정 시
        db.collection("Study")
                .whereArrayContains("members", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    myStudyList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Study study = doc.toObject(Study.class);
                        if (study != null) {
                            study.studyId = doc.getId();
                            myStudyList.add(study);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}