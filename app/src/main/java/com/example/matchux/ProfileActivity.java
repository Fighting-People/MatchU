package com.example.matchux;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView tvNickname, tvEmail, tvBirthdate, tvSex, tvInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 뷰 연결
        tvNickname  = findViewById(R.id.tvNickname);
        tvEmail     = findViewById(R.id.tvEmail);
        tvBirthdate = findViewById(R.id.tvBirthdate);
        tvSex       = findViewById(R.id.tvSex);
        tvInterests = findViewById(R.id.tvInterests);

        // Firestore에서 데이터 불러오기
        loadUserProfile();

        // 하단 네비게이션
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_my_meeting) {
                startActivity(new Intent(this, MyMeetingActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
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