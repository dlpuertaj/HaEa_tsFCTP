/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlpuertaj.optimization.operators;

import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.utils.Distributor;
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

            balanceFirstStage(child1);
            balanceFirstStage(child2);

          return new TwoStageFlowNetwork[]{child1,child2};

        }catch( Exception e ){
          System.out.println("XOver Exception...");
          System.out.println(e.toString());
        }
        return null;
    }

    /**
     * Method that applies the returnProduct method and the firstStageXOverBalance method
     * to balance the network after a second stage swap
     * @param network : TwoStageFlowNetwork
     * */
    private void balanceFirstStage(TwoStageFlowNetwork network) {
        for (int dc = 0 ; dc < network.J ; dc++) {
            if(network.distributionInbound[dc] - network.distributionOutbound[dc] > 0){
                Distributor.returnProduct(dc,
                        network.distributionInbound[dc] - network.distributionOutbound[dc],network);
            }
        }
        for (int i = 0 ; i < network.I ; i++) {
            if(network.productionBalance[i] > 0)
                Distributor.firstStageXOverBalance(i,network);
        }
    }
}
