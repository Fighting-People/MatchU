package com.example.matchux;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    // UI 객체 선언 (중복 제거)
    Button btnAll;
    Button createButton;
    Button categoryButton1;
    Button categoryButton2;
    Button categoryButton3;
    Button categoryButton4;
    RecyclerView studyListView; // RecyclerView로 변경 완료

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 컴포넌트 연결
        studyListView = findViewById(R.id.studyListView);
        createButton = findViewById(R.id.createButton);
        btnAll = findViewById(R.id.btnAll);
        categoryButton1 = findViewById(R.id.categoryButton1);
        categoryButton2 = findViewById(R.id.categoryButton2);
        categoryButton3 = findViewById(R.id.categoryButton3);
        categoryButton4 = findViewById(R.id.categoryButton4);

        // 리사이클러뷰 세팅
        studyListView.setLayoutManager(new LinearLayoutManager(this));

        // 기존에 생성자 인자 개수가 어떻게 정의되었는지에 맞춰 호출 (보통 context와 list 전달)
        adapter = new StudyAdapter(this, studyList);
        studyListView.setAdapter(adapter);

        // 초기 데이터 로드
        getStudiesFromFirestore(null);

        // 스터디 생성 버튼 클릭
        createButton.setOnClickListener(v -> startActivity(new Intent(this, StudyCreateActivity.class)));

        // 카테고리 버튼 클릭 이벤트 세팅
        btnAll.setOnClickListener(v -> {
            getStudiesFromFirestore(null);
            Toast.makeText(this, "전체 목록을 보여줍니다.", Toast.LENGTH_SHORT).show();
        });

        categoryButton1.setOnClickListener(v -> {
            getStudiesFromFirestore("공부");
            Toast.makeText(this, "공부 카테고리", Toast.LENGTH_SHORT).show();
        });

        categoryButton2.setOnClickListener(v -> {
            getStudiesFromFirestore("운동");
            Toast.makeText(this, "운동 카테고리", Toast.LENGTH_SHORT).show();
        });

        categoryButton3.setOnClickListener(v -> {
            getStudiesFromFirestore("음악");
            Toast.makeText(this, "음악 카테고리", Toast.LENGTH_SHORT).show();
        });

        categoryButton4.setOnClickListener(v -> {
            getStudiesFromFirestore("게임");
            Toast.makeText(this, "게임 카테고리", Toast.LENGTH_SHORT).show();
        });

        // 하단 네비게이션 바 세팅 및 리스너 등록
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_my_meeting) {
                // 내 모임 리스트 화면(MyStudyActivity)으로 부드럽게 이동
                startActivity(new Intent(this, MyStudyActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // 프로필 화면으로 부드럽게 이동
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        }); // 👈 꼬여있던 세미콜론과 괄호 정비 완료
    }

    // Firestore 데이터 획득 메서드
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
                    study.studyId = doc.getId();
                    studyList.add(study);
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "에러: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면으로 돌아올 때마다 데이터 새로고침
        getStudiesFromFirestore(null);
    }
}