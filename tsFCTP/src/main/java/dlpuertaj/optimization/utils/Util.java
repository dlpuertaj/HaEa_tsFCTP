package dlpuertaj.optimization.utils;

import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.domain.tsFCTP;

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
     * */
    public void exportInstanceToTextFile(tsFCTP instance){

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
}
