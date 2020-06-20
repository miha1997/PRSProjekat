package simulation;

import util.DataContainer;
import util.ParamContainer;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {
    private static int duration;
    private static int n;
    private static boolean work = true;
    private static Scanner in = new Scanner(java.lang.System.in);

    private static void runOneSimulation() {
        //write in file
        try {
            DecimalFormat df2 = new DecimalFormat("0.0000");
            DecimalFormat df3 = new DecimalFormat("0.00000");

            File file = new File("files/rezultati_simulacija.txt");
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Prikaz trazenih parametara (indeksi: 0 - procesor; 1:3 - sistemski diskovi; 4:12 - korisnicki diskovi \nU - /, X - 1/s, J - /");
            fileWriter.write("\n******************************************************************************************************");
            fileWriter.write("\n\nn = " + n);

            int kStart = ParamContainer.getInstance().getkStart();
            int kEnd = ParamContainer.getInstance().getkEnd();

            //duration is given in minutes
            double durationInMS = duration * 60 * 1000;

            for (int k = kStart; k <= kEnd; k++) {
                System system = new System(k, n, durationInMS);
                system.work();

                fileWriter.write("\n\tk = " + k);
                fileWriter.write("\t(vreme odziva sistema: R = " + df2.format(system.getResponseTime()) + "s)");

                //find bottleneck
                int maxIndex = 0;
                double max = system.getServers()[0].getUsage(durationInMS);

                for (int i = 1; i < system.getServers().length; i++) {
                    Server server = system.getServers()[i];

                    if (server.getUsage(durationInMS) > max) {
                        max = server.getUsage(durationInMS);
                        maxIndex = i;
                    }
                }

                String bootleNeck = "CPU";
                if (maxIndex > 0 && maxIndex < 4)
                    bootleNeck = "sistemski disk";
                if (maxIndex >= 4)
                    bootleNeck = "korisnicki disk";
                fileWriter.write("\t (usko grlo sistema je " + bootleNeck + ")");

                //usages
                fileWriter.write("\n\t\t");
                for (int i = 0; i < system.getServers().length; i++) {
                    Server server = system.getServers()[i];
                    fileWriter.write("U" + i + ": " + df2.format(server.getUsage(durationInMS)) + "\t");
                }

                //productivities
                fileWriter.write("\n\t\t");
                for (int i = 0; i < system.getServers().length; i++) {
                    Server server = system.getServers()[i];
                    fileWriter.write("X" + i + ": " + df2.format(server.getProductivity(durationInMS)) + "\t");
                }

                //jobs
                fileWriter.write("\n\t\t");
                for (int i = 0; i < system.getServers().length; i++) {
                    Server server = system.getServers()[i];
                    fileWriter.write("J" + i + ": " + df2.format(server.getAverageJobs(durationInMS)) + "\t");
                }

                fileWriter.write("\n");
                java.lang.System.out.println("Obrada gotova za n = " + n + ", k = " + k);
            }

            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void runAllSimulations() {
        //write in file
        try {
            DecimalFormat df2 = new DecimalFormat("0.0000");

            File file = new File("files/rezultati_simulacija_usrednjeno.txt");
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Prikaz trazenih parametara (indeksi: 0 - procesor; 1:3 - sistemski diskovi; 4:12 - korisnicki diskovi \nU - /, X - 1/s, J - /");
            fileWriter.write("\n******************************************************************************************************");

            int[] nValues = {10, 15, 20};
            int[] repetitions = {10, 25, 100};

            for (int i = 0; i < nValues.length; i++) {
                int n = nValues[i];
                int reps = repetitions[i];

                fileWriter.write("\n\nn = " + n);
                int kStart = ParamContainer.getInstance().getkStart();
                int kEnd = ParamContainer.getInstance().getkEnd();

                //duration is given in minutes
                double durationInMS = duration * 60 * 1000;
                //store data before calculating average
                DataContainer[] dataContainers = new DataContainer[kEnd + 1];
                for (int k = kStart; k <= kEnd; k++)
                    dataContainers[k] = new DataContainer(k + 4, reps);

                for (int r = 0; r < reps; r++) {
                    for (int k = kStart; k <= kEnd; k++) {
                        System system = new System(k, n, durationInMS);
                        system.work();

                        DataContainer dataContainer = dataContainers[k];
                        int size = k + 4;

                        double[] usages = new double[size];
                        double[] productivities = new double[size];
                        double[] jobs = new double[size];

                        for (int s = 0; s < size; s++) {
                            Server server = system.getServers()[s];

                            usages[s] = server.getUsage(durationInMS);
                            productivities[s] = server.getProductivity(durationInMS);
                            jobs[s] = server.getAverageJobs(durationInMS);
                        }

                        dataContainer.addUsages(usages);
                        dataContainer.addProductivities(productivities);
                        dataContainer.addJobs(jobs);
                        dataContainer.addResponseTime(system.getResponseTime());

                        int rIndex = r + 1;
                        java.lang.System.out.println("Obrada gotova za n = " + n + ", k = " + k + ", rep: " + rIndex);
                    }
                }

                //calculate average
                for (int k = kStart; k <= kEnd; k++) {
                    DataContainer dataContainer = dataContainers[k];
                    dataContainer.calculateAverage();

                    fileWriter.write("\n\tk = " + k);
                    fileWriter.write("\t(vreme odziva sistema: R = " + df2.format(dataContainer.getR()) + "s)");

                    //find bottleneck
                    int maxIndex = 0;
                    double max = dataContainer.getUsages()[0];

                    for (int j = 1; j < dataContainer.getUsages().length; j++) {
                        if (dataContainer.getUsages()[j] > max) {
                            max = dataContainer.getUsages()[j];
                            maxIndex = j;
                        }
                    }

                    String bootleNeck = "CPU";
                    if (maxIndex > 0 && maxIndex < 4)
                        bootleNeck = "sistemski disk";
                    if (maxIndex >= 4)
                        bootleNeck = "korisnicki disk";
                    fileWriter.write("\t (usko grlo sistema je " + bootleNeck + ")");

                    //usages
                    fileWriter.write("\n\t\t");
                    int br = 0;
                    for (double usage : dataContainer.getUsages()) {
                        fileWriter.write("U" + br + ": " + df2.format(usage) + "\t");
                        br++;
                    }

                    //productivities
                    fileWriter.write("\n\t\t");
                    br = 0;
                    for (double productivity : dataContainer.getProductivities()) {
                        fileWriter.write("X" + br + ": " + df2.format(productivity) + "\t");
                        br++;
                    }

                    //jobs
                    fileWriter.write("\n\t\t");
                    br = 0;
                    for (double job : dataContainer.getJobs()) {
                        fileWriter.write("J" + br + ": " + df2.format(job) + "\t");
                        br++;
                    }

                    fileWriter.write("\n");
                    java.lang.System.out.println("Obrada gotova za n = " + n + ", k = " + k);
                }

            }

            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {
        while (work) {
            java.lang.System.out.println("Izabrati jednu od opcija: \n");
            java.lang.System.out.println("1) Pokrenuti jednu simulaciju");
            java.lang.System.out.println("2) Pokrenuti vise simulacija i izvrsiti usrednjavanje");
            java.lang.System.out.println("3) Kraj rada");


            int c = in.nextInt();

            java.lang.System.out.println("\nUneti vreme trajanja simulacije u minutima: (0 za podrazumevano vreme od 24h)");
            duration = in.nextInt();

            //default time -> 24 * 60 min
            if (duration == 0)
                duration = 1440;

            switch (c) {
                case 1: {
                    java.lang.System.out.println("\nUneti stepen multiprogramiranja - n: (10, 15 ili 20)");
                    n = in.nextInt();

                    runOneSimulation();
                    java.lang.System.out.println("Rezultati su u fajlu /files/rezultati_simulacija.txt \n");
                    break;
                }
                case 2: {
                    runAllSimulations();

                    java.lang.System.out.println("Rezultati su u fajlu /files/rezultati_simulacija_usrednjeno.txt \n");
                    break;
                }
                case 3: {
                    work = false;
                }
            }

        }
    }

}
