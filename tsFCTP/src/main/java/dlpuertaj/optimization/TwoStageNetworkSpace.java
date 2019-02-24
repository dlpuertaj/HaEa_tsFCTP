package dlpuertaj.optimization;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Random;


import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.domain.tsFCTP;
import dlpuertaj.optimization.utils.Distributor;
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
                Distributor.leastCostAllocation(problem.getFixedCostS1(),network);
                Distributor.leastCostAllocation(problem.getFixedCostS2(),network);
                break;
            case 1:
                Distributor.leastCostAllocation(problem.getTransportCostS1(),network);
                Distributor.leastCostAllocation(problem.getTransportCostS2(),network);
                break;
            case 2:
                Distributor.VogelsApproximation(problem.getTransportCostS1(),network);
                Distributor.VogelsApproximation(problem.getTransportCostS2(),network);
                break;
            case 3:
                Distributor.VogelsApproximation(problem.getFixedCostS1(),network);
                Distributor.VogelsApproximation(problem.getFixedCostS2(),network);
                break;
            case 4: 
                Distributor.firstStageInitialDistribution(network);
                Distributor.secondStageInitialDistribution(network);
                break; 
        }
        network.productionBalance = new int[network.I];
        return network;
    }
}
