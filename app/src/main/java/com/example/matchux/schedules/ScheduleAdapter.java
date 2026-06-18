package com.example.matchux.schedules;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matchux.R;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<ScheduleItem> list;

    public ScheduleAdapter(ArrayList<ScheduleItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(view, listener, list);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = list.get(position);
        holder.tvDate.setText(item.getDate());
        holder.tvTitle.setText(item.getTitle());
        holder.tvContent.setText(item.getContent());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ScheduleItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTitle, tvContent;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener, ArrayList<ScheduleItem> list) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvScheduleDate);
            tvTitle = itemView.findViewById(R.id.tvScheduleTitle);
            tvContent = itemView.findViewById(R.id.tvScheduleContent);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(list.get(position));
                }
            });
        }
    }
}
