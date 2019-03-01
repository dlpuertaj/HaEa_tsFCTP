package dlpuertaj.optimization.utils;

import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import unalcol.types.collection.vector.Vector;

import java.util.Arrays;
import java.util.Random;

public class Distributor {
    
	/**
	 * Method that allocates random quantities to random nodes (facilities) considering capacities
     * It can allocate a quantity greater than or lesser than the total capacity
	 * @param capacities
	 * @param quantity
     * @return random allocation
	 * */
	public static int[] randomAllocationWithCapacities(int[] capacities,int quantity) {
        
		int[] allocated = new int[capacities.length];
		int[] availableNodes = new int[capacities.length];
		int nodes = availableNodes.length;
		int currentNode = 0;
		int randomQuantity;
		int referenceAmount = quantity;

		for(int i = 0 ; i < nodes ; i++){
		    availableNodes[i] = i;
        }

        Random rand = new Random();
		do{
            if(availableNodes.length == 1){// if there is only one node left, send all the remaining quantity
                //it must not be greater the the remaining capacity
                quantity = quantity > capacities[availableNodes[0]] ? capacities[availableNodes[0]] : quantity;
                allocated[availableNodes[0]] += quantity;
                nodes--;
            }else{
                currentNode = availableNodes[rand.nextInt(availableNodes.length)]; //random selection of node
                randomQuantity = rand.nextInt((referenceAmount - 1) + 1) + 1; // random quantity

                if(randomQuantity > quantity) //it must not be grater than the available quantity
                    randomQuantity = quantity;

                //it must not be greater the the remaining capacity
                if(randomQuantity > capacities[currentNode])
                    randomQuantity = capacities[currentNode];

                allocated[currentNode] += randomQuantity;
                capacities[currentNode] -= randomQuantity;

                quantity -= randomQuantity;
            }
            if(capacities[currentNode] == 0){
                if(quantity == 0 || nodes == 0)break;
                nodes -= 1;
                int counter = 0;
                availableNodes = new int[nodes];

                for (int i = 0; i < allocated.length ; i++) {
                    if(capacities[i] > 0){
                        availableNodes[counter] = i;
                        counter++;
                    }
                }
            }
        }while((quantity != 0) && (nodes != 0));
		return allocated;
    }
	
    /**
    * Method that gives an initial production quantity to all production centers.
    * Given a total demand, this method will add a random quantity to all production centers
    * choosing each one randomly until the total demand is 0 and without exceeding the 
    * production capacity of each production center. 
    * @param network 
    */
    public static void startProduction(TwoStageFlowNetwork network){

        if(network.totalDemand == network.totalProductionCapacity){
            network.quantityProduced = network.productionCapacity.clone();
            network.productionBalance = network.quantityProduced.clone();
        }else{
            
        	int quantity = network.totalDemand;
            int[] availablePlants = new int[network.I];
            
            for (int i = 0; i < availablePlants.length; i++) {
                availablePlants[i] = i;
            }
            
            network.quantityProduced = randomAllocationWithCapacities(network.productionCapacity.clone(), quantity);
            network.productionBalance = network.quantityProduced.clone();
        }
    }
    
    /**
    * Method used to allocate product from production centers to distribution centers
    * considering amount of product in each production center.
    * Remember that the distribution capacity is unlimited.
    * The method selects a random plant and the uses the random allocation algorithm
    * 
    * @param network
    */
    public static void firstStageInitialDistribution(TwoStageFlowNetwork network){
		
        int currentPlant;
        int quantity;
        int [] availableDCs = new int[network.J];
        Vector<Integer> availablePlants = new Vector<>();

        for (int i = 0; i < network.I; i++) {
            if(network.productionBalance[i] > 0) 
            	availablePlants.add(i);
        }
        
        for (int j = 0; j < network.J; j++) { 
            	availableDCs[j] = j;
        }
        
        Random randPlant = new Random();
        while(availablePlants.size() != 1){

        	int index = randPlant.nextInt(availablePlants.size());
            currentPlant = availablePlants.get(index);
            quantity = network.productionBalance[currentPlant];
            network.firstStage[currentPlant] = randomAllocationWithCapacities(network.distributionCapacity.clone(), quantity);
            
            network.productionBalance[currentPlant] = 0;
            
            for(int j = 0 ; j < network.J ; j++) {
            	network.distributionInbound[j] += network.firstStage[currentPlant][j]; 
            }
            availablePlants.del(currentPlant);       
        }
        
        if(availablePlants.size() == 1){

            quantity = network.productionBalance[availablePlants.get(0)];
            network.firstStage[availablePlants.get(0)] = randomAllocationWithCapacities(network.distributionCapacity.clone(),quantity);

        	network.productionBalance[availablePlants.get(0)] = 0;
            
            for(int j = 0 ; j < network.J ; j++) {
            	network.distributionInbound[j] += network.firstStage[availablePlants.get(0)][j]; 
            }
            availablePlants.del(availablePlants.get(0));
        }
    }
    
