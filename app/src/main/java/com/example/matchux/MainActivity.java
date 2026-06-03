package com.example.matchux;

<<<<<<< HEAD
// import com.google.firebase.firestore.FirebaseFirestore;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
=======
import android.content.Intent;
import android.os.Bundle;
>>>>>>> develop
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

  //  FirebaseFirestore db = FirebaseFirestore.getInstance();

    // <button 객체>
=======
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

    // Firestore 인스턴스 초기화
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 실제 데이터와 어댑터 선언
    List<Study> studyList = new ArrayList<>();
    StudyAdapter adapter;

    // UI 객체 선언
    Button btnAll;
>>>>>>> develop
    Button createButton;
    Button categoryButton1;
    Button categoryButton2;
    Button categoryButton3;
    Button categoryButton4;
<<<<<<< HEAD

    // <listview 객체>
    ListView studyListView;

    String[] studies = {
            "첫번째 스터디 방 객체",
            "두번째 스터디 방 객체"
    };

    String[] category = {
            "공부 스터디방 1",
            "공부 스터디방 2"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studyListView = findViewById(R.id.studyListView);
        createButton = findViewById(R.id.createButton);
=======
    ListView studyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. UI 객체들 ID 연결
        studyListView = findViewById(R.id.studyListView);
        createButton = findViewById(R.id.createButton);
        btnAll = findViewById(R.id.btnAll);
>>>>>>> develop
        categoryButton1 = findViewById(R.id.categoryButton1);
        categoryButton2 = findViewById(R.id.categoryButton2);
        categoryButton3 = findViewById(R.id.categoryButton3);
        categoryButton4 = findViewById(R.id.categoryButton4);

<<<<<<< HEAD
        // 스터디 리스트 뷰 어댑터 세팅
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,
                studies
        );

        // 카테고리 리스트 뷰 어댑터 세팅
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1,
                category
        );

        // 스터디 리스트 뷰 세팅
        studyListView.setAdapter(adapter);

        // 스터디 생성 버튼 클릭
=======
        // 2. 중요: 어댑터를 먼저 생성한 후 리스트뷰에 세팅 (순서 필수 변경됨)
        adapter = new StudyAdapter(this, studyList);
        studyListView.setAdapter(adapter);

        // 3. 앱 실행 시 최초 1회 전체 데이터 가져오기
        getStudiesFromFirestore(null);

        // 4. 스터디 생성 버튼 클릭 이벤트
>>>>>>> develop
        createButton.setOnClickListener(v -> {
            startActivity(new Intent(this, StudyCreateActivity.class));
        });

<<<<<<< HEAD
        // 카테고리1 버튼 클릭
        categoryButton1.setOnClickListener(v -> {
            studyListView.setAdapter(adapter1);
            Toast.makeText(this, "공부 카테고리", Toast.LENGTH_SHORT).show();
        });

        // [하단 네비게이션 바 설정]
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);

        // ★ 중요: 홈, 내 모임, 프로필 아이콘 원래의 검은색 모양을 그대로 살리는 코드 추가
        bottomNav.setItemIconTintList(null);

        bottomNav.setSelectedItemId(R.id.nav_home); // 현재 홈 선택 상태
=======
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
            // 필요 시 추가 구현 가능 영역
            getStudiesFromFirestore("음악");
        });

        categoryButton4.setOnClickListener(v -> {
            // 필요 시 추가 구현 가능 영역
            getStudiesFromFirestore("게임");
        });



        // 6. 하단 네비게이션 바 세팅
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home);
>>>>>>> develop

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
<<<<<<< HEAD
                return true; // 현재 화면이므로 그냥 유지
            } else if (id == R.id.nav_group) { // XML에 적은 '내 모임' ID와 똑같이 수정!
                // startActivity(new Intent(this, MyMeetingActivity.class));
                // return true;
            } else if (id == R.id.nav_profile) { // XML에 적은 '프로필' ID와 똑같이 수정!
                // startActivity(new Intent(this, ProfileActivity.class));
                // return true;
            }
            return false;
        });
=======
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_my_meeting) {
                // startActivity(new Intent(this, MyMeetingActivity.class));
                // return true;
            } else if (id == R.id.nav_profile) {
                 startActivity(new Intent(this, ProfileActivity.class));
                 return true;
            }
            return false;
        });
    } // onCreate 끝

    // Firestore 연동 데이터 획득 메서드 (오류 없이 온전히 분리됨)
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
>>>>>>> develop
    }
}