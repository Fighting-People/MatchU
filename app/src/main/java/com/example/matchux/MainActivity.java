package com.example.matchux;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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

        //스터디 방 생성 버튼 클릭
        createButton.setOnClickListener(v -> {

            Toast.makeText(this, "스터디방 생성", Toast.LENGTH_SHORT).show();}
        );
        //카테고리1 버튼 클릭
        categoryButton1.setOnClickListener(v -> {
            studyListView.setAdapter(adapter1);
            Toast.makeText(this, "공부 카테고리", Toast.LENGTH_SHORT).show();
                }
        );
    };





};
