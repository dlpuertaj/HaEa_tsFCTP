 package dlpuertaj.optimization.utils;

import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import unalcol.types.collection.vector.Vector;

import java.util.Arrays;
import java.util.Random;


public class Distributor {
    
	/**
	 * Method that allocates random quantities to random nodes (facilities) considering capacities
	 * @param capacities
	 * @param availableNodes
	 * @param quantity
	 * */
	public static int[] randomAllocationWithCapacities(int[] capacities, int[] availableNodes,int quantity) {
        
		int[] allocated = new int[capacities.length];
		
		Random rand = new Random();
		int nodes = availableNodes.length;
		int currentNode = 0;
		int randomQuantity = 0;
		int referenceAmount = quantity;
		while(quantity != 0){
            
            if(availableNodes.length == 1){// if there is only one node left, send all the remaining quantity
 	
            	allocated[availableNodes[0]] += quantity;
            	break;
                
            }else{
                currentNode = availableNodes[rand.nextInt(availableNodes.length)]; //random selection of node
                randomQuantity = rand.nextInt((referenceAmount - 1) + 1) + 1; // random quantity
                
                if(randomQuantity > quantity) //it must not be grater than the available quantity
                    randomQuantity = quantity;

                //it must not be greater the the remaining capacity
                if(randomQuantity > capacities[currentNode] - allocated[currentNode])
                    randomQuantity = capacities[currentNode] - allocated[currentNode];
                
                allocated[currentNode] += randomQuantity;
                quantity -= randomQuantity;
                
                if(quantity == 0)break;
                
                if(capacities[currentNode] - allocated[currentNode] == 0){
                	nodes -= 1;
                    int counter = 0;
                    availableNodes = new int[nodes];
                    for (int i = 0; i < capacities.length ; i++) {
                        if(capacities[i] - allocated[i] != 0){
                        	availableNodes[counter] = i;
                            counter++;
                        }
                    } 
                }
            }
        }
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
            
            network.quantityProduced = randomAllocationWithCapacities(network.productionCapacity, availablePlants, quantity);
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
        	//Select a plant
            currentPlant = availablePlants.get(index);
            quantity = network.productionBalance[currentPlant];
            
            network.firstStage[currentPlant] = randomAllocationWithCapacities(network.distributionCapacity,availableDCs, quantity);
            
            network.productionBalance[currentPlant] = 0;
            
            //calculate ditribution inbound
            for(int j = 0 ; j < network.J ; j++) {
            	network.distributionInbound[j] += network.firstStage[currentPlant][j]; 
            }
            
            availablePlants.del(currentPlant);       
        }
        
