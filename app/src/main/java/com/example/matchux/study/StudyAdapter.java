package com.example.matchux.study;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchux.R;

import java.util.List;

public class StudyAdapter extends RecyclerView.Adapter<StudyAdapter.StudyViewHolder> {

    private List<Study> studyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Study study, String documentId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public StudyAdapter(List<Study> studyList) {
        this.studyList = studyList;
    }

    @NonNull
    @Override
    public StudyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 💡 중요: 본인의 실제 리스트 한 칸 XML 파일명으로 매칭하세요 (예: study_item)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_study, parent, false);
        return new StudyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyViewHolder holder, int position) {
        Study study = studyList.get(position);

        holder.tvCategory.setText(study.getCategory());
        holder.tvMaxMember.setText("정원: " + study.getMaxPeople() + "명");
        holder.tvTitle.setText(study.getStudyName());
        holder.tvDescription.setText(study.getDescription());
    }

    @Override
    public int getItemCount() {
        return studyList.size();
    }

    class StudyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvMaxMember, tvTitle, tvDescription;

        public StudyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvMaxMember = itemView.findViewById(R.id.tvMaxMember);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            // 아이템 뷰 전체 클릭 시 이벤트 발생
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    Study clickedStudy = studyList.get(position);

                    // 🌟 StudyHomeActivity에서 관리하는 index 매칭을 사용하므로,
                    // 안전하게 객체 정보를 그대로 넘겨 리스너가 ID를 매핑하도록 유도합니다.
                    listener.onItemClick(clickedStudy, null);
                }
            });
        }
    }
}