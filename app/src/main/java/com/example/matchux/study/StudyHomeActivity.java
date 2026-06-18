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
    TextView tvCategoryHome, tvMaxPeopleHome, tvDescriptionHome; // 🌟 정보 연동용 텍스트뷰 추가

    Button postBtn, scheduleBtn, infoBtn, btnLeaveStudy, btnJoinStudy;
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
        btnBack.setOnClickListener(v -> finish());

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인 정보가 유효하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = auth.getCurrentUser().getUid();

        // 1. UI 컴포넌트 연결
        studyTitle = findViewById(R.id.studyTitle);
        tvCategoryHome = findViewById(R.id.tvCategoryHome);
        tvMaxPeopleHome = findViewById(R.id.tvMaxPeopleHome);
        tvDescriptionHome = findViewById(R.id.tvDescriptionHome);

        postBtn = findViewById(R.id.postBtn);
        scheduleBtn = findViewById(R.id.scheduleBtn);
        infoBtn = findViewById(R.id.infoBtn);
        recyclerView = findViewById(R.id.recyclerView);
        infoLayout = findViewById(R.id.infoLayout);
        writeBtn = findViewById(R.id.writeBtn);

        btnLeaveStudy = findViewById(R.id.btnLeaveStudy);
        btnJoinStudy = findViewById(R.id.btnJoinStudy);

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

        // 3. 현재 유저의 참여 여부 체크 및 상세 정보 로드
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
        recyclerView.setAdapter(postAdapter);

        // 5. 초기 데이터 로드 (게시글)
        loadPosts();

        // 6. 상단 탭 클릭 이벤트
        postBtn.setOnClickListener(v -> {
            currentTab = Tab.POSTS;
            recyclerView.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.GONE);
            if (writeBtn != null) {
                writeBtn.setVisibility(View.VISIBLE);
                writeBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#87CEEB")));
            }
            recyclerView.setAdapter(postAdapter);
            loadPosts();
        });

        scheduleBtn.setOnClickListener(v -> {
            currentTab = Tab.SCHEDULE;
            recyclerView.setVisibility(View.VISIBLE);
            infoLayout.setVisibility(View.GONE);
            if (writeBtn != null) {
                writeBtn.setVisibility(View.VISIBLE);
                writeBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#6200EE")));
            }
            recyclerView.setAdapter(scheduleAdapter);
            loadSchedules();
        });

        infoBtn.setOnClickListener(v -> {
            currentTab = Tab.INFO;
            recyclerView.setVisibility(View.GONE);
            infoLayout.setVisibility(View.VISIBLE);
            if (writeBtn != null) writeBtn.setVisibility(View.GONE);
        });

        // 7. 탈퇴 버튼 클릭 리스너
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

        // 8. 참여 버튼 클릭 리스너
        if (btnJoinStudy != null) {
            btnJoinStudy.setOnClickListener(v -> joinStudyGroup());
        }

        // 9. 글쓰기 버튼 클릭
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
        if (currentTab == Tab.POSTS) loadPosts();
        else if (currentTab == Tab.SCHEDULE) loadSchedules();
    }

    // 🌟 참여 여부 실시간 갱신 및 상세 정보 출력 로직
    private void checkParticipationStatus() {
        db.collection("Study").document(studyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // DB 데이터 연동 처리
                        String category = documentSnapshot.getString("category");
                        Long maxPeople = documentSnapshot.getLong("maxPeople");
                        String description = documentSnapshot.getString("description");
                        List<String> members = (List<String>) documentSnapshot.get("members");

                        if (category != null) tvCategoryHome.setText("카테고리 : " + category);
                        if (maxPeople != null) tvMaxPeopleHome.setText("인원 : " + maxPeople + "명");
                        if (description != null) tvDescriptionHome.setText("소개 : " + description);

                        // 참여 여부에 따른 가입/탈퇴 버튼 Visibility 핸들링
                        if (members != null && members.contains(currentUserId)) {
                            if (btnLeaveStudy != null) btnLeaveStudy.setVisibility(View.VISIBLE);
                            if (btnJoinStudy != null) btnJoinStudy.setVisibility(View.GONE);

                            if (postBtn != null) postBtn.setEnabled(true);
                            if (scheduleBtn != null) scheduleBtn.setEnabled(true);
                        } else {
                            if (btnLeaveStudy != null) btnLeaveStudy.setVisibility(View.GONE);
                            if (btnJoinStudy != null) btnJoinStudy.setVisibility(View.VISIBLE);

                            if (postBtn != null) postBtn.setEnabled(false);
                            if (scheduleBtn != null) scheduleBtn.setEnabled(false);
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "상태 확인 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void joinStudyGroup() {
        db.collection("Study").document(studyId)
                .update("members", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudyHomeActivity.this, "모임에 성공적으로 참여했습니다!", Toast.LENGTH_SHORT).show();
                    checkParticipationStatus();
                })
                .addOnFailureListener(e -> Toast.makeText(StudyHomeActivity.this, "참여 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void leaveStudyGroup() {
        db.collection("Study").document(studyId)
                .update("members", FieldValue.arrayRemove(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudyHomeActivity.this, "모임에서 탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                    checkParticipationStatus();
                })
                .addOnFailureListener(e -> Toast.makeText(StudyHomeActivity.this, "탈퇴 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}