        if(availablePlants.size() == 1){

            quantity = network.productionBalance[availablePlants.get(0)];
            network.firstStage[availablePlants.get(0)] = randomAllocationWithCapacities(network.distributionCapacity,availableDCs, quantity);

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
       	int quantity;
    	int currentDC = 0;
                      
        for (int j = 0; j < network.J; j++) {
            if(network.distributionInbound[j] > 0)
                availableDCS.add(new Integer(j));
        }
        for(int k = 0 ; k < network.K ; k++) {
        	availableCustomers[k] = k;
        }
        
        Random rand = new Random();
                
        while(availableDCS.size() != 0){//until all the product has been sent. But it can be until all DC�s are balanced
        	
        	//Select a distribution center and send all the in-bound product using random allocation
        	currentDC = availableDCS.get(rand.nextInt(availableDCS.size()));
        	
        	quantity = network.distributionInbound[currentDC];
            
        	network.secondStage[currentDC] = randomAllocationWithCapacities(network.customerBalance, availableCustomers, quantity);
        	
            //update available distribution centers
        	availableDCS.del(currentDC);
        	
        	//update distribution out-bound and nproduction balance
        	int customers = 0;
        	for(int k = 0 ; k < network.secondStage[currentDC].length ; k++) {
        		network.distributionOutbound[currentDC] += network.secondStage[currentDC][k];
        		network.customerBalance[k] -= network.secondStage[currentDC][k];
        		if(network.customerBalance[k] != 0)
        			customers++;
        	}    	
        	
        	if(availableDCS.size() != 0) {
	        	availableCustomers = new int[customers];
	        	customers = 0;
	        	
	            //update available customers
	        	for(int k = 0 ; k < network.K ; k++) {
	        		if(network.customerBalance[k] != 0) {
	        			availableCustomers[customers] = k;
	        			customers++;
	        		}
	        	}
        	}      	
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
        
        int[] allocated = randomAllocationWithCapacities(capacities, productionCenters, quantity);
        
        for(int i = 0 ; i < allocated.length ; i++) {
        	network.firstStage[i][dc] -= allocated[productionCenters[i]];
        	network.productionBalance[productionCenters[i]] += allocated[productionCenters[i]];
        	network.distributionInbound[dc] -= allocated[productionCenters[i]];
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
    public static void firstStageXOverBalance(int productionCenter,int quantity, TwoStageFlowNetwork network) {
    	//Vector of product quantities to balance and edges
        int[] distributionBalance;
        int[] distributionCenters;
        int dcs = 0;

        //Add the edges with negative balance to the edge vector, and the product difference in the balance vector
        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] < network.distributionOutbound[j]){
                dcs++;
            }
        }

        distributionBalance = new int[dcs];
        distributionCenters = new int[dcs];
        dcs = 0;
        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] < network.distributionOutbound[j]){
                distributionBalance[dcs] = network.distributionInbound[j] - network.distributionOutbound[j];
                distributionCenters[dcs] = j;
                dcs++;
            }
        }

        int[] allocated = randomAllocationWithCapacities(distributionBalance,distributionCenters,quantity);

        /*TODO: update production balance. I can do this without using a for loop.
        * test using for loop and then change*/
        for(int j = 0 ; j < distributionCenters.length ; j++ ){
            network.productionBalance[productionCenter] -= allocated[j];
        }
        //TODO: update first stage matrix
        for(int j = 0 ; j < distributionCenters.length ; j++ ){
            network.firstStage[productionCenter][distributionCenters[j]] += allocated[j];
        }
        //TODO: update distribution centers inbound
        for(int j = 0 ; j < distributionCenters.length ; j++ ){
            network.distributionInbound[distributionCenters[j]] += allocated[j];
        }
    }
    
    /*
    *
    */
    public static void firstStageDistributionBalance(int plant,int quantity, TwoStageFlowNetwork network) {
        Vector<Integer> edges = new Vector<>();
        for (int j = 0 ; j < network.J ; j++) {
            if(network.distributionInbound[j] > 0)
                edges.add(j);
        }
        
        int Q = quantity;
        int randomQuantity;
        int [] randomEdges = new int[edges.size()];
        Random rand = new Random();
        
        while(quantity != 0){
        	
            //UniformIntegerGenerator edgeSelector = new UniformIntegerGenerator(edges.size());
            //randomEdges = edgeSelector.generate(edges.size());

            if(edges.size() == 1){
                network.productionBalance[plant] -= quantity;
                network.firstStage[plant][edges.get(0)] += quantity;
                network.distributionInbound[edges.get(0)] += quantity;
                quantity = 0;
            }else{
                for (int i = 0; i < edges.size(); i++) {
                randomEdges[i] = rand.nextInt(edges.size());
                }
                for (int e : randomEdges) {
                    randomQuantity = rand.nextInt((Q - 1) + 1) + 1;
                    randomQuantity = randomQuantity > quantity ? quantity : randomQuantity;

                    network.productionBalance[plant] -= randomQuantity;
                    network.firstStage[plant][edges.get(e)] += randomQuantity;
                    network.distributionInbound[edges.get(e)] += randomQuantity;

                    quantity -= randomQuantity;
                    if(quantity == 0)break;
                }
            }
        }
    }

    /*
    *
    */
    public static void secondStageDistributionBalance(int dc, int quantity, TwoStageFlowNetwork network) {
        Vector<Integer> balanceQuantities = new Vector<>();
        Vector<Integer> edges = new Vector<>();
        for (int k = 0 ; k < network.K ; k++) {
            if(network.customerBalance[k] > 0){
                balanceQuantities.add(network.customerBalance[k]);
                edges.add(k);
            }
        }
        
        int Q = quantity;
        int randomQuantity;
        int [] randomEdges = new int[edges.size()];
        Random rand = new Random();
        while(quantity != 0){
        	
            //UniformIntegerGenerator edgeSelector = new UniformIntegerGenerator(edges.size());
            //randomEdges = edgeSelector.generate(edges.size());
            for (int i = 0; i < edges.size(); i++) {
                randomEdges[i] = rand.nextInt(edges.size());
            }
            
            
            for (int e : randomEdges) {
                randomQuantity = rand.nextInt((Q - 1) + 1) + 1;
                randomQuantity = randomQuantity > quantity ? quantity : randomQuantity;
                randomQuantity = randomQuantity > balanceQuantities.get(e) ? balanceQuantities.get(e) : randomQuantity; 

                network.distributionOutbound[dc] += randomQuantity;
                network.secondStage[dc][edges.get(e)] += randomQuantity;
                network.customerBalance[edges.get(e)] -= randomQuantity;

                balanceQuantities.set(e, balanceQuantities.get(e)-randomQuantity);
                quantity -= randomQuantity;
                if(balanceQuantities.get(e) == 0){
                    balanceQuantities.del(e);
                    edges.del(e);
                    //Location<Integer> locator = edges.find(edges.get(e));
                    //edges.del(locator);
                    randomEdges = new int[edges.size()];
                    break;
                }
                if(quantity == 0)break;
            }
        }
    }
    
    /*
    *
    */
    public static void importFirstStagePlan(int [][] plan, TwoStageFlowNetwork network){
        int[] dcIn = new int[network.J];
        for (int i = 0; i < network.I; i++) {
            network.quantityProduced[i] = 0;
            for (int j = 0; j < network.J; j++) {
                network.quantityProduced[i] += plan[i][j];
                dcIn[j] += plan[i][j];
            }
            network.firstStage[i] = plan[i].clone();
        }
        network.distributionInbound = dcIn.clone();
    }
    
    /*
    *
    */
    public static void importSecondStagePlan(int [][] plan, TwoStageFlowNetwork network){
        int[] dcOut = new int[network.J];
        for (int j = 0; j < network.J; j++) {
            network.distributionOutbound[j] = 0;
            for (int k = 0; k < network.K; k++) {
                network.distributionOutbound[j] += plan[j][k];
                dcOut[j] += plan[j][k];
            }
            network.secondStage[j] = plan[j].clone();
        }
        network.distributionOutbound = dcOut.clone();
    }
    
    /*
    *
    */
    public static void closeDistributionCenter(int DC, TwoStageFlowNetwork network){
		
        network.distributionInbound[DC] = 0;
        network.distributionOutbound[DC] = 0;
        for (int k = 0; k < network.K; k++) {
            network.customerBalance[k] += network.secondStage[DC][k];
            network.secondStage[DC][k] = 0;
        }
        for (int i = 0; i < network.I; i++) {
            network.productionBalance[i] += network.firstStage[i][DC];
            network.firstStage[i][DC] = 0;
        }
    }
    
   /*
    *
    */    
    public static void randomPlantTransportation(int plant, int quantity, TwoStageFlowNetwork network) {
        int[] edges = new int[network.J];
        for (int j = 0 ; j < network.J ; j++) {
            network.distributionInbound[j] -= network.firstStage[plant][j];
            network.productionBalance[plant] += network.firstStage[plant][j];
            network.firstStage[plant][j] = 0;
            edges[j] = j;
        }
        
        int Q = quantity;
        int randomQuantity;
        int [] randomEdges = new int[edges.length];
        Random rand = new Random();
        while(quantity != 0){
        	
            //UniformIntegerGenerator edgeSelector = new UniformIntegerGenerator(edges.size());
            //randomEdges = edgeSelector.generate(edges.size());
            for (int i = 0; i < edges.length; i++) {
                randomEdges[i] = rand.nextInt(edges.length);
            }
            if(edges.length == 1){
                network.productionBalance[plant] -= quantity;
                network.firstStage[plant][edges[0]] += quantity;
                network.distributionInbound[edges[0]] += quantity;
                quantity = 0;
            }else{
                for (int e : randomEdges) {
                    randomQuantity = rand.nextInt((Q - 1) + 1) + 1;
                    randomQuantity = randomQuantity > quantity ? quantity : randomQuantity;

                    network.productionBalance[plant] -= randomQuantity;
                    network.firstStage[plant][edges[e]] += randomQuantity;
                    network.distributionInbound[edges[e]] += randomQuantity;

                    quantity -= randomQuantity;
                    if(quantity == 0)break;
                }
            }
        }
    }
    
    
    /*
    *
    */ 
    public static void productionMutationBalance(int dc, int quantity, TwoStageFlowNetwork network) {
        int[] balanceQuantities;
        int bQuantities = 0;
        int[] edges;
        int e = 0;
        
        for (int k = 0 ; k < network.K ; k++) {
            if(network.customerBalance[k] > 0){
                bQuantities++;
                e++;
            }
        }
        balanceQuantities = new int[bQuantities];
        bQuantities = 0;
        edges = new int[e];
        e = 0;
        for (int k = 0 ; k < network.K ; k++) {
            if(network.customerBalance[k] > 0){
                balanceQuantities[bQuantities] = network.customerBalance[k];
                bQuantities++;
                edges[e] = k;
                e++;
            }
        }
        
        int Q = quantity;
        int randomQuantity;
        int [] randomEdges = new int[edges.length];
        Random rand = new Random();
        while(quantity != 0){

            for (int i = 0; i < edges.length; i++) {
                randomEdges[i] = rand.nextInt(edges.length);
            }
            
            for (int re : randomEdges) {
                randomQuantity = rand.nextInt((Q - 1) + 1) + 1;
                randomQuantity = randomQuantity > quantity ? quantity : randomQuantity;
                randomQuantity = randomQuantity > balanceQuantities[re] ? balanceQuantities[re] : randomQuantity; 

                network.distributionOutbound[dc] += randomQuantity;
                network.secondStage[dc][edges[re]] += randomQuantity;
                network.customerBalance[edges[re]] -= randomQuantity;

                balanceQuantities[re] -= randomQuantity;
                quantity -= randomQuantity;
                if(balanceQuantities[re] == 0){
                	bQuantities--;
                	e--;
                	edges = new int[e];
                    balanceQuantities = new int[bQuantities];
                    bQuantities = 0;
                    e = 0;
                    for (int k = 0 ; k < network.K ; k++) {
                        if(network.customerBalance[k] > 0){
                            balanceQuantities[bQuantities] = network.customerBalance[k];
                            bQuantities++;
                            edges[e] = k;
                            e++;
                        }
                    }
                    
                    randomEdges = new int[edges.length];
                    break;
                }
                if(quantity == 0)break;
            }
        }
    }
    
    /*
    *
    */ 
    public static void leastCostAllocation(int[][] leastCostMatrix, TwoStageFlowNetwork n){
	   
		
        int leastCost = 0;
        int count = 0;
        int s = 0;
        int d = 0;
            
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
    public static void VogelsApproximation(int[][] costMatrix, TwoStageFlowNetwork network){
        
		
        int quantity;
        int[] supply;
        int[] demand;
        int[] supplyPenalties;
        int[] demandPenalties = null;
        
        
        if(costMatrix.length == network.I && costMatrix[0].length == network.J){
                    
            supply = network.quantityProduced.clone();
            demand = network.distributionCapacity.clone();

        }else{
            //System.out.println("Second Stage");
            supply = network.distributionInbound.clone();
            demand = network.customerDemand.clone();
        }
        
        int count = 0;    
        int row;
        int col = 0;
        
        Vector<int[]> penalties;
        while(count != network.totalDemand){
            row = 0;
            col = 0;
            
            penalties = findSupplyPenalties(costMatrix, supply, demand);
        
            supplyPenalties = penalties.get(0);
            demandPenalties = penalties.get(1);
            row = 0;
            int maxPenalty = supplyPenalties[0];
            for (int j = 1; j < supplyPenalties.length; j++) {//Max penalty of supplies
                if(supplyPenalties[j] > maxPenalty){
                    maxPenalty = supplyPenalties[j];
                    row = j;
                }
            }
            
            col = 0;
            maxPenalty = demandPenalties[0];
            for (int j = 1; j < demandPenalties.length; j++) {//Max penalty of supplies
                if(demandPenalties[j] > maxPenalty){
                    maxPenalty = demandPenalties[j];
                    col = j;
                }
            }
            
            boolean choice = false;
            //Tie break 2;
            if(supplyPenalties[row] == demandPenalties[col]){
                if(costMatrix.length != network.I){
                    if(supply[row] > demand[col])
                        choice = true;
                }   
            }else if(supplyPenalties[row] > demandPenalties[col])
                choice = true;
            
            int minimumCost;
            
            //Find minimum cost
            if(choice){
                col = 0;
                minimumCost = Integer.MAX_VALUE; 
                for (int j = 0; j < costMatrix[row].length; j++) {
                    if(costMatrix[row][j] <= minimumCost && demand[j] != 0){
                        minimumCost = costMatrix[row][j];
                        col = j;
                    }
                }
            }else{
                row = 0;
                minimumCost = Integer.MAX_VALUE; 
                for (int j = 0; j < costMatrix.length; j++) {
                    if(costMatrix[j][col] < minimumCost && supply[j] != 0){
                        minimumCost = costMatrix[j][col];
                        row = j;
                    }
                }
                
            }
            
            //Send product
            if(demand[col] > supply[row]){
                quantity = supply[row];
                supply[row] -= quantity;
                demand[col] -= quantity;
            }else{
                quantity = demand[col];
                supply[row] -= quantity;
                demand[col] -= quantity;
            }

            if(costMatrix.length == network.I && costMatrix[0].length == network.J){
            	network.firstStage[row][col] +=  quantity;
            	network.distributionInbound[col] += quantity;
            }else{
            	network.secondStage[row][col] +=  quantity;
            	network.distributionOutbound[row] += quantity;
            	network.customerBalance[col] -= quantity;
            }
                       
            count += quantity;
            
        }
        
    }
    
    /*
    *
    */ 
    public static int[] flowArray(TwoStageFlowNetwork network){
        int[] array = new int[network.I*network.J+network.J*network.K];
        int edge = 0;
        for (int i = 0; i < network.I; i++) {
            for (int j = 0; j < network.J; j++) {
                array[edge] = network.firstStage[i][j];
                edge++;
            }
        }
        for (int i = 0; i < network.J; i++) {
            for (int j = 0; j < network.K; j++) {
                array[edge] = network.secondStage[i][j];
                edge++;
            }
        }
        return array;
    }
    
    /*
    *
    */ 
    public static void importFlowArray(int[] flowArray, TwoStageFlowNetwork network){
        boolean stage = true;

        int edge = 0;
        int i = 0, j = 0, k = 0;
        while(edge != flowArray.length){

            if(stage == true){
                network.firstStage[i][j] = flowArray[edge];
                network.quantityProduced[i] += flowArray[edge];
                network.distributionInbound[j] += flowArray[edge];

                if(edge == network.I*network.J-1){
                    j = 0;
                    stage = false;
                }else if(j == network.J-1){
                    i++;
                    j = 0;
                }else{
                    j++;
                }

            }else{
                network.secondStage[j][k] = flowArray[edge];
                network.distributionOutbound[j] += flowArray[edge];
                network.customerBalance[k] -= flowArray[edge];
                if(k >= network.K-1){
                    j++;
                    k = 0;
                }else
                    k++;
            }

            edge++;
        }
    }
  
}
