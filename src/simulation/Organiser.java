package simulation;

import java.util.ArrayList;

public class Organiser {
    ArrayList<Robot> inUse = new ArrayList<>();
    ArrayList<Robot> inIdle = new ArrayList<>();
    // jobs pending [[fromx, fromy, tox, toy]]
    ArrayList<int[]> jobsPending = new ArrayList<>();

    // give tasks to idle robots
    public void parseIdleRobots(){
        if(inIdle.size() != 0 && jobsPending.size() != 0){
            int freeRobotsNum = Math.min(inIdle.size(), jobsPending.size());
            for (int i = 0; i < freeRobotsNum; i++) {
                inIdle.get(0).newJob(jobsPending.get(0));
                jobsPending.remove(0);
                inUse.add(inIdle.get(0));
                inIdle.remove(0);
            }
        }
    }

    // GETTERS AND SETTERS
    public Robot getFirstInuse() {
        return inUse.get(0);
    }

    public ArrayList<Robot> getInUse() {
        return inUse;
    }

    public void addInUse(Robot robot){
        this.inUse.add(robot);
    }

    public void removeInUse(Robot robot){
        this.inUse.remove(robot);
    }

    public Robot getFirstInIdle() {
        return inIdle.get(0);
    }

    public ArrayList<Robot> getInIdle() {
        return inIdle;
    }

    public void addInIdle(Robot robot){
        this.inIdle.add(robot);
    }

    public void removeInIdle(Robot robot){
        this.inIdle.remove(robot);
    }

    public void addJobPending(int[] arr){
        this.jobsPending.add(arr);
    }

    public ArrayList<int[]> getJobsPending() {
        return jobsPending;
    }
}
