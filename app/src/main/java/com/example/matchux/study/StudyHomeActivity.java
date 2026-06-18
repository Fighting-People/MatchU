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
import java.util.List;

public class StudyHomeActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    TextView studyTitle;
    Button postBtn, scheduleBtn, infoBtn, btnLeaveStudy, btnJoinStudy; // 🌟 참여 버튼 추가
    RecyclerView recyclerView;
    LinearLayout infoLayout;
    FloatingActionButton writeBtn, chatBtn;

    ArrayList<PostItem> postList;
    PostAdapter adapter;

    String studyId;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_home);

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인 정보가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = auth.getCurrentUser().getUid();

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
        btnJoinStudy = findViewById(R.id.btnJoinStudy); // 🌟 XML에 추가할 참여 버튼 연결

        // 2. Intent 데이터 처리
        studyId = getIntent().getStringExtra("studyId");
        String title = getIntent().getStringExtra("title");

        if (title != null) {
            studyTitle.setText(title);
        }

        if (studyId == null) {
            Toast.makeText(this, "모임 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. 🌟 현재 유저의 참여 여부 체크 및 버튼 제어
        checkParticipationStatus();

        // 4. 게시글 리사이클러뷰 세팅
        postList = new ArrayList<>();
        postList.add(new PostItem("첫 게시글", "안녕하세요"));
        postList.add(new PostItem("공지사항", "스터디 시간 변경"));

        adapter = new PostAdapter(postList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 5. 초기 화면 가시성 세팅
        recyclerView.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.GONE);
        if (writeBtn != null) writeBtn.setVisibility(View.VISIBLE);

        // 6. 상단 탭 클릭 이벤트
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

        // 7. 탈퇴 버튼 클릭
        if (btnLeaveStudy != null) {
            btnLeaveStudy.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("모임 탈퇴")
                        .setMessage("정말 이 모임에서 탈퇴하시겠습니까?")
                        .setPositiveButton("탈퇴", (dialog, which) -> leaveStudyGroup())
                        .setNegativeButton("취소", null)
                        .show();
            });
        }

        // 8. 🌟 참여 버튼 클릭 이벤트 구현
        if (btnJoinStudy != null) {
            btnJoinStudy.setOnClickListener(v -> joinStudyGroup());
        }
    }

    // 🌟 참여 여부 확인 로직
    private void checkParticipationStatus() {
        db.collection("Study").document(studyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> members = (List<String>) documentSnapshot.get("members");

                        if (members != null && members.contains(currentUserId)) {
                            // 이미 멤버인 경우: 탈퇴 버튼만 노출, 참여 버튼 숨김
                            if (btnLeaveStudy != null) btnLeaveStudy.setVisibility(View.VISIBLE);
                            if (btnJoinStudy != null) btnJoinStudy.setVisibility(View.GONE);

                            // 참여자만 게시글 작성 및 기능을 이용할 수 있도록 제어
                            if (postBtn != null) postBtn.setEnabled(true);
                            if (scheduleBtn != null) scheduleBtn.setEnabled(true);
                        } else {
                            // 멤버가 아닌 경우: 참여 버튼 노출, 탈퇴 버튼 숨김
                            if (btnLeaveStudy != null) btnLeaveStudy.setVisibility(View.GONE);
                            if (btnJoinStudy != null) btnJoinStudy.setVisibility(View.VISIBLE);

                            // 미참여자는 게시글 탭 등을 비활성화하거나 안내 레이아웃 처리 권장
                            if (postBtn != null) postBtn.setEnabled(false);
                            if (scheduleBtn != null) scheduleBtn.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "상태 확인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // 🌟 모임 참여 프로세스 (members 배열에 추가)
    private void joinStudyGroup() {
        db.collection("Study").document(studyId)
                .update("members", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudyHomeActivity.this, "모임에 성공적으로 참여했습니다!", Toast.LENGTH_SHORT).show();
                    checkParticipationStatus(); // 상태 새로고침하여 버튼 스위칭
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyHomeActivity.this, "참여 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // 모임 탈퇴 프로세스 (members 배열에서 제거)
    private void leaveStudyGroup() {
        db.collection("Study").document(studyId)
                .update("members", FieldValue.arrayRemove(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudyHomeActivity.this, "모임에서 탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                    checkParticipationStatus(); // 상태 새로고침하여 버튼 스위칭
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyHomeActivity.this, "탈퇴 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}