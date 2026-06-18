package com.example.matchux.study;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchux.R;
import com.example.matchux.post.PostAdapter;
import com.example.matchux.post.PostItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class StudyHomeActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView studyTitle;

    Button postBtn, scheduleBtn, infoBtn;

    RecyclerView recyclerView;

    LinearLayout infoLayout;

    FloatingActionButton writeBtn;

    FloatingActionButton chatBtn;

    ArrayList<PostItem> postList;
    PostAdapter adapter;

    String studyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_home);

        ImageButton btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(v -> {
            finish(); //
        });


        // 1. UI 컴포넌트 연결
        studyTitle = findViewById(R.id.studyTitle);

        postBtn = findViewById(R.id.postBtn);
        scheduleBtn = findViewById(R.id.scheduleBtn);
        infoBtn = findViewById(R.id.infoBtn);

        recyclerView = findViewById(R.id.recyclerView);

        infoLayout = findViewById(R.id.infoLayout);

        writeBtn = findViewById(R.id.writeBtn);
        chatBtn = findViewById(R.id.chatBtn);
        btnLeaveStudy = findViewById(R.id.btnLeaveStudy);

        // 2. Intent 데이터 처리
        studyId = getIntent().getStringExtra("studyId");
        chatBtn = findViewById(R.id.chatBtn);
        String title = getIntent().getStringExtra("title");

        if (title != null) {
            studyTitle.setText(title);
        }

        if (studyId == null) {
            Toast.makeText(this, "모임 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. 게시글 리사이클러뷰 세팅
        postList = new ArrayList<>();

        postList.add(new PostItem("첫 게시글", "안녕하세요"));
        postList.add(new PostItem("공지사항", "스터디 시간 변경"));

        // 어댑터 연결
        adapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 4. 초기 화면 가시성 세팅
        recyclerView.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.GONE);
        if (writeBtn != null) writeBtn.setVisibility(View.VISIBLE);

        // 5. 상단 탭 클릭 이벤트
        postBtn.setOnClickListener(v -> {

            recyclerView.setVisibility(View.VISIBLE);

            infoLayout.setVisibility(View.GONE);
            if (writeBtn != null) writeBtn.setVisibility(View.VISIBLE);
        });

        // 일정 버튼
        scheduleBtn.setOnClickListener(v -> {

            // 나중에 ScheduleActivity 연결 예정

        });

        // 정보 버튼
        infoBtn.setOnClickListener(v -> {

            recyclerView.setVisibility(View.GONE);

            infoLayout.setVisibility(View.VISIBLE);
            if (writeBtn != null) writeBtn.setVisibility(View.GONE);
        });

        // 6. 탈퇴 버튼 클릭
        if (btnLeaveStudy != null) {
            btnLeaveStudy.setOnClickListener(v -> {
                if (auth.getCurrentUser() == null) {
                    Toast.makeText(this, "로그인 정보가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(this)
                        .setTitle("모임 탈퇴")
                        .setMessage("정말 이 모임에서 탈퇴하시겠습니까?")
                        .setPositiveButton("탈퇴", (dialog, which) -> leaveStudyGroup())
                        .setNegativeButton("취소", null)
                        .show();
            });
        }
    }

    private void leaveStudyGroup() {
        String currentUserId = auth.getCurrentUser().getUid();
        db.collection("Study").document(studyId)
                .update("members", FieldValue.arrayRemove(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudyHomeActivity.this, "모임에서 탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyHomeActivity.this, "탈퇴 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
