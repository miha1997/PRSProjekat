package util;

public class ParamContainer {
    private ParamContainer(){

    }
    //define all params here
    private double sp = 5;
    private double sd1 = 20;
    private double sd2 = 15;
    private double sd3 = 15;
    private double sdk = 20;

    private double up = 1000.0 / sp;
    private double ud1 = 1000.0 / sd1;
    private double ud2 = 1000.0 / sd2;
    private double ud3 = 1000.0 / sd3;
    private double udk = 1000.0 / sdk;

    private double pLeave = 0.1;

    private double[] uValues = {up, ud1, ud2, ud3, udk, udk, udk, udk, udk, udk, udk, udk};
    private int[] nValues = { 10, 15, 20};

    private int kStart = 2;
    private int kEnd = 8;


    private static ParamContainer paramContainer;

    public static ParamContainer getInstance(){
        if(paramContainer == null)
            paramContainer = new ParamContainer();

        return paramContainer;
    }

    public double getSp() {
        return sp;
    }

    public double getpLeave() {
        return pLeave;
    }

    public double getSd1() {
        return sd1;
    }

    public double getSd2() {
        return sd2;
    }

    public double getSd3() {
        return sd3;
    }

    public double getSdk() {
        return sdk;
    }

    public double getUp() {
        return up;
    }

    public double getUd1() {
        return ud1;
    }

    public double getUd2() {
        return ud2;
    }

    public double getUd3() {
        return ud3;
    }

    public double getUdk() {
        return udk;
    }

    public int getkStart() {
        return kStart;
    }

    public int getkEnd() {
        return kEnd;
    }

    public int[] getnValues() {
        return nValues;
    }

    public double[] getuValues() {
        return uValues;
    }
}
