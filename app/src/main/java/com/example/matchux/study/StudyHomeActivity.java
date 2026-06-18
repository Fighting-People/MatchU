package com.example.matchux.study;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchux.R;
import com.example.matchux.post.PostAdapter;
import com.example.matchux.post.PostItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class StudyHomeActivity extends AppCompatActivity {

    TextView studyTitle;

    Button postBtn, scheduleBtn, infoBtn;

    RecyclerView recyclerView;

    LinearLayout infoLayout;

    FloatingActionButton writeBtn;

    FloatingActionButton chatBtn;

    ArrayList<PostItem> postList;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_home);

        // 연결
        studyTitle = findViewById(R.id.studyTitle);

        postBtn = findViewById(R.id.postBtn);
        scheduleBtn = findViewById(R.id.scheduleBtn);
        infoBtn = findViewById(R.id.infoBtn);

        recyclerView = findViewById(R.id.recyclerView);

        infoLayout = findViewById(R.id.infoLayout);

        writeBtn = findViewById(R.id.writeBtn);

        chatBtn = findViewById(R.id.chatBtn);
        String title = getIntent().getStringExtra("title");

        if(title != null){
            studyTitle.setText(title);
        }

        // 게시글 리스트
        postList = new ArrayList<>();

        postList.add(new PostItem("첫 게시글", "안녕하세요"));
        postList.add(new PostItem("공지사항", "스터디 시간 변경"));

        // 어댑터 연결
        adapter = new PostAdapter(postList);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);

        // 처음 화면
        recyclerView.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.GONE);

        // 게시글 버튼
        postBtn.setOnClickListener(v -> {

            recyclerView.setVisibility(View.VISIBLE);

            infoLayout.setVisibility(View.GONE);

        });

        // 일정 버튼
        scheduleBtn.setOnClickListener(v -> {

            // 나중에 ScheduleActivity 연결 예정

        });

        // 정보 버튼
        infoBtn.setOnClickListener(v -> {

            recyclerView.setVisibility(View.GONE);

            infoLayout.setVisibility(View.VISIBLE);

        });

        //채팅창 버튼
         chatBtn.setOnClickListener(v -> {

            // 나중에 ChatActivity 연결 예정

        });

        // 게시글 작성 버튼
        writeBtn.setOnClickListener(v -> {

            // 나중에 WritePostActivity 연결 예정

        });

    }
}
