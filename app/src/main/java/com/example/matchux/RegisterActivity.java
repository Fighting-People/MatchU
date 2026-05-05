package com.example.matchux;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase 인증
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Button registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(v -> registerUser());

        ImageButton imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(v -> backToMain());
    }
    public void backToMain(){
        navigateToLoginActivity();

    }

    private void registerUser() {
        String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
        String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Firebase 사용자 가져오기 (필요 시 사용)
                        // FirebaseUser user = auth.getCurrentUser();

                        // Firestore에 사용자 정보 저장
                        saveUserData(username, email);

                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(this,
                                "회원가입 실패: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String username, String email) {

        // 1. 현재 로그인된 Firebase 유저의 고유 ID 가져오기
        //    createUserWithEmailAndPassword 성공 직후라 getCurrentUser()가 null이 아님
        String uid = auth.getCurrentUser().getUid();
        // uid 예시: "abc123xyz789..."

        // 2. Firestore에 저장할 데이터 구성
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("uid", uid);  // UID도 함께 저장 (나중에 조회할 때 필요)

        firestore.collection("Users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "기초 정보 저장 완료");
                    // ✅ 저장 성공 후 여기서 화면 이동
                    Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                })

                // 6. 저장 실패했을 때 실행되는 콜백
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "데이터 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}