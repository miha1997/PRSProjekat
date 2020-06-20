package util;

public class DataContainer {
    private int size;
    private double[] usages;
    private double[] productivities;
    private double[] jobs;
    private double R;
    private double reps;

    public DataContainer(int size, double reps){
        this.size = size;
        this.reps = reps;

        usages = new double[size];
        productivities = new double[size];
        jobs = new double[size];
    }

    public void addUsages(double [] usages){
        for(int i = 0; i < usages.length; i++){
            this.usages[i] += usages[i];
        }
    }

    public void addProductivities(double [] productivities){
        for(int i = 0; i < productivities.length; i++){
            this.productivities[i] += productivities[i];
        }
    }


    public void addJobs(double [] jobs){
        for(int i = 0; i < jobs.length; i++){
            this.jobs[i] += jobs[i];
        }
    }

    public void addResponseTime(double R){
        this.R += R;
    }

    public void calculateAverage(){
        for(int i = 0; i < size; i++){
            usages[i] /= reps;
            productivities[i] /= reps;
            jobs[i] /= reps;

        }

        R /= reps;
    }

    public double[] getUsages() {
        return usages;
    }

    public double[] getProductivities() {
        return productivities;
    }

    public double[] getJobs() {
        return jobs;
    }

    public double getR() {
        return R;
    }
}
