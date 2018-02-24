package com.test.retrofit.response.task;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.test.retrofit.model.ModelTask;

import java.util.List;

/**
 * Created by lcom151-one on 2/16/2018.
 */

public class ResponseTask {
    @SerializedName("tasks")
    @Expose
    private List<ModelTask> tasks = null;

    public List<ModelTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ModelTask> tasks) {
        this.tasks = tasks;
    }
}
