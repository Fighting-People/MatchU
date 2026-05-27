package com.example.matchux;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudyCreateActivity extends AppCompatActivity {

    Button StudyCreateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* [임시 잠금 처리] 메인 화면과 하단 바를 먼저 띄우기 위해 에러 나는 코드를 안전하게 잠갔습니다.
        setContentView(R.layout.activity_studycreate);

        Button StudyCreate = findViewById(R.id.btnCreate);

        StudyCreate.setOnClickListener(v -> {
            String StudyName = ((EditText) findViewById(R.id.StudyName)).getText().toString();
            String Description = ((EditText) findViewById(R.id.Description)).getText().toString();
            int maxPeople = Integer.parseInt(((EditText) findViewById(R.id.SetMaxPeople)).getText().toString());
        });
        */
    }
}