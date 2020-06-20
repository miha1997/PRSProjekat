package analitical;

import java.util.Scanner;

public class Main {
    private static boolean work = true;
    private static Scanner in = new Scanner(System.in);

    public static void main(String args[]){
        while(work){
            System.out.println("Izabrati jednu od opcija: \n");
            System.out.println("1) Izracunavanje normalizovanih potraznji");
            System.out.println("2) Odredjivanje parametara koriscenjem Bjuzenove metode");
            System.out.println("3) Kraj rada");


            int n = in.nextInt();

            switch (n){
                case 1: {
                    GNSolver.getInstance().calculateXDemands();
                    GNSolver.getInstance().saveInFile();

                    System.out.println("Rezultati su u fajlu /files/potraznje_analiticki.txt \n");
                    break;
                }
                case 2: {
                    BuzenSolver.getInstance().calculateParams();

                    System.out.println("Rezultati su u fajlu /files/rezultati_analiticki.txt \n");
                    break;
                }
                case 3: {
                    work = false;
                }
            }

        }
    }

}
