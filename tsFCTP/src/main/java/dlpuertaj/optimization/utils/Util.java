package dlpuertaj.optimization.utils;

public class Util {

    public static double euclideanDistance(int[] p1, int[]p2){
        double xx = Math.pow(p1[0]-p2[0],2);
        double yy = Math.pow(p1[1]-p2[1],2);

        return Math.sqrt(xx + yy);
    }
}
