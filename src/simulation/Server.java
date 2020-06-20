package simulation;

import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

public abstract class Server {
    protected double u;
    private System system;
    private Random random = new Random();
    protected boolean working = false;
    private double workingTime = 0.0;
    private double jobsFinished = 0.0;

    private double previousChangeTime = 0.0;
    private double sumOfJobTime = 0.0;

    protected Queue<Job> jobsInQueue = new ArrayDeque<>();
    private PriorityQueue<Job> processingJobs;

    public Server(PriorityQueue<Job> processingJobs, System system){
        this.system = system;
        this.processingJobs = processingJobs;
    }

    private double getProcessingTime() {
        //lambda = u / 1000 (1s => 1000ms)
        return  Math.log(1 - random.nextDouble())/(-u / 1000);
    }

    public abstract Server routeJob(Job job);

    public void tryToProcess(){
        if(working)
            return;

        if(jobsInQueue.isEmpty())
            return;

        working = true;

        //calculate finishing time
        double timeToFinish = getProcessingTime();

        //update job
        Job jobToProcess = jobsInQueue.poll();
        jobToProcess.setTimeToFinish(timeToFinish);
        jobToProcess.setTotalProcessingTime(timeToFinish);
        jobToProcess.setMyCurrentServer(this);

        //add job to processingJobs
        processingJobs.add(jobToProcess);
    }

    public void addWorkingTime(double time){
        workingTime += time;
        jobsFinished++;

        sumOfJobTime += (system.getCurrentTime() - previousChangeTime) * (jobsInQueue.size() + 1);
        previousChangeTime = system.getCurrentTime();
    }

    public void receiveJob(Job job){
        //add jobTime
        int workingItem = 0;
        if(working)
            workingItem = 1;

        sumOfJobTime += (system.getCurrentTime() - previousChangeTime) * (jobsInQueue.size() + workingItem);
        previousChangeTime = system.getCurrentTime();

        jobsInQueue.add(job);
    }

    public double getUsage(double totalTime){
        return workingTime/ totalTime;
    }
    
    public double getProductivity(double totalTime){
        return jobsFinished/ totalTime * 1000.0;
    }

    public void notWorking() {
        working = false;
    }

    public double getAverageJobs(double totalTime){
        return sumOfJobTime / totalTime;
    }

}
