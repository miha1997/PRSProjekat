package simulation;

import java.util.PriorityQueue;
import java.util.Random;

public class CPU extends Server {
    private int k;
    private int size;
    private Server[] connections;
    private double[] distribution;
    private Random random = new Random();

    public CPU(int k, double u, PriorityQueue<Job> processingJobs, System system) {
        super(processingJobs, system);
        super.u = u;

        this.k = k;
        this.size = k + 4;
        this.connections = new Server[size];
        //return branch
        connections[0] = this;

        this.distribution = new double[size];
        distribution[0] = 0.1;
        for(int i = 1; i < 4; i++)
            distribution[i] = distribution[i - 1] + 0.1;

        //probability of going to Dk, considering previous 0.6 probability
        double pk = 0.6 / k;
        for(int i = 4; i < size; i++)
            distribution[i] = distribution[i - 1] + pk;
    }

    public void connectSever(int i, Server server){
        connections[i] = server;
    }

    public void initQueue(Job[] jobs){
        for(Job job: jobs)
            jobsInQueue.add(job);
    }


    @Override
    public Server routeJob(Job job) {
        double randomBranch = random.nextDouble();

        //send job to new server
        for(int i = 0; i < size; i++)
            if(randomBranch < distribution[i]) {
                //if job is finished
                if(i == 0)
                    job.finishJob();

                connections[i].receiveJob(job);
                return connections[i];
            }

        return null;
    }

}
