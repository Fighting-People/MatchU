package com.example.matchux;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.profile.ProfileActivity;
import com.example.matchux.study.MyStudyActivity;
import com.example.matchux.study.Study;
import com.example.matchux.study.StudyAdapter;
import com.example.matchux.study.StudyCreateActivity;
import com.example.matchux.study.StudyHomeActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Study> studyList = new ArrayList<>();
    StudyAdapter adapter;

    Button btnAll, createButton, categoryButton1, categoryButton2, categoryButton3, categoryButton4;
    ListView studyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studyListView = findViewById(R.id.studyListView);
        createButton = findViewById(R.id.createButton);
        btnAll = findViewById(R.id.btnAll);
        categoryButton1 = findViewById(R.id.categoryButton1);
        categoryButton2 = findViewById(R.id.categoryButton2);
        categoryButton3 = findViewById(R.id.categoryButton3);
        categoryButton4 = findViewById(R.id.categoryButton4);

        adapter = new StudyAdapter(this, studyList);
        studyListView.setAdapter(adapter);

        // [추가] 리스트 항목 클릭 시 상세 정보 및 게시판(StudyHomeActivity)으로 이동
        studyListView.setOnItemClickListener((parent, view, position, id) -> {
            Study clickedStudy = studyList.get(position);
            Intent intent = new Intent(MainActivity.this, StudyHomeActivity.class);
            // 클릭 리스너 부분 studyTitle -> studyName으로 수정
            intent.putExtra("studyId", clickedStudy.studyId);
            intent.putExtra("title", clickedStudy.studyName); // clickedStudy.studyTitle에서 변경
            startActivity(intent);
        });

        getStudiesFromFirestore(null);

        createButton.setOnClickListener(v -> startActivity(new Intent(this, StudyCreateActivity.class)));

        btnAll.setOnClickListener(v -> getStudiesFromFirestore(null));
        categoryButton1.setOnClickListener(v -> getStudiesFromFirestore("공부"));
        categoryButton2.setOnClickListener(v -> getStudiesFromFirestore("운동"));
        categoryButton3.setOnClickListener(v -> getStudiesFromFirestore("음악"));
        categoryButton4.setOnClickListener(v -> getStudiesFromFirestore("게임"));

        // 하단 네비게이션 설정 변경
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 현재 홈이므로 아무것도 하지 않음 (중복 실행 방지)
                return true;
            } else if (itemId == R.id.nav_my_meeting) {
                startActivity(new Intent(this, MyStudyActivity.class));
                overridePendingTransition(0, 0); // 화면 전환 애니메이션 제거로 부드럽게 이동
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void getStudiesFromFirestore(String categoryName) {
        Query query = db.collection("Study");
        if (categoryName != null) {
            query = query.whereEqualTo("category", categoryName);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            studyList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Study study = doc.toObject(Study.class);
                if (study != null) {
                    study.studyId = doc.getId(); // 문서 ID 확보
                    studyList.add(study);
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "에러: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudiesFromFirestore(null);
    }
}