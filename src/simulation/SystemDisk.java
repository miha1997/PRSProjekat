package simulation;

import java.util.PriorityQueue;
import java.util.Random;

public class SystemDisk extends Server {
    private int k;
    private int size;
    private Server[] connections;
    private double[] distribution;
    private Random random = new Random();

    public SystemDisk(int k, double u, PriorityQueue<Job> processingJobs, System system) {
        super(processingJobs, system);
        super.u = u;

        this.k = k;
        this.size = k + 4;
        this.connections = new Server[size];

        this.distribution = new double[size];
        for(int i = 0; i < 4; i++)
            distribution[i] = 0.4;

        //probability of going to Dk, considering previous 0.6 probability
        double pk = 0.6 / k;
        for(int i = 4; i < size; i++)
            distribution[i] = distribution[i - 1] + pk;
    }

    public void connectSever(int i, Server server){
        connections[i] = server;
    }

    @Override
    public Server routeJob(Job job) {
        double randomBranch = random.nextDouble();

        //send job to new server
        for(int i = 0; i < size; i++)
            if(randomBranch < distribution[i]) {
                connections[i].receiveJob(job);
                return connections[i];
            }

        return null;
    }
}
