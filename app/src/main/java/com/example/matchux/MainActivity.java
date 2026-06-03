package com.example.matchux;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager; // 🌟 추가
import androidx.recyclerview.widget.RecyclerView; // 🌟 추가

import com.example.matchux.profile.ProfileActivity;
import com.example.matchux.study.Study;
import com.example.matchux.study.StudyAdapter;
import com.example.matchux.study.StudyCreateActivity;
import com.example.matchux.study.StudyHomeActivity; // 🌟 내 모임 창 연결용 추가
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Firestore 인스턴스 초기화
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 실제 데이터와 어댑터 선언
    List<Study> studyList = new ArrayList<>();
    StudyAdapter adapter;

    // UI 객체 선언
    Button btnAll;
    Button createButton;
    Button categoryButton1;
    Button categoryButton2;
    Button categoryButton3;
    Button categoryButton4;

    // 🌟 ListView에서 RecyclerView로 변경
    RecyclerView studyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. UI 객체들 ID 연결
        studyListView = findViewById(R.id.studyListView);
        createButton = findViewById(R.id.createButton);
        btnAll = findViewById(R.id.btnAll);
        categoryButton1 = findViewById(R.id.categoryButton1);
        categoryButton2 = findViewById(R.id.categoryButton2);
        categoryButton3 = findViewById(R.id.categoryButton3);
        categoryButton4 = findViewById(R.id.categoryButton4);

        // 🌟 2. 리사이클러뷰 필수 세팅: LayoutManager 지정 및 어댑터 연결
        studyListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudyAdapter(studyList);
        studyListView.setAdapter(adapter);

        // 3. 앱 실행 시 최초 1회 전체 데이터 가져오기
        getStudiesFromFirestore(null);

        // 4. 스터디 생성 버튼 클릭 이벤트
        createButton.setOnClickListener(v -> {
            startActivity(new Intent(this, StudyCreateActivity.class));
        });

        // 5. 카테고리 버튼 클릭 이벤트 세팅
        btnAll.setOnClickListener(v -> {
            getStudiesFromFirestore(null); // 필터 없이 전부 가져오기
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

        // 6. 하단 네비게이션 바 세팅
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // 현재 화면이 메인이므로 다시 띄울 필요 없이 주석 처리하거나 유지
                return true;
            } else if (id == R.id.nav_my_meeting) {
                // 🌟 2번 단계에서 만든 '내 모임 창(StudyHomeActivity)'으로 이동 연동!
                startActivity(new Intent(this, StudyHomeActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    } // onCreate 끝

    // Firestore 연동 데이터 획득 메서드
    // Firestore 연동 데이터 획득 메서드
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
                    studyList.add(study);
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "에러 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }); // 👈 여기서 소괄호와 중괄호가 온전하게 닫혀야 합니다.
    } // 👈 getStudiesFromFirestore 메서드를 닫는 중괄호

    @Override
    protected void onResume() {
        super.onResume();
        // 화면으로 돌아올 때마다 데이터를 최신으로 새로고침함
        getStudiesFromFirestore(null);
    }
} // 👈 !!! 중요 !!! MainActivity 클래스 전체를 닫는 최외곽 중괄호