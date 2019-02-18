/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution.dlpuertaj.operators;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.domain.Distributor;
import unalcol.search.variation.Variation_2_2;


/**
 *
 * @author Davosoft
 */
public class XOverNetworkCustomers implements Variation_2_2<TwoStageFlowNetwork> {

  public XOverNetworkCustomers(){
 	
  }

  /**
   * Apply the simple point crossover operation over the given genomes at the given
   * cross point
     * @param parent1
     * @param parent2
   * @return The crossover point
   */
    @Override
  public TwoStageFlowNetwork[] apply(TwoStageFlowNetwork parent1, TwoStageFlowNetwork parent2) {
    	try{
            //Swap second stage distribution plan
            TwoStageFlowNetwork child1 = new TwoStageFlowNetwork(parent1);
            TwoStageFlowNetwork child2 = new TwoStageFlowNetwork(parent2);

            int[][] tempSecondStage = new int[parent1.J][parent1.K];
            for (int i = 0; i < tempSecondStage.length; i++) {
                tempSecondStage[i] = child1.secondStage[i].clone();
            }
            Distributor.importSecondStagePlan(child2.secondStage, child1);
            Distributor.importSecondStagePlan(tempSecondStage, child2);

            for (int dc = 0 ; dc < child1.J ; dc++) {              
                if(child1.distributionInbound[dc] - child1.distributionOutbound[dc] > 0){

                    Distributor.returnProduct(dc,
                                              child1.distributionInbound[dc] - child1.distributionOutbound[dc]  ,
                                              child1);
                }
            }

            for (int i = 0 ; i < child1.I ; i++) {              
                if(child1.productionBalance[i] > 0)
                    Distributor.firstStageXOverBalance(i,
                                                       child1.productionBalance[i],
                                                       child1); 
            }


            for (int dc = 0 ; dc < child2.J ; dc++) {              
                if(child2.distributionInbound[dc] - child2.distributionOutbound[dc] > 0){

                    Distributor.returnProduct(dc ,
                                              child2.distributionInbound[dc] - child2.distributionOutbound[dc]  ,
                                              child2);
                  }
            }

            for (int i =0 ; i < child2.I ; i++) {              
                if(child2.productionBalance[i] > 0)

                    Distributor.firstStageXOverBalance(i,
                                                        child2.productionBalance[i],
                                                        child2);     
            }

          return new TwoStageFlowNetwork[]{child1,child2};
      
      }catch( Exception e ){
          System.out.println("XOver Exception...");
          System.out.println(e.toString());
      }
      return null;
  }

}
