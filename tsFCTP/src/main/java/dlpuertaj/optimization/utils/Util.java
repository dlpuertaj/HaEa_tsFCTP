package dlpuertaj.optimization.utils;

import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.domain.tsFCTP;
import unalcol.random.integer.IntUniform;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Util {

    /***/
    public static double euclideanDistance(int[] p1, int[]p2){
        double xx = Math.pow(p1[0]-p2[0],2);
        double yy = Math.pow(p1[1]-p2[1],2);

        return Math.sqrt(xx + yy);
    }

    /***/
    public static int addArrayContent(int[] array){
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        return sum;
    }

    /** This method creates a text file with the instance properties ina format that is ready to read
     * # of productioncenters I
     * # of distribution centers J
     * # of customers K
     * first stage unit transport costs
     * first stage fixed costs
     * second stage unit transport costs
     * second stage fixed costs
     * production capacity
     * demand of customers
     * https://www.journaldev.com/878/java-write-to-file
     * https://www.mkyong.com/java/how-to-write-to-file-in-java-bufferedwriter-example/
     * */
    public static void exportInstanceToTextFile(tsFCTP instance){

        BufferedWriter bw = null;
        FileWriter fw = null;
        StringBuilder sb = new StringBuilder();
        String NEWLINE = System.getProperty(Constants.STRING_BUILDER_NEW_LINE);

        String filename = Constants.NEW_INSTANCE_FILE_PATH+instance.I+"-"+instance.J+"-"+instance.K+Constants.TXT;
        try {

            sb.append(instance.I);
            sb.append(NEWLINE);
            sb.append(instance.J);
            sb.append(NEWLINE);
            sb.append(instance.K);
            sb.append(NEWLINE);

            for (int i = 0; i < instance.I; i++) {
                for (int j = 0; j < instance.J; j++) {
                    sb.append(instance.firstStageTransportCost[i][j]);
                    sb.append(NEWLINE);
                }
            }
            for (int i = 0; i < instance.I; i++) {
                for (int j = 0; j < instance.J; j++) {
                    sb.append(instance.firstStageFixedCost[i][j]);
                    sb.append(NEWLINE);
                }
            }


            for (int j = 0; j < instance.J; j++) {
                for (int k = 0; k < instance.K; k++) {
                    sb.append(instance.secondStageTransportCost[j][k]);
                    sb.append(NEWLINE);
                }
            }
            for (int j = 0; j < instance.J; j++) {
                for (int k = 0; k < instance.K; k++) {
                    sb.append(instance.secondStageFixedCost[j][k]);
                    sb.append(NEWLINE);
                }
            }

            for (int i = 0; i < instance.I; i++) {
                sb.append(instance.productionCapacity[i]);
                sb.append(NEWLINE);
            }

            for (int k = 0; k < instance.K; k++) {
                sb.append(instance.customerDemand[k]);
                sb.append(NEWLINE);
            }

            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw);
            bw.write(sb.toString());

            System.out.println(instance.I+"-"+instance.J+"-"+instance.K+"Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }

    /** This method creates a text file with the network properties and transported product
     * # of productioncenters I
     * # of distribution centers J
     * # of customers K
     * unit distributed from I to J
     * unit distributed from J to K
     * */
    public void exportNetworkToTextFile(TwoStageFlowNetwork instance){

    }

    /***/
    public static int[] generatePoint() {
        IntUniform rand = new IntUniform(Constants.POINTS_LOWER_BOUND,Constants.POINTS_UPPER_BOUND);
        int x = rand.next();
        int y = rand.next();
        return new int[]{x, y};
    }

    /**
     * Method that compares a given point with all the points in a matrix
     * @param points
     * @param p */
    public static boolean validatePoint(int[][] points,int[] p, int numberOfPoints) {

        for (int i = 0; i < numberOfPoints; i++) {
            if(i < points.length) {
                if (Arrays.equals(points[i], p))
                    return false;
            }
        }
        return true;
    }
}