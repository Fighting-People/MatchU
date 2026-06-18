package com.example.matchux.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.MainActivity;
import com.example.matchux.R;
import com.example.matchux.study.MyStudyActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView tvNickname, tvEmail, tvBirthdate, tvSex, tvInterests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton btnBack = findViewById(R.id.btnBack4);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 화면을 닫고 이전 화면(메인 또는 로그인 등)으로 돌아갑니다.
                finish();
            }
        });

        // 뷰 연결
        tvNickname  = findViewById(R.id.tvNickname);
        tvEmail     = findViewById(R.id.tvEmail);
        tvBirthdate = findViewById(R.id.tvBirthdate);
        tvSex       = findViewById(R.id.tvSex);
        tvInterests = findViewById(R.id.tvInterests);

        // 2. Firestore에서 회원 프로필 데이터 불러오기
        loadUserProfile();

        // 3. 하단 네비게이션 바 세팅
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_my_meeting) {
                startActivity(new Intent(this, MyStudyActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    } // onCreate 끝

    private void loadUserProfile() {
        // 현재 로그인된 유저 확인
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid   = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // 이메일은 Auth에서 바로 가져옴
        tvEmail.setText(email);

        // 나머지는 Firestore에서 불러옴
        FirebaseFirestore.getInstance().collection("Users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        tvNickname.setText(document.getString("nickname"));
                        tvBirthdate.setText(document.getString("birthdate"));
                        tvSex.setText(document.getString("sex"));

                        // 관심사는 배열로 저장되어 있으므로 문자열로 변환
                        Object interests = document.get("interests");
                        if (interests != null) {
                            tvInterests.setText(interests.toString()
                                    .replace("[", "")
                                    .replace("]", ""));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "프로필 로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    } // loadUserProfile 끝
} // ProfileActivity 클래스 끝