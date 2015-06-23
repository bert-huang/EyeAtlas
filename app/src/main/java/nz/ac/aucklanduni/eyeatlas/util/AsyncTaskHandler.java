package nz.ac.aucklanduni.eyeatlas.util;


import android.os.AsyncTask;

import java.util.HashSet;
import java.util.Set;

public class AsyncTaskHandler {

    private Set<AsyncTask> tasks;

    public AsyncTaskHandler() {
        tasks = new HashSet<>();
    }

    public void add(AsyncTask newTask) {
        purgeFinishedTasks();
        tasks.add(newTask);

    }

    public void purgeFinishedTasks() {
        for(AsyncTask t : tasks) {
            if (t.getStatus().equals(AsyncTask.Status.FINISHED)) {
                tasks.remove(t);
            }
        }
    }

    public void purgeAll() {
        for(AsyncTask t : tasks) {
            t.cancel(true);
        }
        tasks.clear();
    }

}
