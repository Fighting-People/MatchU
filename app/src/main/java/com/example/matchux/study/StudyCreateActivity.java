package com.example.matchux.study;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudyCreateActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studycreate);

        // 1. 카테고리 목록 정의
        String[] categories = {"공부", "운동", "음악", "게임"};

        Spinner spinner = findViewById(R.id.spinnerCategory);
        Button StudyCreate = findViewById(R.id.btnCreate);

        // 2. 어댑터를 이용해 목록을 스피너에 입힘
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 3. 버튼 클릭 시 데이터 수집
        StudyCreate.setOnClickListener(v -> {
            String StudyName = ((EditText) findViewById(R.id.StudyName)).getText().toString();
            String Description = ((EditText) findViewById(R.id.Description)).getText().toString();
            String maxPeopleStr = ((EditText) findViewById(R.id.SetMaxPeople)).getText().toString();

            // 스피너에서 선택된 값 가져오기
            String selectedCategory = spinner.getSelectedItem().toString();

            if (StudyName.isEmpty() || maxPeopleStr.isEmpty()) {
                Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxPeople = Integer.parseInt(maxPeopleStr);

            // 선택된 카테고리(selectedCategory)를 넣어서 생성
            Study study = new Study(null, selectedCategory, false, maxPeople, Description, StudyName, null);

            db.collection("Study")
                    .add(study)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "스터디 생성 완료", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "생성 실패", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
