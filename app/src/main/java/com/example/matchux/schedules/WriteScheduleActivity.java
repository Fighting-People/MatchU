package com.example.matchux.schedules;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.matchux.R;

import androidx.appcompat.app.AppCompatActivity;

// 다른 패키지에 있는 MainActivity를 사용하기 위해 import 추가

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WriteScheduleActivity extends AppCompatActivity {

    // 1. 사용할 화면 요소 객체 선언
    private EditText titleEditText;
    private EditText contentEditText;
    private EditText dateEditText; // 일정 날짜 입력창

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2. 레이아웃 XML 파일 연결
        setContentView(R.layout.activity_schedule);

        // Firebase 초기화
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // 3. XML의 ID와 자바 객체 연결 (findViewById)
        titleEditText = findViewById(R.id.editTextScheduleTitle);
        contentEditText = findViewById(R.id.editTextScheduleContent);
        dateEditText = findViewById(R.id.editTextScheduleDate);

        findViewById(R.id.btnBackSchedule).setOnClickListener(v -> finish());

        Button saveButton = findViewById(R.id.buttonSaveSchedule);

        // 4. 저장 버튼 클릭 리스너 설정
        saveButton.setOnClickListener(v -> saveScheduleAndPost());
    }

    private void saveScheduleAndPost() {
        // 입력값 가져오기
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        // 유효성 검사
        if (title.isEmpty() || content.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "모든 빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        String studyId = getIntent().getStringExtra("studyId"); // 스터디 ID 받기

        // 5. Posts 컬렉션에 저장할 데이터 구성
        Map<String, Object> postMap = new HashMap<>();
        postMap.put("title", title);
        postMap.put("content", content);
        postMap.put("authorUid", uid);
        postMap.put("studyId", studyId); // 스터디 ID 추가
        postMap.put("timestamp", com.google.firebase.Timestamp.now()); // 작성 시간

        // 6. Schedule 컬렉션에 저장할 데이터 구성
        Map<String, Object> scheduleMap = new HashMap<>();
        scheduleMap.put("title", title);
        scheduleMap.put("content", content); // 내용도 추가
        scheduleMap.put("date", date); // 일정 날짜
        scheduleMap.put("userUid", uid);
        scheduleMap.put("studyId", studyId); // 스터디 ID 추가
        scheduleMap.put("timestamp", com.google.firebase.Timestamp.now());

        // 7. Firestore에 저장 (문서 ID 자동 생성 방식: .add())
        // 먼저 Posts 컬렉션에 저장
        firestore.collection("Posts")
                .add(postMap)
                .addOnSuccessListener(postDocumentReference -> {
                    Log.d("Firestore", "Posts 저장 완료. 문서 ID: " + postDocumentReference.getId());

                    // Posts 저장 성공 후 Schedule 컬렉션에 저장 (연쇄 저장)
                    firestore.collection("Schedule")
                            .add(scheduleMap)
                            .addOnSuccessListener(scheduleDocumentReference -> {
                                Toast.makeText(this, "일정 및 게시글 등록 완료!", Toast.LENGTH_SHORT).show();

                                // 8. 성공 시 메인 화면으로 이동하거나 현재 화면 종료
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Schedule 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Posts 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}