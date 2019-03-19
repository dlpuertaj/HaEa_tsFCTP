package dlpuertaj.optimization.utils;

import dlpuertaj.optimization.domain.tsFCTP;

import unalcol.random.integer.IntUniform;

import java.util.Arrays;

public class Generator {
    //TODO: Methdod that creates a tsFCTP instances generating the number od facilities based on a percentage
    //TODO: Search how supply chain facilities are distributed and how are distributed considering customers

    /***/
    public tsFCTP generateInstance(int plants, int distributors, int customers, int globalDemand){
        tsFCTP instance = new tsFCTP(plants,distributors,customers);

        int[][] productionPoints = generatePoints(plants, new int[plants][2],new int[plants][2]);
        int[][] distributionPoints = generatePoints(distributors, productionPoints, new int[distributors][2]);
        int[][] customerPoints = generatePoints(customers,distributionPoints,productionPoints);

        int[][] unitTransportCostFirstStage = generateUnitVariableCosts(productionPoints,distributionPoints);
        //new int[plants][distributors];
        int[][] unitTransportCostSecondStage = generateUnitVariableCosts(distributionPoints,customerPoints);
        //new int[plants][distributors];

        int[][] fixedCostFirstStage = generateFixedCosts(unitTransportCostFirstStage,
                Constants.FIXED_COST_LOWER_BOUND_FIRST_STAGE,Constants.FIXED_COST_UPPER_BOUND_FIRST_STAGE);//new int[plants][distributors];
        int[][] fixedCostSecondStage = generateFixedCosts(unitTransportCostSecondStage,
                Constants.FIXED_COST_LOWER_BOUND_SECOND_STAGE,Constants.FIXED_COST_UPPER_BOUND_SECOND_STAGE);//new int[plants][distributors];

        int[] productionCapacity = generatePlantSupply(plants);
        int[] customerDemand = generateCustomerDemand(customers);

        makeAddedDemandCoincideWithGlobalDemand(customerDemand,globalDemand);

        instance.setFixedCostS1(fixedCostFirstStage);
        instance.setFixedCostS2(fixedCostSecondStage);
        instance.setTransportCostS1(unitTransportCostFirstStage);
        instance.setTransportCostS2(unitTransportCostSecondStage);

        instance.setProductionCapacity(productionCapacity);
        instance.setCustomerDemand(customerDemand);

        for (int i = 0; i < distributors; i++) {
            instance.distributionCapacity[i] = globalDemand;
        }

        return instance;
    }

    /**plants and depots have been randomly generated in the [-400, 400] x [-400, 400] square according to
     * a discrete uniform distribution*/
    public int[][] generatePoints(int numberOfPoints, int[][] referencePointsOne, int[][] referencePointsTwo){

        int pointsGenerated = 1;
        int[][] points = new int[numberOfPoints][2];

        for (int i = 0; i < numberOfPoints; i++) {
            int[] point =  Util.generatePoint();
            points[i] = point;
            while(!Util.validatePoint(points,point,pointsGenerated-1) ||
                  !Util.validatePoint(referencePointsOne,point,pointsGenerated) ||
                  !Util.validatePoint(referencePointsTwo,point,pointsGenerated)){
                points[i] = Util.generatePoint();
            }
            pointsGenerated += 1;
        }
        return points;
    }

    /**
     * The fixed costs f_ij and g_jk are computed as b_ij X r_f and c_jk X r_g, respectively, where r_f
     * is randomly chosen as an integer in the interval [10,15] and r_g is randomly chosen as an integer in the interval
     * [5,10].
     * */
    public int[][] generateFixedCosts(int[][] unitVariableCosts, int loweBound, int upperBound){
        IntUniform rand = new IntUniform(loweBound,upperBound);

        int[][] fixedCost = new int[unitVariableCosts.length][unitVariableCosts[0].length];

        for (int i = 0; i < unitVariableCosts.length; i++) {
            for (int j = 0; j < unitVariableCosts[i].length; j++) {
                 fixedCost[i][j] = unitVariableCosts[i][j] * rand.next();
            }

        }
        return fixedCost;
    }

    /**Unit variable costs are equal to [dist(plant I, depot J)] (similarly, [dist(depot J, customer K)])
     * where dist(a, b) indicates the Euclidean distance between a and b, and  [·]  denotes the floor function
     * i.e. [x] is the largest integer not greater than x*/
    public int[][] generateUnitVariableCosts(int[][] source, int[][]target){

        int[][] unitTransportCosts = new int[source.length][target.length];
        for (int s = 0; s < source.length; s++) {
            for (int t = 0; t < target.length; t++) {
                unitTransportCosts[s][t] = (int) Math.floor(Util.euclideanDistance(source[s],target[t]));
            }
        }
        return unitTransportCosts;
    }

    /**
     * If the added demand of all the customers exceeds the global demand the demand
     * of every node is successively decreased of one unit until both numbers coincide.
     * A similar procedure is followed if the added demand is less than the global demand, but in this case the demand
     * is increased of one unit
     * */
    public void makeAddedDemandCoincideWithGlobalDemand(int[]customerDemand, int globalDemand){
        boolean decreaseOrEncrease = Util.addArrayContent(customerDemand) > globalDemand;
        for (int i = 0; i < customerDemand.length; i++) {
            if (decreaseOrEncrease)
                customerDemand[i] -= 1;
            else
                customerDemand[i] += 1;
            if(Util.addArrayContent(customerDemand) == globalDemand)
                break;
        }
    }

    /**
     * Customer demand is randomly selected as an integer in the
     * interval [10, 30].*/
    public int[] generateCustomerDemand(int customers){
        int[] customerDemand = new int[customers];
        IntUniform rand = new IntUniform(Constants.DEMAND_LOWER_BOUND,Constants.DEMAND_UPPER_BOUND);
        for (int i = 0; i < customers; i++) {
            customerDemand[i] = rand.next();
        }
        return customerDemand;
    }

    /**
     * Plant supply is 80 in all the plants.*/
    public int[] generatePlantSupply(int productionCenters){
        int[] customerDemand = new int[productionCenters];
        for (int i = 0; i < productionCenters; i++) {
            customerDemand[i] = Constants.FIXED_PLANT_SUPPLY;
        }
        return customerDemand;
    }
}
