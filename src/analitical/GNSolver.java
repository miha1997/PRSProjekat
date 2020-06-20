package analitical;

import org.apache.commons.math3.linear.*;
import util.ParamContainer;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

public class GNSolver {
    private static GNSolver gnSolver;
    private double[] uValues;

    //for every k: 2 - 8
    //x0 - CPU
    private double[][] xDemands = new double[9][];

    private GNSolver() {
        uValues = ParamContainer.getInstance().getuValues();
    }

    public static GNSolver getInstance(){
        if(gnSolver == null)
            gnSolver = new GNSolver();

        return gnSolver;
    }

    public double[][] getxDemands() {
        return xDemands;
    }

    private double[][] initProbabilityMatrix(int k){
        int size = k + 4;
        //create GN matrix
        double [][] P = new double[size][size];

        //probability of going to Dk, considering previous 0.6 probability
        double pk = 0.6 / k;

        //CPU
        P[0][0] = P[0][1] = P[0][2] = P[0][3] = 0.1;
        for(int j = 4; j < size; j++){
            P[0][j] = pk;
        }

        //system disks
        for(int i = 1; i < 4; i++){
            P[i][0] = 0.4;

            for(int j = 4; j < size; j++){
                P[i][j] = pk;
            }
        }

        //user disks
        for(int i = 4; i < size; i++){
            P[i][0] = 1;
        }

        return P;
    }

    public void calculateXDemands(){
        int kStart = ParamContainer.getInstance().getkStart();
        int kEnd = ParamContainer.getInstance().getkEnd();

        for(int k = kStart; k <= kEnd; k++){
            int size = k + 4;
            double [][] P = initProbabilityMatrix(k);

            //change values where i == j, so matrix can be used as coefs in equation solver
            for(int i = 0; i < size; i++)
                for(int j = 0; j < size; j++)
                    if(i == j)
                        P[i][j]--;

            RealMatrix pMatrix = new Array2DRowRealMatrix(P, false);
            pMatrix = pMatrix.transpose();

            RealMatrix coeficients = pMatrix.getSubMatrix(1, size - 1 , 1, size - 1);

            DecompositionSolver solver = new SingularValueDecomposition(coeficients).getSolver();

            //set right side of equation
            double [] rightSide = new double[size - 1];
            for(int i = 0; i < size - 1; i++)
                rightSide[i] = -uValues[0] * pMatrix.getEntry(i + 1, 0);

            RealVector constants = new ArrayRealVector(rightSide , false);
            double[] solution = solver.solve(constants).toArray();

            //store results
            xDemands[k] = new double[size];

            //normalize on x0
            xDemands[k][0] = 1.0;

            for(int i = 0; i < size - 1; i++){
                xDemands[k][i + 1] = solution[i] / uValues[i + 1] ;
            }
        }
    }

    public void saveInFile(){
        try{
            DecimalFormat df2 = new DecimalFormat("0.000");

            File file = new File("files/potraznje_analiticki.txt");
            file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Normalizovane potraznje: \nx0 - procesor; x1, x2, x3 - sistemski diskovi; x4:12 - korisnicki diskovi \n******************************************************************************************************\n");

            for(int k = ParamContainer.getInstance().getkStart(); k <= ParamContainer.getInstance().getkEnd(); k++){
                fileWriter.write("\nk = " + k + ": \n");

                for(int j = 0; j < xDemands[k].length ; j++){
                    fileWriter.write("x" + j + ": " + df2.format(xDemands[k][j]) + "\t\t");
                }
            }

            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
