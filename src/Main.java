import java.io.*;
import java.util.*;

public class Main {
    static int nr;  //nr skladowej wektora
    static int iloscDanych;
    static List<double[]> wektory=new ArrayList<>();
    static Scanner scanner=null;

    public static void main(String[] args) {
         String path=args[0];   //Iris.csv
         int k = Integer.parseInt(args[1]); //4

         final int maxIteracji = 100;
         try{
             zaladuj(path);
         }catch(Exception e){
             System.out.println("Błąd pliku #1");
             e.printStackTrace();
         }
         if(k<2) {
             System.out.println("Za mała liczba grup");
             System.exit(1);
         }else if(k>iloscDanych){
             System.out.println("Za duża liczba grup");
             System.exit(1);
         }

         Map<Integer, double[]> centroidy = new HashMap<>();
         poczatkoweCentroidy(k,centroidy);
         Map<double[], Integer> klastry; //indeksy grup
         klastry = kmeans(wektory, centroidy, k);

         //przypisywanie do nowych klastrow
         double[] tab;
         for (int i = 0; i < maxIteracji; i++) {
             for (int j = 0; j < k; j++) {
                 List<double[]> list = new ArrayList<>();
                 for (double[] key : klastry.keySet()) {
                     if (klastry.get(key)==j) list.add(key);
                 }
                 tab = centroidLiczenie(list);
                 centroidy.put(j, tab);
             }
             klastry.clear();
             klastry = kmeans(wektory, centroidy, k);
         }
         print(klastry);
    }

    static void zaladuj(String s){
        try {
            scanner=new Scanner(new File(s));
            while(scanner.hasNextLine()) {
                String[] newString = scanner.nextLine().split(",");
                double[] czesci = new double[newString.length - 1];
                nr = czesci.length;
                for (int i = 0; i < czesci.length; i++)
                    czesci[i] = Double.parseDouble(newString[i]);
                wektory.add(czesci);
                iloscDanych++;
            }
        } catch (Exception e) {
            System.out.println("Błąd pliku #2");
            e.printStackTrace();
        }
    }

    public static void poczatkoweCentroidy(int k, Map<Integer, double[]> centroidy){
        for (int i = 0; i < k; i++) {
            double[] tab=wektory.get(i);
            centroidy.put(i, tab);
        }
    }

    public static double odleglosc(double[] x, double[] y) {
        double d = 0;
        for(int i = 0; i < x.length; i++) {
            d = d+ ((x[i] - y[i]) * (x[i] - y[i]));
        }
        d=Math.sqrt(d);
        return d;
    }

    public static double[] centroidLiczenie(List<double[]> list) {
        double[] centroidy = new double[nr];
        int ilosc;
        double suma;
        for (int i = 0; i < centroidy.length; i++) {
            suma=0;
            ilosc=0;
            for(double[] x:list){
                ilosc++;
                suma=suma+x[i];
            }
            centroidy[i]=(suma/ilosc);
        }
        return centroidy;
    }
    //przypisywanie wektorow do grup iponowne przypisywanie
    public static Map<double[], Integer> kmeans(List<double[]> wektory, Map<Integer, double[]> centroidy, int k) {
        Map<double[], Integer> noweklastry = new HashMap<>();
        int newK=0;
        double dist;
        for(double[] x:wektory) {
            double minimum = Double.MAX_VALUE;
            for (int j = 0; j < k; j++) {
                dist=odleglosc(centroidy.get(j), x);
                if (dist<minimum) {
                    minimum=dist;
                    newK=j;
                }
            }
            noweklastry.put(x, newK);
        }
        return noweklastry;
    }

    public static void print(Map<double[], Integer> klastry){
        for (double[] key : klastry.keySet()) {
            for (double v : key) {
                System.out.print(v + " ");
            }
            System.out.print(" => GRUPA NR: "+klastry.get(key) + "\n");
        }
    }
}