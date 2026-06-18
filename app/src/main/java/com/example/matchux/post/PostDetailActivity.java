package com.example.matchux.post;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.matchux.R;

public class PostDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        ImageButton btnBack = findViewById(R.id.btnBackPostDetail);
        TextView tvTitle = findViewById(R.id.tvDetailPostTitle);
        TextView tvContent = findViewById(R.id.tvDetailPostContent);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");

        tvTitle.setText(title);
        tvContent.setText(content);

        btnBack.setOnClickListener(v -> finish());
    }
}
