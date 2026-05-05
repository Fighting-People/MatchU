        package com.example.matchux;
        import com.google.firebase.firestore.FirebaseFirestore;

        import android.os.Bundle;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import androidx.activity.EdgeToEdge;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.graphics.Insets;
        import androidx.core.view.ViewCompat;
        import androidx.core.view.WindowInsetsCompat;

        public class StudyCreateActivity extends AppCompatActivity {

            Button StudyCreateButton;

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_studycreate);

                Button StudyCreate = findViewById(R.id.btnCreate);

                StudyCreate.setOnClickListener(v -> {
                    String StudyName = ((EditText) findViewById(R.id.StudyName)).getText().toString();
                    String CateGory = ((EditText) findViewById(R.id.Category)).getText().toString();
                    String Description = ((EditText) findViewById(R.id.Description)).getText().toString();
                    int maxPeople = Integer.parseInt(((EditText) findViewById(R.id.SetMaxPeople)).getText().toString());

                    Study study = new Study(CateGory, false, maxPeople, Description, StudyName);

                    db.collection("Study")
                            .add(study)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "스터디 생성 완료", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "생성 실패", Toast.LENGTH_SHORT).show();
                            });
                });
            }
        }