package dlpuertaj.optimization;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import unalcol.optimization.OptimizationFunction;
/**
 *
 * @author Davosoft
 */
public class TwoStageNetworkFlowFitness extends OptimizationFunction<TwoStageFlowNetwork>{

	private int[][] transportCostS1;
	private int[][] fixedCostS1;
	private int[][] transportCostS2;
	private int[][] fixedCostS2;
	public int fCount;
	
	public TwoStageNetworkFlowFitness(int[][] transportCostS1, int[][] fixedCostS1, int[][] transportCostS2,int[][] fixedCostS2) {
		this.transportCostS1 = transportCostS1;
		this.fixedCostS1 = fixedCostS1;
		this.transportCostS2 = transportCostS2;
		this.fixedCostS2 = fixedCostS2;
		this.fCount = 0;
	}

    /**
     * Evaluate the objective function of the problem type #1
     * @param network
     * @return the OptimizationFunction function 
     */
    @Override  
    public Double compute( TwoStageFlowNetwork network ){

        double f = 0;
        for (int i = 0; i < network.I; i++) {
            for (int j = 0; j < network.J; j++) {
                if(network.firstStage[i][j] > 0){
                    f += (network.firstStage[i][j] * transportCostS1[i][j])
                        + fixedCostS1[i][j];
                }
            }
        }
        
        for (int j = 0; j < network.J; j++) {
            for (int k = 0; k < network.K; k++) {
                if(network.secondStage[j][k] > 0){
                    f += (network.secondStage[j][k] * transportCostS2[j][k])
                        + fixedCostS2[j][k];
                }
            }
        }
        fCount++;
        return f;
    }
}
