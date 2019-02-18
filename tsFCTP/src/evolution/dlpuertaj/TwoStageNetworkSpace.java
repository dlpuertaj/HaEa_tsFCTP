/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution.dlpuertaj;

import java.util.Random;

import evolution.dlpuertaj.domain.Distributor;
import unalcol.search.space.Space;

public class TwoStageNetworkSpace implements Space<TwoStageFlowNetwork>{
	
	public tsFCTP problem;
	
	public TwoStageNetworkSpace(tsFCTP problem) {		
		this.problem = problem;
	}

	@Override
	public double feasibility(TwoStageFlowNetwork arg0) {
		// TODO Auto-generated method stub
		return 1.0;
	}
	
	@Override
	public boolean feasible(TwoStageFlowNetwork arg0) {

		return true;
	}

	@Override
	public TwoStageFlowNetwork repair(TwoStageFlowNetwork arg0) {

		return arg0;
	}

	@Override
	public TwoStageFlowNetwork pick() {
        TwoStageFlowNetwork network = new TwoStageFlowNetwork(problem);
        Distributor.startProduction(network);
        Random rand = new Random();
        int choice = rand.nextInt(5);

        switch(choice){
            case 0:
                //System.out.println(choice); 	
                Distributor.leastCostAllocation(problem.getFixedCostS1(),network);
                Distributor.leastCostAllocation(problem.getFixedCostS2(),network);
                break;
            case 1:
                //System.out.println(choice);
                Distributor.leastCostAllocation(problem.getTransportCostS1(),network);
                Distributor.leastCostAllocation(problem.getTransportCostS2(),network);
                break;
            case 2:
                //System.out.println(choice);
                    Distributor.VogelsApproximation(problem.getTransportCostS1(),network);
                    Distributor.VogelsApproximation(problem.getTransportCostS2(),network);
                break;
            case 3:
                //System.out.println(choice);
                    Distributor.VogelsApproximation(problem.getFixedCostS1(),network);
                    Distributor.VogelsApproximation(problem.getFixedCostS2(),network);
                break;
            case 4: 
                //System.out.println(choice);
                Distributor.firstStageInitialDistribution(network);
                Distributor.secondStageInitialDistribution(network);
                break; 
        }
        //System.out.println("S:"+network.testNetwork());
            return network;
    }

   /* 
    public static void main(String args[]){
        
        String instance = "223";
        tsFCTP problem = new tsFCTP(instance);
//        problem.showProblemInstance();
        TwoStageFlowNetwork network1 = new TwoStageFlowNetwork(problem);
        TwoStageFlowNetwork network2 = new TwoStageFlowNetwork(problem);
        TwoStageFlowNetwork network3 = new TwoStageFlowNetwork(problem);

        Distributor.startProduction(network1);
        Distributor.leastCostAllocation(problem.getFixedCostS1(),network1);
        Distributor.leastCostAllocation(problem.getFixedCostS2(),network1);

        System.out.println("Network with LCA");
//        System.out.println(network1.toString());
        System.out.println("Test: "+network1.testNetwork());

        Distributor.startProduction(network2);
        Distributor.VogelsApproximation(problem.getFixedCostS1(),network2);
        Distributor.VogelsApproximation(problem.getFixedCostS2(),network2);

        System.out.println("Network with Vogel");
//        System.out.println(network2.toString());
        System.out.println("Test: "+network2.testNetwork());

        Distributor.startProduction(network3);
        Distributor.firstStageInitialDistribution(network3);
        Distributor.secondStageInitialDistribution(network3);

        System.out.println("Network with Random");
//        System.out.println(network3.toString());
        System.out.println("Test: "+network3.testNetwork());

//        System.out.println("Array: "+Arrays.toString(network3.flowArray()));
    }
    */
}
