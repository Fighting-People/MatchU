package com.example.matchux.study;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StudyCreateActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studycreate);

        ImageButton btnBack = findViewById(R.id.btnBack3);
        btnBack.setOnClickListener(v -> {

        });

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

            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. 현재 생성하는 유저(방장)의 UID를 참여 멤버 리스트에 첫 번째로 추가
            String myUid = auth.getCurrentUser().getUid();
            List<String> membersList = new ArrayList<>();
            membersList.add(myUid);

            int maxPeople = Integer.parseInt(maxPeopleStr);

            // [수정] 통일된 Study 생성자 규격(총 7개 인자)에 완벽하게 매칭
            // 처음 생성할 때는 문서 ID가 없으므로 첫 인자에 null을 넘깁니다.
            Study study = new Study(null, selectedCategory, false, maxPeople, Description, StudyName, membersList);

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