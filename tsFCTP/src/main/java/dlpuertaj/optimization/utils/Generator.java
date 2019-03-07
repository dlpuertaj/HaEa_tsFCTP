package dlpuertaj.optimization.utils;

import dlpuertaj.optimization.domain.tsFCTP;

import unalcol.random.integer.IntUniform;

import java.util.Arrays;
import java.util.Random;

public class Generator {
    //TODO: Methdod that creates a tsFCTP instances generating the number od facilities based on a percentage
    //TODO: Method that generates fixed costs and random costs based on 2018 paper given the size of the problem
    //TODO: Search how supply chain facilities are distributed and how are distributed considering customers

    /***/
    public tsFCTP generateInstance(int plants, int distributors, int customers){
        tsFCTP instance = null;
        int[][] productionPoints = generatePoints(plants);
        int[][] distributionPoints = generatePoints(distributors);
        int[][] customerPoints = generatePoints(customers);

        //TODO: evaluate if two facilities (also of the same type) are in the same point


        return instance;
    }

    /***/
    public int[][] generatePoints(int numberOfPoints){

        int[][] points = new int[numberOfPoints][2];

        int x,y;

        IntUniform rand = new IntUniform(-400,400);

        for (int i = 0; i < numberOfPoints; i++) {
            x = rand.next();
            y = rand.next();
            points[i] = new int[]{x, y};
        }
        return points;
    }

    /**
     * The fixed costs f_ij and g_jk are computed as b_ij X r_f and c_jk X r_g, respectively, where r_f
     * is randomly chosen as an integer in the interval [10,15] and r_g is randomly chosen as an integer in the interval
     * [5,10].
     * */
    public int[][] generateFixedCosts(int[][] unitVariableCosts){
        return null;
    }

    /**Unit variable costs are equal to [dist(plant I, depot J)] (similarly, [dist(depot J, customer K)])
     * where dist(a, b) indicates the Euclidean distance between a and b, and  [·]  denotes the floor function
     * i.e. [x] is the largest integer not greater than x*/
    public int[][] generateUnitVariableCosts(int[][] source, int[][]target){

        int[][] unitTransportCosts = new int[source.length][target.length];
        for (int s = 0; s < source.length; s++) {
            for (int t = 0; t < target.length; t++) {
                unitTransportCosts[s][t] = (int) Math.floor(Util.euclideanDistance(source[s],target[t]));
            }
        }
        return null;
    }

    /***/
    public boolean validatePoints(int[] p1, int[] p2){
        return Arrays.equals(p1,p2);
    }

    /**
     *  I*J*K global supply global demand
     *  1 - 2 × 4 × 6 160 120
     *  2 - 2 × 4 × 8 160 160
     *  3 - 4 × 8 × 12 320 240
     *  4 - 4 × 8 × 16 320 320
     *  5 - 6 × 12 × 18 480 360
     *  6 - 6 × 12 × 24 480 480
     *  7 - 8 × 16 × 24 640 480
     *  8 - 8 × 16 × 32 640 640
     *  9 - 10 × 20 × 30 800 600
     * 10 - 10 × 20 × 40 800 800
     * 11 - 20 × 40 × 60 1600 1200
     * 12 - 20 × 40 × 80 1600 1600
     * 13 - 30 × 60 × 90 2400 1800
     * 14 - 30 × 60 × 120 2400 2400
     * 15 - 40 × 80 × 120 3200 2400
     * 16 - 40 × 80 × 160 3200 3200
     * 17 - 50 × 100 × 150 4000 3000
     * 18 - 50 × 100 × 200 4000 4000
     * 19 - 60 × 120 × 180 4800 3600
     * 20 - 60 × 120 × 240 4800 4800*/

    /**plants and depots have been randomly generated in the [−400, 400] × [−400, 400] square according to
     * a discrete uniform distribution*/

    /**
     * Plant supply is 80 in all the plants. Customer demand is randomly selected as an integer in the
     * interval [10, 30]. If the added demand of all the customers exceeds the global demand the demand
     * of every node is successively decreased of one unit until both numbers coincide.
     * A similar procedure is followed if the added demand is less than the global demand, but in this case the demand
     * is increased of one unit
     * */
}
