package com.example.matchux.study;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.matchux.R;

public class StudyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_detail); // 상세 정보 레이아웃

        // 1. Intent로 넘어온 모임 정보 캐치
        String name = getIntent().getStringExtra("studyName");
        String desc = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        int maxPeople = getIntent().getIntExtra("maxPeople", 0);

        // 2. XML 텍스트뷰에 데이터 바인딩
        TextView tvName = findViewById(R.id.detailTitle);
        TextView tvDesc = findViewById(R.id.detailDescription);
        TextView tvInfo = findViewById(R.id.detailInfo);

        tvName.setText(name);
        tvDesc.setText(desc);
        tvInfo.setText("카테고리: " + category + " | 제한 인원: " + maxPeople + "명");
    }
}