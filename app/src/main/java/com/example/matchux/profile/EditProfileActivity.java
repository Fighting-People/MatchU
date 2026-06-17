package com.example.matchux.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText editNickname, editBirthdate, editSex, editMajor, editInterests;
    Button btnSave;
    ImageButton btnBack;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 뷰 연결
        editNickname  = findViewById(R.id.editNickname);
        editBirthdate = findViewById(R.id.editBirthdate);
        editSex       = findViewById(R.id.editSex);
        editMajor     = findViewById(R.id.editMajor);
        editInterests = findViewById(R.id.editInterests);
        btnSave       = findViewById(R.id.btnSave);
        btnBack       = findViewById(R.id.btnBack);

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // Firestore에서 기존 데이터 불러와서 EditText에 채우기
        loadCurrentProfile();

        // 저장 버튼 클릭
        btnSave.setOnClickListener(v -> saveProfile());
    }

    // 기존 프로필 데이터를 불러와서 EditText에 미리 채움
    private void loadCurrentProfile() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        editNickname.setText(document.getString("nickname"));
                        editBirthdate.setText(document.getString("birthdate"));
                        editSex.setText(document.getString("sex"));
                        editMajor.setText(document.getString("major"));

                        // 관심사: List → 쉼표 구분 문자열로 변환해서 보여줌
                        Object interests = document.get("interests");
                        if (interests != null) {
                            editInterests.setText(interests.toString()
                                    .replace("[", "")
                                    .replace("]", ""));
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "불러오기 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // 수정된 내용 Firestore에 저장
    private void saveProfile() {
        String nickname  = editNickname.getText().toString().trim();
        String birthdate = editBirthdate.getText().toString().trim();
        String sex       = editSex.getText().toString().trim();
        String major     = editMajor.getText().toString().trim();
        String interestsRaw = editInterests.getText().toString().trim();

        // 입력값 검사
        if (nickname.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 관심사: 쉼표 구분 문자열 → List
        List<String> interests = Arrays.asList(interestsRaw.split("\\s*,\\s*"));

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> updates = new HashMap<>();
        updates.put("nickname",  nickname);
        updates.put("birthdate", birthdate);
        updates.put("sex",       sex);
        updates.put("major",     major);
        updates.put("interests", interests);

        db.collection("Users").document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); // ProfileActivity로 돌아가기
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "수정 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
