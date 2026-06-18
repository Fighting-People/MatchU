package com.example.matchux.study;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.matchux.post.PostDetailActivity;
import com.example.matchux.post.PostItem;
import com.example.matchux.post.WritePostActivity;
import com.example.matchux.schedules.ScheduleAdapter;
import com.example.matchux.schedules.ScheduleDetailActivity;
import com.example.matchux.schedules.ScheduleItem;
import com.example.matchux.schedules.WriteScheduleActivity;
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

    Button postBtn, scheduleBtn, infoBtn, btnLeaveStudy,btnJoinStudy;
    RecyclerView recyclerView;

    LinearLayout infoLayout;

    FloatingActionButton writeBtn;



    ArrayList<PostItem> postList;
    PostAdapter postAdapter;

    ArrayList<ScheduleItem> scheduleList;
    ScheduleAdapter scheduleAdapter;

    String studyId;
    String currentUserId;

    private enum Tab { POSTS, SCHEDULE, INFO }
    Tab currentTab = Tab.POSTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_home);

        ImageButton btnBack = findViewById(R.id.btnBack2);
        btnBack.setOnClickListener(v -> {
            finish(); //
        });


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

        btnLeaveStudy = findViewById(R.id.btnLeaveStudy);
        btnJoinStudy = findViewById(R.id.btnJoinStudy); // XML에 추가할 참여 버튼 연결

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

        // 3. 현재 유저의 참여 여부 체크 및 버튼 제어
        checkParticipationStatus();

        // 4. 리사이클러뷰 및 어댑터 초기화
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        postAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("title", item.getTitle());
            intent.putExtra("content", item.getContent());
            startActivity(intent);
        });

        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(scheduleList);
        scheduleAdapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, ScheduleDetailActivity.class);
            intent.putExtra("date", item.getDate());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("content", item.getContent());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter); // 기본은 게시글 어댑터

        // 5. 초기 데이터 로드 (게시글)
        loadPosts();

        // 6. 초기 화면 가시성 세팅
        recyclerView.setVisibility(View.VISIBLE);
        infoLayout.setVisibility(View.GONE);
        if (writeBtn != null) writeBtn.setVisibility(View.VISIBLE);

        // 7. 상단 탭 클릭 이벤트
        postBtn.setOnClickListener(v -> {
            currentTab = Tab.POSTS;
            recyclerView.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.GONE);
            if (writeBtn != null) {
                writeBtn.setVisibility(View.VISIBLE);
                // 버튼 색상 변경 (게시글임을 명시)
                writeBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#87CEEB")));
            }
            
            recyclerView.setAdapter(postAdapter);
            loadPosts();
        });

        // 일정 버튼
        scheduleBtn.setOnClickListener(v -> {
            currentTab = Tab.SCHEDULE;
            recyclerView.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.GONE);
            if (writeBtn != null) {
                writeBtn.setVisibility(View.VISIBLE);
                // 버튼 색상 변경 (일정임을 명시 - 보라색 계열로 차별화)
                writeBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#6200EE")));
            }

            recyclerView.setAdapter(scheduleAdapter);
            loadSchedules();
        });

        // 정보 버튼
        infoBtn.setOnClickListener(v -> {
            currentTab = Tab.INFO;
            recyclerView.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
            if (writeBtn != null) writeBtn.setVisibility(View.GONE);
        });

        // 8. 탈퇴 버튼 클릭
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

        // 8. 🌟 참여 버튼 클릭 이벤트 구현
        if (btnJoinStudy != null) {
            btnJoinStudy.setOnClickListener(v -> joinStudyGroup());
        }

        // 9. 🌟 글쓰기 버튼 클릭 (탭에 따라 다른 Activity 연결)
        if (writeBtn != null) {
            writeBtn.setOnClickListener(v -> {
                Intent intent;
                if (currentTab == Tab.POSTS) {
                    intent = new Intent(this, WritePostActivity.class);
                } else {
                    intent = new Intent(this, WriteScheduleActivity.class);
                }
                intent.putExtra("studyId", studyId);
                startActivity(intent);
            });
        }
    }

    private void loadPosts() {
        db.collection("Posts")
                .whereEqualTo("studyId", studyId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        PostItem item = doc.toObject(PostItem.class);
                        if (item != null) postList.add(item);
                    }
                    postAdapter.notifyDataSetChanged();
                });
    }

    private void loadSchedules() {
        db.collection("Schedule")
                .whereEqualTo("studyId", studyId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    scheduleList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        ScheduleItem item = doc.toObject(ScheduleItem.class);
                        if (item != null) scheduleList.add(item);
                    }
                    scheduleAdapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면에 다시 돌아올 때 데이터 새로고침
        if (currentTab == Tab.POSTS) loadPosts();
        else if (currentTab == Tab.SCHEDULE) loadSchedules();
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
        String currentUserId = auth.getCurrentUser().getUid();
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