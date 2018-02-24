package com.test.retrofit.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.test.retrofit.R;
import com.test.retrofit.adapter.TaskAdapter;
import com.test.retrofit.config.ApiClient;
import com.test.retrofit.config.ApiInterface;
import com.test.retrofit.model.ModelTask;
import com.test.retrofit.response.task.ResponseGetTask;
import com.test.retrofit.utils.UserSession;
import com.test.retrofit.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskActivity extends AppCompatActivity {

    private RecyclerView rvTaskUser;
    private TaskAdapter adapter;
    private List<ModelTask> tasks = new ArrayList();
    private SwipeRefreshLayout srlTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        rvTaskUser = findViewById(R.id.rv_tasks);
        srlTask=findViewById(R.id.srl_task);

        adapter = new TaskAdapter(tasks);
        rvTaskUser.setLayoutManager(new LinearLayoutManager(TaskActivity.this));
        rvTaskUser.setAdapter(adapter);

        onGetTask();
        srlTask.post(new Runnable() {
            @Override
            public void run() {
                onGetTask();
                srlTask.setRefreshing(true);
            }
        });

        srlTask.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onGetTask();
            }
        });

    }

    private void onGetTask() {
        try {
            Call<ResponseGetTask> getTask = Utility.getApiService().getTask(Utility.getSession().getApiKey());
            getTask.enqueue(new Callback<ResponseGetTask>() {
                @Override
                public void onResponse(Call<ResponseGetTask> call, Response<ResponseGetTask> response) {
                    srlTask.setRefreshing(false);
                    if (response.isSuccessful()) {
                        if (response.body().getError()) {
                            Toast.makeText(TaskActivity.this, "oops", Toast.LENGTH_SHORT).show();
                        } else {
                            tasks.clear();
                            tasks.addAll(response.body().getTask().getTasks());
                            adapter.notifyDataSetChanged();
                            Toast.makeText(TaskActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseGetTask> call, Throwable t) {
                    srlTask.setRefreshing(false);
                    Toast.makeText(TaskActivity.this, "fails", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
