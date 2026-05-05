package com.example.matchux;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    // 1. 사용할 객체 선언
    EditText nicknameEditText;   // 닉네임 입력
    EditText interestsEditText;  // 관심사 입력 (쉼표로 구분, ex: "운동, 독서, 코딩")
    EditText birthdateEditText;  // 생년월일 입력 (ex: 2000-01-01)
    EditText sexEditText;        // 성별 입력 (ex: M / F)
    EditText majorEditText;      // 전공 입력

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // 2. XML의 ID와 자바 객체를 연결
        // 레이아웃 파일(activity_user_profile.xml)에 해당 ID들이 있는지 꼭 확인하세요!
        nicknameEditText  = findViewById(R.id.editTextNickname);
        interestsEditText = findViewById(R.id.editTextInterests);
        birthdateEditText = findViewById(R.id.editTextBirthdate);
        sexEditText       = findViewById(R.id.editTextSex);
        majorEditText     = findViewById(R.id.editTextMajor);

        Button saveButton = findViewById(R.id.buttonSave); // 저장 버튼
        saveButton.setOnClickListener(v -> updateAdditionalUserInfo());
    }

    private void updateAdditionalUserInfo() {

        // 3. 입력값 가져오기
        String nickname  = nicknameEditText.getText().toString().trim();
        // 관심사는 쉼표로 구분된 문자열을 List로 변환 (Firestore array 타입으로 저장)
        // ex: "운동, 독서" → ["운동", "독서"]
        String interestsRaw = interestsEditText.getText().toString().trim();
        List<String> interests = Arrays.asList(interestsRaw.split("\\s*,\\s*"));
        String birthdate = birthdateEditText.getText().toString().trim();
        String sex       = sexEditText.getText().toString().trim();
        String major     = majorEditText.getText().toString().trim();

        // 4. 현재 로그인된 유저 UID 가져오기
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // 5. 업데이트할 필드들을 Map으로 구성
            //    update()는 기존 문서에서 지정한 필드만 덮어씀 (다른 필드는 유지)
            Map<String, Object> updates = new HashMap<>();
            updates.put("nickname",  nickname);
            updates.put("interests", interests); // Firestore에 array 타입으로 저장됨
            updates.put("birthdate", birthdate);
            updates.put("sex",       sex);
            updates.put("major",     major);

            // 6. 기존 유저 문서에 필드 추가 (update 사용)
            FirebaseFirestore.getInstance().collection("Users").document(uid)
                    .update(updates) // Firestore의 해당 필드들을 한번에 업데이트
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "프로필 설정 완료!", Toast.LENGTH_SHORT).show();

                        // 7. 마지막에 메인 액티비티로 이동
                        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}