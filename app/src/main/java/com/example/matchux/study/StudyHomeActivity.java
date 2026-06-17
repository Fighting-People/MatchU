package com.example.matchux.study;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    Button postBtn, scheduleBtn, infoBtn, btnLeaveStudy; // 탈퇴 버튼 변수 등록
    RecyclerView recyclerView;
    LinearLayout infoLayout;
    FloatingActionButton writeBtn, chatBtn; // chatBtn 등록됨

    ArrayList<PostItem> postList;
    PostAdapter adapter;

    String studyId; // Firestore 문서 고유 ID를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_home);

        // 1. UI 컴포넌트 연결 (XML 파일의 ID와 정확히 매칭되어야 합니다)
        studyTitle = findViewById(R.id.studyTitle);
        postBtn = findViewById(R.id.postBtn);
        scheduleBtn = findViewById(R.id.scheduleBtn);
        infoBtn = findViewById(R.id.infoBtn);

        recyclerView = findViewById(R.id.recyclerView);
        infoLayout = findViewById(R.id.infoLayout);

        writeBtn = findViewById(R.id.writeBtn);
        chatBtn = findViewById(R.id.chatBtn); // <-- [누락되었던 연결 코드 추가]

        // infoLayout 내부에 배치한 탈퇴 버튼 연결
        btnLeaveStudy = findViewById(R.id.btnLeaveStudy);

        // 2. Intent 데이터 처리 및 안전장치
        studyId = getIntent().getStringExtra("studyId");
        String title = getIntent().getStringExtra("title");

        if (title != null) {
            studyTitle.setText(title);
        }

        // 인텐트로 넘어온 studyId가 없으면 로직 진행 시 크래시가 나므로 방어 처리
        if (studyId == null) {
            Toast.makeText(this, "모임 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. 임시 게시글 데이터 및 리사이클러뷰 세팅
        postList = new ArrayList<>();
        postList.add(new PostItem("첫 게시글", "안녕하세요"));
        postList.add(new PostItem("공지사항", "스터디 시간 변경"));

        adapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 4. 초기 화면 가시성 세팅 (게시글 탭 우선 노출)
        recyclerView.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.GONE);
        if (writeBtn != null) writeBtn.setVisibility(View.VISIBLE);

        // 5. 상단 탭 버튼 클릭 이벤트 처리
        postBtn.setOnClickListener(v -> {
            recyclerView.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.GONE);
            if (writeBtn != null) writeBtn.setVisibility(View.VISIBLE);
        });

        infoBtn.setOnClickListener(v -> {
            recyclerView.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
            if (writeBtn != null) writeBtn.setVisibility(View.GONE);
        });

        // 6. 모임 탈퇴 버튼 클릭 이벤트 및 다이얼로그 노출
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

        // 7. 기타 기능 리스너 공백 유지 (추후 구현 타겟)
        if (scheduleBtn != null) {
            scheduleBtn.setOnClickListener(v -> {
                // 나중에 ScheduleActivity 연결 예정
            });
        }

        if (chatBtn != null) {
            chatBtn.setOnClickListener(v -> {
                // 나중에 ChatActivity 연결 예정
            });
        }

        if (writeBtn != null) {
            writeBtn.setOnClickListener(v -> {
                // 나중에 WritePostActivity 연결 예정
            });
        }
    }

    // Firestore 'members' 배열에서 현재 로그인한 유저의 UID를 제거하는 프로세스
    private void leaveStudyGroup() {
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("Study").document(studyId)
                .update("members", FieldValue.arrayRemove(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudyHomeActivity.this, "모임에서 탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // 탈퇴 성공 후 내 모임 리스트 화면으로 복귀
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyHomeActivity.this, "탈퇴 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}