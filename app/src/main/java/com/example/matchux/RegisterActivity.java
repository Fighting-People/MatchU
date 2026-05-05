package com.example.matchux;

import android.content.Intent;
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

    public void backToMain() {
        navigateToMainActivity();
    }

    private void registerUser() {
        String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();
        String email    = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                        // ✅ 화면 이동을 saveUserData 내부 콜백에서 처리
                        saveUserData(username, email);
                    } else {
                        Toast.makeText(this,
                                "회원가입 실패: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String username, String email) {
        String uid = auth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("uid", uid);

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
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "데이터 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}