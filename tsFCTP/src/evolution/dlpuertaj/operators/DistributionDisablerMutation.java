/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution.dlpuertaj.operators;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.utils.Distributor;
import unalcol.random.util.RandBool;
import unalcol.search.variation.ParameterizedObject;
import unalcol.search.variation.Variation_1_1;

/**
 *
 * @author Davosoft
 */
public class DistributionDisablerMutation implements Variation_1_1<TwoStageFlowNetwork>, ParameterizedObject<Double> {

  /**
   * Probability of mutating one single bit
   */
  protected double vertex_mutation_rate = 0.0;

  /**
   * Constructor: Creates a mutation with the given mutation rate
     * @param vertex_mutation_rate
   */
  public DistributionDisablerMutation(double vertex_mutation_rate) {
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

        int activated = 0;
        for (int j = 0 ; j < child.J ; j++) {
            if (child.distributionInbound[j] > 0) {
                activated++;
                if (activated > 1)break;   
            }
        }
        if (activated > 1) {
            double rate = 1.0 - ((vertex_mutation_rate == 0.0)?1.0/child.J:vertex_mutation_rate);
            RandBool gb = new RandBool(rate);
            for (int j = 0 ; j < child.J ; j++) {
                if (gb.next() && child.distributionInbound[j] > 0) {
                    Distributor.closeDistributionCenter(j,child);

                    for (int i = 0 ; i < child.I ; i++) {
                        if(child.productionBalance[i] > 0)
                            Distributor.firstStageDistributionBalance(i, 
                                                                      child.productionBalance[i], 
                                                                      child);

                    }

                    for (int dc = 0 ; dc < child.J ; dc++) {
                        if(child.distributionInbound[dc] - child.distributionOutbound[dc] > 0)
                            Distributor.secondStageDistributionBalance( dc    , 
                                                                        child.distributionInbound[dc] - child.distributionOutbound[dc] , 
                                                                        child);

                    }
                    break;
                }
            }
        }

        return child;
    }catch( Exception e ){ 
        System.out.println("Distribution Mutation Exception...");
        System.err.println("[D Mutation]"+e.getMessage()); 
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
