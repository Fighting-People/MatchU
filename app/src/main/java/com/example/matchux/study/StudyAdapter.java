package com.example.matchux.study;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.matchux.R;

import java.util.List;

public class StudyAdapter extends BaseAdapter {
    private Context context;
    private List<Study> studyList;

    public StudyAdapter(Context context, List<Study> studyList) {
        this.context = context;
        this.studyList = studyList;
    }

    @Override
    public int getCount() {
        return studyList.size(); // 리스트에 담긴 스터디 개수만큼 생성
    }

    @Override
    public Object getItem(int position) {
        return studyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. 커스텀 레이아웃 적용
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_study, parent, false);
        }

        // 2. 현재 순서에 맞는 데이터 획득
        Study study = studyList.get(position);

        // 3. 커스텀 레이아웃 안의 컴포넌트들과 아이디 연결 (이 부분이 누락되었는지 확인하세요)
        TextView tvCategory = convertView.findViewById(R.id.tvCategory); // <-- 이 줄이 정확히 있는지 확인!
        TextView tvMaxMember = convertView.findViewById(R.id.tvMaxMember);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);

        // 4. 데이터 반영 (이전 단계에서 수정한 필드명 적용)
        tvCategory.setText(study.category);
        tvMaxMember.setText("정원: " + study.maxPeople + "명");
        tvTitle.setText(study.studyName);
        tvDescription.setText(study.description);

        return convertView;
    }

    // 데이터를 갱신할 때 쓰는 편의 메서드
    public void updateData(List<Study> newList) {
        this.studyList = newList;
        notifyDataSetChanged();
    }
}