    /**
    *Method used to allocate product from random distribution centers to
    *random customers. This is done considering distribution center inbound and
    *customer demands
    *
    *@param network
    */
    public static void secondStageInitialDistribution(TwoStageFlowNetwork network){

    	Vector<Integer> availableDCS = new Vector<>();
    	int[] availableCustomers = new int[network.K];
    	int[] remainingDemand = network.customerDemand.clone();
    	int[] allocated;
       	int quantity;
    	int currentDC;
                      
        for (int j = 0; j < network.J; j++) {
            if(network.distributionInbound[j] > 0)
                availableDCS.add(new Integer(j));
        }
        for(int k = 0 ; k < network.K ; k++) {
        	availableCustomers[k] = k;
        }
        Random rand = new Random();
        while(availableDCS.size() != 0){//until all the product has been sent. But it can be until all DCs are balanced
        	//Select a distribution center and send all the in-bound product using random allocation

            currentDC = availableDCS.get(rand.nextInt(availableDCS.size()));
        	quantity = network.distributionInbound[currentDC];

        	allocated = randomAllocationWithCapacities(remainingDemand, quantity);

        	//update distribution out-bound and production balance
        	int customers = 0;
        	for(int k = 0 ; k < availableCustomers.length ; k++) {
        	    network.secondStage[currentDC][availableCustomers[k]] += allocated[k];
        		network.distributionOutbound[currentDC] += allocated[k];
        		network.customerBalance[availableCustomers[k]] -= allocated[k];
        		if(network.customerBalance[availableCustomers[k]] != 0)
        			customers++;
        	}
            availableDCS.del(currentDC);
        	if(availableDCS.size() != 0) {
	        	availableCustomers = new int[customers];
	        	remainingDemand = new int[customers];
	        	customers = 0;
	        	
	            //update available customers
	        	for(int k = 0 ; k < network.K ; k++) {
	        		if(network.customerBalance[k] != 0) {
	        			availableCustomers[customers] = k;
	        			remainingDemand[customers] = network.customerBalance[k];
	        			customers++;
	        		}
	        	}
        	}
        }
    }


    /**
     * Method that replaces the second stage of a network with a new distribution plan.
     * Updating the distribution outbound and the customer balance (just in case)
     * @param plan
     * @param network
     */
    public static void importSecondStagePlan(int [][] plan, TwoStageFlowNetwork network){
        System.arraycopy(network.customerDemand,0,network.customerBalance,0,network.K);
        for (int j = 0; j < network.J; j++) {
            network.distributionOutbound[j] = 0;
            for (int k = 0; k < network.K; k++) {
                network.distributionOutbound[j] += plan[j][k];
                network.customerBalance[k] -= plan[j][k];
            }
            System.arraycopy(plan[j],0,network.secondStage[j],0,network.K);
        }
    }

    /**
    * Method that returns random amounts of product from a distribution center
    * to production centers (through random positive edges) using the random allocation algorithm
    * 
    * @param dc
    * @param quantity
    * @param network
    */    
    public static  void returnProduct(int dc, int quantity,TwoStageFlowNetwork network) {
        
    	int pc = 0;
    	int[] productionCenters; 
        int[] capacities;
        for (int i = 0 ; i < network.I ; i++) {
            if(network.firstStage[i][dc] > 0)//Only edges with positive flow
                pc++;
        }
        productionCenters = new int[pc];
        capacities = new int[pc];
        pc = 0;

        for (int i = 0 ; i < network.I ; i++) {
            if(network.firstStage[i][dc] > 0) {//Only edges with positive flow
                productionCenters[pc] = i;
                capacities[pc] = network.firstStage[i][dc];
            	pc++;
            }
        }

        int[] allocated = randomAllocationWithCapacities(capacities, quantity);

        for(int i = 0 ; i < allocated.length ; i++) {
        	network.firstStage[productionCenters[i]][dc] -= allocated[i];
        	network.productionBalance[productionCenters[i]] += allocated[i];
        	network.distributionInbound[dc] -= allocated[i];
        }
    }

