/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlpuertaj.optimization.operators;

import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.utils.Distributor;
import unalcol.random.util.RandBool;
import unalcol.search.variation.ParameterizedObject;
import unalcol.search.variation.Variation_1_1;

/**
 *
 * @author Davosoft
 */
public class NetworkProductionMutation implements Variation_1_1<TwoStageFlowNetwork>, ParameterizedObject<Double> {

  /**
   * Probability of mutating one single bit
   */
  protected double vertex_mutation_rate = 0.0;
  
  /**
   * Constructor: Creates a mutation with the given mutation rate
     * @param vertex_mutation_rate
   */
  public NetworkProductionMutation(double vertex_mutation_rate) {
    this.vertex_mutation_rate = vertex_mutation_rate;
  }

  /**
   * Flips a bit in the given genome
     * @param network
   * @return Number of mutated bits
   */
  @Override
  public TwoStageFlowNetwork apply(TwoStageFlowNetwork network) {
    
    try{
        TwoStageFlowNetwork child = new TwoStageFlowNetwork(network);
//        child.importStages(network.transportedProductS1, network.transportedProductS2);
        double rate = 1.0 - ((vertex_mutation_rate == 0.0) ? 1.0/child.I : vertex_mutation_rate);
        RandBool gb = new RandBool(rate);
        
        for (int i = 0 ; i < child.I ; i++) {
            if (gb.next() && (child.quantityProduced[i] > 0 && child.productionBalance[i] == 0)) {
//                System.out.println("Mut: "+i);
                
                Distributor.randomPlantTransportation(i,
                                                      child.quantityProduced[i],
                                                      child);
//                System.out.println("Random transportation");
//                System.out.println(child.toString());
                for (int j = 0 ; j < child.J ; j++) {
                    for (int k = 0 ; k < child.K ; k++) {
                        if(child.distributionInbound[j] != child.distributionOutbound[j]){
                           child.customerBalance[k] += child.secondStage[j][k];
                           child.distributionOutbound[j] -= child.secondStage[j][k];
                           child.secondStage[j][k] = 0; 
                        }
                    }                   
                }
//                System.out.println("Reset second stage");
//                System.out.println(child.toString());
                for (int j = 0 ; j < child.J ; j++) {
                    if(child.distributionInbound[j] != child.distributionOutbound[j]){
	                Distributor.productionMutationBalance(j    ,
                                                              child.distributionInbound[j],
                                                              child);
                    }
                }
//                System.out.println("Balance");
//                System.out.println(child.toString());
                break;
            }
        }
//        if(!child.testNetwork()){
//            System.out.println("D: "+child.testNetwork());
//        }
        
        return child;

    }catch( Exception e ){ 
        System.out.println("Production Mutation Exception...");
        System.out.println(e.toString());
    }
    return null;
  }

  @Override
  public void setParameters(Double parameters) {
	vertex_mutation_rate = parameters;
  }

  @Override
  public Double getParameters() {
	// TODO Auto-generated method stub
	return vertex_mutation_rate;
  }
}
