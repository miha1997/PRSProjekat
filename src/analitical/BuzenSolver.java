package analitical;

import util.ParamContainer;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class BuzenSolver {
    private static BuzenSolver buzenSolver;

    private BuzenSolver(){

    }

    public static BuzenSolver getInstance(){
        if(buzenSolver == null)
            buzenSolver = new BuzenSolver();

        return buzenSolver;
    }

    public void calculateParams(){
        try{
            DecimalFormat df2 = new DecimalFormat("0.0000");

            File file = new File("files/rezultati_analiticki.txt");
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Prikaz trazenih parametara (indeksi: 0 - procesor; 1:3 - sistemski diskovi; 4:12 - korisnicki diskovi \nU - /, X - 1/s, J - /");
            fileWriter.write("\n******************************************************************************************************");

            GNSolver.getInstance().calculateXDemands();
            double[][] xDemands = GNSolver.getInstance().getxDemands();
            int[] nValues = ParamContainer.getInstance().getnValues();
            double[] uValues = ParamContainer.getInstance().getuValues();

            int kStart = ParamContainer.getInstance().getkStart();
            int kEnd = ParamContainer.getInstance().getkEnd();

            for(int k = kStart; k <= kEnd; k++){
                fileWriter.write("\n\nk = " + k);
                //find bottleneck
                int maxIndex = 0;
                double max = xDemands[k][0];
                for(int i = 1; i < xDemands[k].length; i++)
                    if(xDemands[k][i] > max){
                        max = xDemands[k][i];
                        maxIndex = i;
                    }

                String bootleNeck = "CPU";
                if(maxIndex > 0 && maxIndex < 4)
                    bootleNeck = "sistemski disk";
                if(maxIndex >= 4)
                    bootleNeck = "korisnicki disk";

                fileWriter.write("\t (usko grlo sistema je " + bootleNeck + ")\n");

                //create Buzen matrix, n = 21 (for nMax = 20)
                int nMax = 21;
                int m = k + 4 + 1;
                double [][] buzenMatrix = new double[nMax][m];

                for(int j = 1; j < m; j++)
                    buzenMatrix[0][j] = 1.0;

                for(int i = 1; i < nMax; i++)
                    for(int j = 1; j < m; j++)
                        buzenMatrix[i][j] = buzenMatrix[i][j - 1] + buzenMatrix[i - 1][j] * xDemands[k][j - 1];

                //store G constants
                double [] G = new double[nMax];

                for(int i = 0; i < nMax; i++)
                    G[i] = buzenMatrix[i][m - 1];
                for(int n : nValues){
                    fileWriter.write("\n\tn = " + n);
                    //response time
                    //first, system productivity Xs
                    double pLeave = ParamContainer.getInstance().getpLeave();
                    double X0 = xDemands[k][0] * G[n - 1] / G[n] * uValues[0];
                    double Xs = pLeave * X0;
                    double T = n / Xs;
                    fileWriter.write("\t(vreme odziva sistema: R = " + df2.format(T) + "s)");

                    //usages
                    double [] usages = new double[m - 1];
                    fileWriter.write("\n\t\t");
                    for(int i = 0; i < m - 1; i++){
                        usages[i] = xDemands[k][i] * G[n - 1] / G[n];
                        fileWriter.write("U" + i + ": " + df2.format(usages[i]) + "\t");
                    }

                    //productivities
                    fileWriter.write("\n\t\t");
                    for(int i = 0; i < m - 1; i++){
                        double productivity = usages[i] * uValues[i];
                        fileWriter.write("X" + i + ": " + df2.format(productivity) + "\t");
                    }

                    //jobs
                    fileWriter.write("\n\t\t");
                    for(int i = 0; i < m - 1; i++){
                        double x = xDemands[k][i];
                        double job = 0;
                        for(int j = 1; j <= n; j++){
                            double temp = Math.pow(x, j) * G[n - j] / G[n];
                            job += temp;
                        }
                        fileWriter.write("J" + i + ": " + df2.format(job) + "\t");
                    }
                }
            }

            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
