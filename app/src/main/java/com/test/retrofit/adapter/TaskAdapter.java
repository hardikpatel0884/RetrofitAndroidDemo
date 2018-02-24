package com.test.retrofit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.test.retrofit.R;
import com.test.retrofit.model.ModelTask;

import java.util.List;

/**
 * Created by lcom151-one on 2/16/2018.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    List<ModelTask> tasks;
    Context context;

    public TaskAdapter(List<ModelTask> tasks) {
        this.tasks = tasks;
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context=parent.getContext();
        return new TaskHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, null, false));
    }

    @Override
    public void onBindViewHolder(final TaskHolder holder, final int position) {
        try{
            holder.tvTitle.setText(tasks.get(position).getTitle());
            final String url=context.getResources().getString(R.string.host)+"task/"+tasks.get(position).getImage();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.llTaskDetail.getVisibility()==View.GONE){
                        holder.tvTitle.setVisibility(View.GONE);
                        holder.llTaskDetail.setVisibility(View.VISIBLE);
                        Picasso.with(context).load(url).skipMemoryCache().into(holder.ivTaskImage);
                        holder.tvTaskTitle.setText(tasks.get(position).getTitle());
                        holder.tvTaskDescription.setText(tasks.get(position).getDescription());
                    }else{
                        holder.tvTitle.setVisibility(View.VISIBLE);
                        holder.llTaskDetail.setVisibility(View.GONE);
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTaskTitle, tvTaskDescription;
        ImageView ivTaskImage;
        LinearLayout llTaskDetail;

        public TaskHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTaskTitle = itemView.findViewById(R.id.tv_task_title);
            tvTaskDescription = itemView.findViewById(R.id.tv_task_description);
            ivTaskImage = itemView.findViewById(R.id.iv_task_image);
            llTaskDetail = itemView.findViewById(R.id.ll_task_detail);
        }
    }
}
