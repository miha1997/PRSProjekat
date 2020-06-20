package simulation;

public class Job {
    private double prevFinishTime = 0.0;
    private double responseTimeSum = 0.0;
    private double responseTimeCounter = 0.0;

    private double timeToFinish;
    private double totalProcessingTime;
    private Server myCurrentServer;
    private System system;

    public Job(System system){
        this.system = system;
    }

    public void finishJob(){
        if(prevFinishTime == 0.0){
            prevFinishTime = system.getCurrentTime();
            return;
        }

        responseTimeCounter++;
        responseTimeSum += system.getCurrentTime() - prevFinishTime;
        prevFinishTime = system.getCurrentTime();
    }

    public Server getMyCurrentServer() {
        return myCurrentServer;
    }

    public void setMyCurrentServer(Server myCurrentServer) {
        this.myCurrentServer = myCurrentServer;
    }

    public double getTimeToFinish() {
        return timeToFinish;
    }

    public void setTimeToFinish(double timeToFinish) {
        this.timeToFinish = timeToFinish;
    }

    public double getTotalProcessingTime() {
        return totalProcessingTime;
    }

    public void setTotalProcessingTime(double totalProcessingTime) {
        this.totalProcessingTime = totalProcessingTime;
    }

    public double getAverageResponseTime(){
        return responseTimeSum / responseTimeCounter;
    }
}