    /**
    * Method used to balance the first stage of the flow network after the second stage swap
    * and the return of the product are applied. The method uses the random allocation algorithms
    * to send product from a production center to distribution centers with negative balance
    * @param: productionCenter
    * @param: quantity
    * @param: network 
    **/
    public static void firstStageXOverBalance(int productionCenter,TwoStageFlowNetwork network) {
    	// of product quantities to balance and edges
        int dcs = 0;

        //Add the edges with negative balance to the edge vector, and the product difference in the balance vector
        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] < network.distributionOutbound[j]){
                dcs++;
            }
        }

        int[] distributionBalance = new int[dcs];
        int[] distributionCenters = new int[dcs];

        dcs = 0;
        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] < network.distributionOutbound[j]){
                distributionBalance[dcs] = (network.distributionInbound[j] - network.distributionOutbound[j])*-1;
                distributionCenters[dcs] = j;
                dcs++;
            }
        }

        int[] allocated = randomAllocationWithCapacities(distributionBalance,network.productionBalance[productionCenter]);

        /*pdate production balance. I can do this without using a for loop.
        * test using for loop and then change*/
        for(int j = 0 ; j < distributionCenters.length ; j++ ){
            network.productionBalance[productionCenter] -= allocated[j];
        }
        //update first stage matrix
        for(int j = 0 ; j < distributionCenters.length ; j++ ){
            network.firstStage[productionCenter][distributionCenters[j]] += allocated[j];
        }
        //update distribution centers inbound
        for(int j = 0 ; j < distributionCenters.length ; j++ ){
            network.distributionInbound[distributionCenters[j]] += allocated[j];
        }
    }

    /**
     * Method that closes a distribution center and returns all the product passing through it.
     * It return the product from the customers to the production centers.
     *
     * @param dc : int
     * @param network : TwoStageFlowNetwork
     */
    public static void closeDistributionCenter(int dc, TwoStageFlowNetwork network){

        network.distributionInbound[dc] = 0;
        network.distributionOutbound[dc] = 0;
        for (int k = 0; k < network.K; k++) {
            network.customerBalance[k] += network.secondStage[dc][k];
            network.secondStage[dc][k] = 0;
        }
        for (int i = 0; i < network.I; i++) {
            network.productionBalance[i] += network.firstStage[i][dc];
            network.firstStage[i][dc] = 0;
        }
    }

    /**
    * Method that balances the first stage of a network after a distribution center had been closed
    *
    * @param pc
    * @param network
    *
    * TODO: implement unit test
    */
    public static void firstStageDistributionBalance(int pc, int closed,TwoStageFlowNetwork network) {
        int distributionCenters = 0;

        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] >= 0 && j != closed)
                distributionCenters++;
        }

        int[] available = new int[distributionCenters];
        int[] capacities = new int[distributionCenters];
        distributionCenters = 0;
        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] >= 0 && j != closed) {
                available[distributionCenters] = j;
                capacities[distributionCenters] = network.totalDemand;
                distributionCenters++;
            }
        }

        int[] allocated = randomAllocationWithCapacities(capacities,network.productionBalance[pc]);

        network.productionBalance[pc] = 0;
        for(int j = 0 ; j < allocated.length ; j++){
             network.firstStage[pc][available[j]] += allocated[j];
             network.distributionInbound[available[j]] += allocated[j];
        }
    }

    /**
     * Method that balances the second stage of a network after a the balance of the first stage
     *
     * @param dc
     * @param network
     */
    public static void secondStageDistributionBalance(int dc,TwoStageFlowNetwork network) {
        int customers = 0;
        for (int balance : network.customerBalance) {
            if(balance > 0)
                customers++;
        }

        int[] available = new int[customers];
        int[] customersBalance = new int[customers];
        customers = 0;
        for (int k = 0 ; k < network.K ; k++) {
            if(network.customerBalance[k] > 0) {
                available[customers] = k;
                customersBalance[customers] = network.customerBalance[k];
                customers++;
            }
        }

        int[] allocated = randomAllocationWithCapacities(customersBalance,network.distributionInbound[dc]-network.distributionOutbound[dc]);

        for(int k = 0 ; k < available.length ; k++){
            network.secondStage[dc][available[k]] += allocated[k];
            network.distributionOutbound[dc] += allocated[k];
            network.customerBalance[available[k]] -= allocated[k];
        }
    }
    
   /**
    * Method that allocates random quantities to random distribution centers from one production center
    * @param pc
    * @param network;
    */    
    public static void randomAllocationFromProductionCenter(int pc,TwoStageFlowNetwork network) {
        int[] capacities = new int[network.J];

        for (int i = 0; i < network.J; i++) {
            capacities[i] = network.totalDemand;
        }

        //TODO: first I need to return the product to de production center
        int [] allocated = randomAllocationWithCapacities(capacities,network.productionBalance[pc]);

        for (int j = 0; j < network.J; j++) {
            network.firstStage[pc][j] = allocated[j];
            network.distributionInbound[j] += allocated[j];
        }
        network.productionBalance[pc] = 0;
    }

    /***
     * Method that return product from customers tu distribution centers that are unbalanced
     * @param network
     */
    public static void returnToUnbalancedDistributionCenters(TwoStageFlowNetwork network) {
        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] != network.distributionOutbound[j]){
                for (int k = 0 ; k < network.K ; k++) {
                    network.customerBalance[k] += network.secondStage[j][k];
                    network.distributionOutbound[j] -= network.secondStage[j][k];
                    network.secondStage[j][k] = 0;
                }
            }
        }
    }

    /**
     * Method that closes a production center returning all the product from distribution centers
     * @param productionCenter
     * @param network
     * */
    public static void closeProductionCenter(int productionCenter,TwoStageFlowNetwork network){
        for (int j = 0 ; j < network.J ; j++) {
            network.distributionInbound[j] -= network.firstStage[productionCenter][j];
            network.productionBalance[productionCenter] += network.firstStage[productionCenter][j];
            network.firstStage[productionCenter][j] = 0;
        }
    }
    
    /*
    *
    */ 
    public static void leastCostAllocation(int[][] leastCostMatrix, TwoStageFlowNetwork n){
	   
		
        int leastCost;
        int count = 0;
        int s;
        int d;
            
        int[] supply;
        int[] demand;
        
        if(leastCostMatrix.length == n.I && leastCostMatrix[0].length == n.J){
        	supply = n.quantityProduced.clone();
        	demand = n.distributionCapacity.clone();
        }else{
        	supply = n.distributionInbound.clone();
        	demand = n.customerDemand.clone();
        }
        
        while(count != n.totalDemand){
            s = 0;
            d = 0;
            leastCost = Integer.MAX_VALUE;
            for (int i = 0; i < leastCostMatrix.length; i++) {
                if(supply[i] != 0){
                    
                    for (int j = 0; j < leastCostMatrix[i].length; j++) {
                        if(demand[j] != 0){
                            
                            if(leastCostMatrix[i][j] < leastCost){
                                leastCost = leastCostMatrix[i][j];
                                s = i; d = j;
                                
                            }else if(leastCostMatrix[i][j] == leastCost){ 
                                //tie break
                                if((demand[j] - supply[i]) > (demand[d] - supply[s])){
                                    s = i; d = j;
                                    
                                }
                            }
                        }
                    }
                }
            }
            int quantity = supply[s];
            if(quantity > demand[d])
                quantity = demand[d];
            

            supply[s] -= quantity;
            demand[d] -= quantity;
            count += quantity;
            
            
            if(leastCostMatrix.length == n.I && leastCostMatrix[0].length == n.J){
            	n.firstStage[s][d] +=  quantity;
            	n.distributionInbound[d] += quantity;
            }else{
            	n.secondStage[s][d] +=  quantity;
            	n.distributionOutbound[s] += quantity;
            	n.customerBalance[d] -= quantity;
            }

        }
    }
    
    /*
    *
    */ 
    public static Vector<int[]> findSupplyPenalties(int[][] costMatrix, int[]supply, int[] demand){
        
    	int col = 0;
        int supplyMin = 0;
        int supplyNextMin = 0;
        
        int[] supplyPenalties = new int[supply.length];
        int[] demandMin       = new int[demand.length];
        int[] demandNextMin   = new int[demand.length];
        
        Vector<int[]> penalties = new Vector<>();
        
        Arrays.fill(demandMin, Integer.MAX_VALUE);
        Arrays.fill(demandNextMin, Integer.MAX_VALUE);
        
        for (int i = 0; i < costMatrix.length; i++) {
            
            if(supply[i] != 0){
                supplyMin = Integer.MAX_VALUE;
         
                supplyNextMin = supplyMin;
                
                for (int c = 0; c < costMatrix[i].length; c++) {
                    if(demand[c] != 0){
                        //Supply min cost and next min cost
                        if(costMatrix[i][c] < supplyMin){
                            supplyNextMin = supplyMin;
                            supplyMin = costMatrix[i][c]; //It's the minimum cost
                        }else if(costMatrix[i][c] < supplyNextMin && costMatrix[i][c] != supplyMin)
                            supplyNextMin = costMatrix[i][c];
                        
                        //Demand min cost and next min cost
                        if(costMatrix[i][c] < demandMin[c]){
                            demandNextMin[c] = demandMin[c];
                            demandMin[c] = costMatrix[i][c];
                        }else if(costMatrix[i][c] < demandNextMin[c] && costMatrix[i][c] != demandMin[c]){
                            demandNextMin[c] = costMatrix[i][c];
                        }
                    }
                    
                }
                if(supplyNextMin == Integer.MAX_VALUE){
                    supplyNextMin = 0;
                    supplyPenalties[col] = -1*(supplyNextMin - supplyMin);
                }else
                    supplyPenalties[col] = supplyNextMin - supplyMin;
                col++;
            }else{
                supplyPenalties[i] = 0;
                col++;
            }
        }
        for (int i = 0; i < demandMin.length; i++) {
            if(demandNextMin[i] == Integer.MAX_VALUE)
                demandNextMin[i] = 0;
            else
                demandNextMin[i] -= demandMin[i];
        }
        
        penalties.add(supplyPenalties);
        penalties.add(demandNextMin);
        return penalties;
    }
	
    /*
    *
    */ 
    public static void VogelsApproximation(int[][] costMatrix, TwoStageFlowNetwork network) {


        int quantity;
        int[] supply;
        int[] demand;
        int[] supplyPenalties;
        int[] demandPenalties;

        if (costMatrix.length == network.I && costMatrix[0].length == network.J) {

            supply = network.quantityProduced.clone();
            demand = network.distributionCapacity.clone();

        } else {
            supply = network.distributionInbound.clone();
            demand = network.customerDemand.clone();
        }

        int count = 0;
        int row;
        int col;

        Vector<int[]> penalties;
        while (count != network.totalDemand) {

            penalties = findSupplyPenalties(costMatrix, supply, demand);

            supplyPenalties = penalties.get(0);
            demandPenalties = penalties.get(1);
            row = 0;
            int maxPenalty = supplyPenalties[0];
            for (int j = 1; j < supplyPenalties.length; j++) {//Max penalty of supplies
                if (supplyPenalties[j] > maxPenalty) {
                    maxPenalty = supplyPenalties[j];
                    row = j;
                }
            }

            col = 0;
            maxPenalty = demandPenalties[0];
            for (int j = 1; j < demandPenalties.length; j++) {//Max penalty of supplies
                if (demandPenalties[j] > maxPenalty) {
                    maxPenalty = demandPenalties[j];
                    col = j;
                }
            }

            boolean choice = false;
            //Tie break 2;
            if (supplyPenalties[row] == demandPenalties[col]) {
                if (costMatrix.length != network.I) {
                    if (supply[row] > demand[col])
                        choice = true;
                }
            } else if (supplyPenalties[row] > demandPenalties[col])
                choice = true;

            int minimumCost;

            //Find minimum cost
            if (choice) {
                col = 0;
                minimumCost = Integer.MAX_VALUE;
                for (int j = 0; j < costMatrix[row].length; j++) {
                    if (costMatrix[row][j] <= minimumCost && demand[j] != 0) {
                        minimumCost = costMatrix[row][j];
                        col = j;
                    }
                }
            } else {
                row = 0;
                minimumCost = Integer.MAX_VALUE;
                for (int j = 0; j < costMatrix.length; j++) {
                    if (costMatrix[j][col] < minimumCost && supply[j] != 0) {
                        minimumCost = costMatrix[j][col];
                        row = j;
                    }
                }

            }

            //Send product
            if (demand[col] > supply[row]) {
                quantity = supply[row];
                supply[row] -= quantity;
                demand[col] -= quantity;
            } else {
                quantity = demand[col];
                supply[row] -= quantity;
                demand[col] -= quantity;
            }

            if (costMatrix.length == network.I && costMatrix[0].length == network.J) {
                network.firstStage[row][col] += quantity;
                network.distributionInbound[col] += quantity;
            } else {
                network.secondStage[row][col] += quantity;
                network.distributionOutbound[row] += quantity;
                network.customerBalance[col] -= quantity;
            }

            count += quantity;

        }
    }
        
}