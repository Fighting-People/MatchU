package com.example.matchux;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

   //<button 객체>
    Button createButton;
    Button categoryButton1;
    Button categoryButton2;
    Button categoryButton3;

    Button categoryButton4;

    //<listview 객체>
    ListView studyListView;

    String[] studies = {
            "첫번쨰 스터디 방 객체",
            "두번째 스터디 방 객체"
    };

    String[] category = {
            "공부 스터디방 1",
            "공부 스터디방 2"
    };

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        studyListView = findViewById(R.id.studyListView);
        createButton = findViewById(R.id.createButton);
        categoryButton1 = findViewById(R.id.categoryButton1);
        categoryButton2 = findViewById(R.id.categoryButton2);
        categoryButton3 = findViewById(R.id.categoryButton3);
        categoryButton4 = findViewById(R.id.categoryButton4);

        //스터디 리스트 뷰 어댑터 세팅
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,android.R.layout.simple_list_item_1,
                studies
        );


        //카테고리 리스트 뷰 어댑터 세팅
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                this,android.R.layout.simple_list_item_1,
                category
                        );

        //스터디 리스트 뷰 세팅
        studyListView.setAdapter(adapter);


        //스터디 생성 버튼 클릭

        createButton.setOnClickListener(v -> {
                    startActivity(new Intent(this, StudyCreateActivity.class));
                });
        //카테고리1 버튼 클릭
        categoryButton1.setOnClickListener(v -> {
            studyListView.setAdapter(adapter1);
            Toast.makeText(this, "공부 카테고리", Toast.LENGTH_SHORT).show();
                }
        );

        // 하단 네비게이션 바
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_home); // 현재 홈 선택 상태

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // 현재 화면이므로 그냥 유지
            } else if (id == R.id.nav_my_meeting) {
                //startActivity(new Intent(this, MyMeetingActivity.class));
                //return true;
            } else if (id == R.id.nav_profile) {
                //startActivity(new Intent(this, ProfileActivity.class));
                //return true;
            }
            return false;
        });
    };





};
