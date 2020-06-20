package simulation;

import util.JobComparator;
import util.ParamContainer;
import java.util.Iterator;
import java.util.PriorityQueue;

public class System {
    private int k;
    private int n;
    private int size;

    private Server[] servers;
    private Job[] allJobs;
    private PriorityQueue<Job> processingJobs;

    //in ms
    private double currentTime = 0.0;
    private double endTime;

    public System(int k, int n, double duration) {
        this.k = k;
        this.n = n;
        this.size = k + 4;
        this.endTime = duration;

        allJobs = new Job[n];
        for(int i = 0; i < n; i++)
            allJobs[i] = new Job(this);

        processingJobs = new PriorityQueue<>(new JobComparator());

        servers = new Server[size];
        //CPU
        servers[0] = new CPU(k, ParamContainer.getInstance().getUp(), processingJobs, this);
        //Sd1, Sd2 and Sd3
        servers[1] = new SystemDisk(k, ParamContainer.getInstance().getUd1(), processingJobs, this);
        servers[2] = new SystemDisk(k, ParamContainer.getInstance().getUd2(), processingJobs, this);
        servers[3] = new SystemDisk(k, ParamContainer.getInstance().getUd3(), processingJobs, this);

        //user disks
        for(int i = 4; i < size; i++)
            servers[i] = new UserDisk(ParamContainer.getInstance().getUdk(), processingJobs, this);

        //connect all servers
        CPU cpu = (CPU) servers[0];
        cpu.initQueue(allJobs);
        for(int i = 1; i < size; i++)
            cpu.connectSever(i, servers[i]);

        for(int i = 1; i < 4; i++){
            SystemDisk systemDisk = (SystemDisk) servers[i];
            for(int j = 0; j < size; j++)
                systemDisk.connectSever(j, servers[j]);
        }

        for(int i = 4; i < size; i++){
            UserDisk userDisk = (UserDisk) servers[i];
            userDisk.connectCPU(servers[0]);
        }

    }

    public void work(){
        //start first job on CPU
        servers[0].tryToProcess();

        while(currentTime < endTime){
            Job finishedJob = processingJobs.poll();

            //update current time
            double passedTime = finishedJob.getTimeToFinish();
            currentTime += passedTime;

            //decrement timeToFinish in remaining processingJobs
            Iterator<Job> iterator = processingJobs.iterator();
            while(iterator.hasNext()){
                Job job = iterator.next();
                job.setTimeToFinish(job.getTimeToFinish() - passedTime);
            }

            //get server where job is finished
            Server finishedJobWorkingServer = finishedJob.getMyCurrentServer();
            finishedJobWorkingServer.addWorkingTime(finishedJob.getTotalProcessingTime());
            finishedJobWorkingServer.notWorking();

            //route finishedJob
            Server receivingServerJob = finishedJobWorkingServer.routeJob(finishedJob);

            finishedJobWorkingServer.tryToProcess();
            receivingServerJob.tryToProcess();
        }
    }

    public Server[] getServers() {
        return servers;
    }

    public double getCurrentTime(){
        return currentTime;
    }

    public double getResponseTime(){
        double sum = 0.0;
        for(int i = 0 ; i < n; i++)
            sum += allJobs[i].getAverageResponseTime();

        return sum / (double) n / 1000.0;
    }
}
