package com.example.matchux;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.profile.ProfileActivity;
import com.example.matchux.study.Study;
import com.example.matchux.study.StudyAdapter;
import com.example.matchux.study.StudyCreateActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 1. Firestore 및 데이터 변수 선언
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Study> studyList = new ArrayList<>();
    StudyAdapter adapter;

    // 2. UI 객체 선언
    Button btnAll;
    Button createButton;
    Button categoryButton1;
    Button categoryButton2;
    Button categoryButton3;
    Button categoryButton4;
    ListView studyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 3. UI 객체들 ID 연결
        studyListView = findViewById(R.id.studyListView);
        createButton = findViewById(R.id.createButton);
     //   btnAll = findViewById(R.id.btnAll);
        categoryButton1 = findViewById(R.id.categoryButton1);
        categoryButton2 = findViewById(R.id.categoryButton2);
        categoryButton3 = findViewById(R.id.categoryButton3);
        categoryButton4 = findViewById(R.id.categoryButton4);

        // 4. 중요: 어댑터를 먼저 생성한 후 리스트뷰에 세팅
        adapter = new StudyAdapter(this, studyList);
        studyListView.setAdapter(adapter);

        // 5. 앱 실행 시 최초 1회 전체 데이터 가져오기
        getStudiesFromFirestore(null);

        // 6. 스터디 생성 버튼 클릭 이벤트
        createButton.setOnClickListener(v -> {
            startActivity(new Intent(this, StudyCreateActivity.class));
        });

        // 7. 카테고리 및 전체보기 버튼 클릭 이벤트 세팅
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

        // 8. 하단 네비게이션 바 설정 (아이콘 색상 유지 및 ID 매칭 완료)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setItemIconTintList(null); // 홈, 내 모임, 프로필 아이콘 원래 모양 유지
        bottomNav.setSelectedItemId(R.id.nav_home); // 현재 홈 선택 상태

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // 현재 화면 유지
            } else if (id == R.id.nav_group) { // 아까 메뉴 파일에서 정한 '내 모임' ID
                // startActivity(new Intent(this, MyMeetingActivity.class));
                // return true;
            } else if (id == R.id.nav_profile) { // 아까 메뉴 파일에서 정한 '프로필' ID
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

    } // onCreate 끝

    // 9. Firestore 연동 데이터 획득 메서드
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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면으로 돌아올 때마다 데이터를 새로고침함
        getStudiesFromFirestore(null);
    }
}