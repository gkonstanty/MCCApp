package pl.edu.agh.iet.mobilne.mccapp.domain;

import java.util.Map;

/**
 * Created by gaba on 02.06.17.
 */

public class PiTask extends Task{

    final String type = "PI";
    long pointsNo;

    public PiTask(Map workerParameters, long pointsNo) {
        super(workerParameters);

        this.pointsNo = pointsNo;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String toString(){
        return "PiTask: " + id;
    }

}
