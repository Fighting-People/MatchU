package com.example.matchux.study;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchux.R;

import java.util.List;

public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.StudyViewHolder> {

    private Context context;
    private List<Study> studyList;
    private OnItemClickListener listener;

    // 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(Study study, String documentId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // MainActivity 호출 구조에 맞게 Context를 포함한 생성자 정의
    public StudyAdapter(Context context, List<Study> studyList) {
        this.context = context;
        this.studyList = studyList;
    }

    @NonNull
    @Override
    public StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_study, parent, false);
        return new StudyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyViewHolder holder, int position) {
        Study study = studyList.get(position);

        // getter 메서드를 사용하여 데이터 세팅 (필드 직접 접근인 study.category 형태도 가능)
        holder.tvCategory.setText(study.getCategory());
        holder.tvMaxMember.setText("정원: " + study.getMaxPeople() + "명");
        holder.tvTitle.setText(study.getStudyName());
        holder.tvDescription.setText(study.getDescription());

        // [추가] 리사이클러뷰 자체 아이템 클릭 리스너 연결로 StudyHomeActivity 이동 구현
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudyHomeActivity.class);
            intent.putExtra("studyId", study.getStudyId());
            intent.putExtra("title", study.getStudyName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return studyList.size();
    }

    // ViewHolder 정의
    class StudyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvMaxMember, tvTitle, tvDescription;

        public StudyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvMaxMember = itemView.findViewById(R.id.tvMaxMember);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}