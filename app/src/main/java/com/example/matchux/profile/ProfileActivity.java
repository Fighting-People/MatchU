package com.example.matchux.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.MainActivity;
import com.example.matchux.R;
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

        // 하단 네비게이션 바 세팅 (아이콘 색상 유지 및 ID 매칭 완료)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setItemIconTintList(null); // 아이콘 원래 모양 유지 코드 추가
        bottomNav.setSelectedItemId(R.id.nav_profile); // ★ 현재 프로필 화면이므로 프로필 선택 상태로 변경

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // 홈 버튼을 누르면 메인 화면으로 이동
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.nav_group) { // ★ nav_my_meeting을 최신 ID인 nav_group으로 변경!
                // startActivity(new Intent(this, MyMeetingActivity.class));
                // return true;
            } else if (id == R.id.nav_profile) {
                return true; // 현재 프로필 화면이므로 그대로 유지
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