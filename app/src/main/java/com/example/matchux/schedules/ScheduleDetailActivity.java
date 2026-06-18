package com.example.matchux.schedules;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.matchux.R;

public class ScheduleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_detail);

        ImageButton btnBack = findViewById(R.id.btnBackScheduleDetail);
        TextView tvDate = findViewById(R.id.tvDetailScheduleDate);
        TextView tvTitle = findViewById(R.id.tvDetailScheduleTitle);
        TextView tvContent = findViewById(R.id.tvDetailScheduleContent);

        String date = getIntent().getStringExtra("date");
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");

        tvDate.setText(date);
        tvTitle.setText(title);
        tvContent.setText(content);

        btnBack.setOnClickListener(v -> finish());
    }
}
