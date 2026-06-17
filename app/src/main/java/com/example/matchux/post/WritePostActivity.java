package com.example.matchux;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.matchux.post.PostItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WritePostActivity extends AppCompatActivity {

    EditText editTextTitle;
    EditText editTextContent;
    Button buttonSubmit;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        editTextTitle   = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        buttonSubmit    = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(v -> submitPost());
    }

    private void submitPost() {
        String title   = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        // 입력값 검사
        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // PostItem 객체 생성
        PostItem postItem = new PostItem(title, content);

        // Firestore에 저장할 데이터 구성
        Map<String, Object> postData = new HashMap<>();
        postData.put("title",   postItem.getTitle());
        postData.put("content", postItem.getContent());
        postData.put("uid",     FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "");
        postData.put("createdAt", com.google.firebase.Timestamp.now());

        // Firestore Posts 컬렉션에 저장
        db.collection("Posts")
                .add(postData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "게시글 작성 완료!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "작성 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}