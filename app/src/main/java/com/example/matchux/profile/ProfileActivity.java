package com.example.matchux.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.R;
import com.example.matchux.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView tvNickname, tvEmail, tvBirthdate, tvSex, tvInterests, menuLogout;
    Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 뷰 연결
        tvNickname    = findViewById(R.id.tvNickname);
        tvEmail       = findViewById(R.id.tvEmail);
        tvBirthdate   = findViewById(R.id.tvBirthdate);
        tvSex         = findViewById(R.id.tvSex);
        tvInterests   = findViewById(R.id.tvInterests);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        menuLogout    = findViewById(R.id.menuLogout);

        // Firestore에서 데이터 불러오기
        loadUserProfile();

        // 프로필 수정 버튼 → EditProfileActivity 이동
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        // 로그아웃 버튼 → 확인 다이얼로그 후 로그아웃
        menuLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("로그아웃")
                    .setMessage("정말 로그아웃 하시겠습니까?")
                    .setPositiveButton("로그아웃", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

        // 하단 네비게이션 바 세팅
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                finish();
                return true;
            } else if (id == R.id.nav_my_meeting) {
                // startActivity(new Intent(this, MyMeetingActivity.class));
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    // 수정 후 돌아왔을 때 자동으로 최신 데이터 표시
    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

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
                    Toast.makeText(this, "불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}