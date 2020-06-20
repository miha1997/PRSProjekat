package util;

import simulation.Job;
import java.util.Comparator;

public class JobComparator implements Comparator<Job> {

    @Override
    public int compare(Job job1, Job job2) {
        if(job1.getTimeToFinish() > job2.getTimeToFinish())
            return 1;

        if(job1.getTimeToFinish() < job2.getTimeToFinish())
            return -1;

        return 0;
    }
}
