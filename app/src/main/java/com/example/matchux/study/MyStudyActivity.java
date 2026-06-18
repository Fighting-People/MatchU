package com.example.matchux.study;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchux.MainActivity;
import com.example.matchux.R;
import com.example.matchux.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyStudyActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    List<Study> myStudyList = new ArrayList<>();
    StudyAdapter adapter;

    // [수정] ListView에서 RecyclerView로 변경
    RecyclerView myStudyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_study);

        // [수정] 리사이클러뷰 연결 및 레이아웃 매니저 필수 세팅
        myStudyListView = findViewById(R.id.myStudyListView);
        myStudyListView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudyAdapter(this, myStudyList);
        myStudyListView.setAdapter(adapter);

        // 💡 중요: 리사이클러뷰의 아이템 클릭 이벤트는 이미 StudyAdapter 내부에
        // StudyHomeActivity로 이동하도록 직접 구현해 두었으므로, 여기 있던 기존 ListView 전용
        // setOnItemClickListener 코드는 완전히 걷어내도 정상 작동합니다.

        getMyStudiesFromFirestore();

        // 하단 네비게이션 처리
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_my_meeting);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // 이미 MainActivity가 뒤에 있으므로 단순히 현재 화면을 닫으면 홈으로 돌아갑니다.
                // 또는 FLAG_ACTIVITY_CLEAR_TOP을 사용해 확실히 홈을 최상단으로 가져옵니다.
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_my_meeting) {
                return true; // 현재 화면 유지
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish(); // MyStudyActivity를 닫고 ProfileActivity를 엽니다.
                return true;
            }
            return false;
        });
    }

    private void getMyStudiesFromFirestore() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = auth.getCurrentUser().getUid();

        db.collection("Study")
                .whereArrayContains("members", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    myStudyList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Study study = doc.toObject(Study.class);
                        if (study != null) {
                            study.studyId = doc.getId();
                            myStudyList.add(study);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}