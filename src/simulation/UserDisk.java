package simulation;

import java.util.PriorityQueue;

public class UserDisk extends Server {
    private Server cpu;

    public UserDisk(double u, PriorityQueue<Job> processingJobs, System system) {
        super(processingJobs, system);
        super.u = u;
    }

    public void connectCPU(Server cpu){
        this.cpu = cpu;
    }

    @Override
    public Server routeJob(Job job) {
        cpu.receiveJob(job);
        return cpu;
    }
}
