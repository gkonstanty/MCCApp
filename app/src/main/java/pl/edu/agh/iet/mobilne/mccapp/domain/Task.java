package pl.edu.agh.iet.mobilne.mccapp.domain;

import android.util.Log;

import java.util.Map;

/**
 * Created by gaba on 02.06.17.
 */

public abstract class Task {


    long id;
    boolean isDone;
    String result;

    Map<String, String> workerParameters;

    public Task(Map workerParameters){
        this.isDone = false;
        this.workerParameters = workerParameters;
    }


    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        this.isDone = done;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public abstract String getType();

    public abstract String getTaskParametersString();

    public String getWorkerParametersString(){
        StringBuilder workerParametersString = new StringBuilder();
        for (Map.Entry<String, String> workerParam : workerParameters.entrySet()) {
            workerParametersString.append(workerParam.getKey() + ": ");
            workerParametersString.append(workerParam.getValue());
            workerParametersString.append("\n");
        }

        return workerParametersString.toString();
    }

    @Override
    public String toString(){
        return "Task: " + this.id;
    }

}
