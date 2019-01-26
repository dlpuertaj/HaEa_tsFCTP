package evolution.dlpuertaj;

import java.util.Arrays;

public class TwoStageFlowNetwork {
	
    public final int I;
    public final int J;
    public final int K;
    public int totalDemand;
    public int totalProductionCapacity;

    public int[] productionCapacity;
    public int[] productionBalance;
    public int[] quantityProduced;
    public int[] distributionInbound;
    public int[] distributionOutbound;
    public int[] distributionCapacity;// unlimited capacity
    public int[] customerDemand;
    public int[] customerBalance;

    public int[][] transportedProductS1;
    public int[][] transportedProductS2;

	
	
    public TwoStageFlowNetwork(tsFCTP instance){
        
		this.I = instance.I;
		this.J = instance.J;
		this.K = instance.K;
		
		this.customerDemand       = instance.customerDemand.clone();
		this.customerBalance      = customerDemand.clone();//new int[K];
		this.productionCapacity   = instance.productionCapacity.clone();
		this.productionBalance    = new int[I];
		this.quantityProduced     = new int[I];
		this.distributionInbound  = new int[J];
		this.distributionOutbound = new int[J];
		this.distributionCapacity = new int[J];

		this.transportedProductS1 = new int[I][J];
		this.transportedProductS2 = new int[J][K];
	
		this.totalProductionCapacity = instance.totalProductionCapacity;
		this.distributionCapacity = instance.distributionCapacity.clone();
		this.totalDemand += instance.totalDemand;
	}
    
    public TwoStageFlowNetwork(TwoStageFlowNetwork network){
            
        this.I = network.I;
        this.J = network.J;
        this.K = network.K;
        this.totalDemand = network.totalDemand;
        this.totalProductionCapacity = network.totalProductionCapacity;
        
        this.customerDemand       = network.customerDemand.clone();
        this.customerBalance      = network.customerBalance.clone();
        this.productionCapacity   = network.productionCapacity.clone();
        this.productionBalance    = network.productionBalance.clone();
        this.quantityProduced     = network.quantityProduced.clone();
        this.distributionInbound  = network.distributionInbound.clone();
        this.distributionOutbound = network.distributionOutbound.clone();
        this.distributionCapacity = network.distributionCapacity.clone();
        
        transportedProductS1 = new int[I][J];
        transportedProductS2 = new int[J][K];
        
        for (int i = 0; i < transportedProductS1.length; i++) {
            transportedProductS1[i] = network.transportedProductS1[i].clone();
        }
        for (int j = 0; j < transportedProductS2.length; j++) {
            transportedProductS2[j] = network.transportedProductS2[j].clone();
        }
    }
		
    public boolean testNetwork(){

        int firstStageFlow          = 0;
        int secondStageFlow         = 0;
        int[] distributionIn        = new int[J];
        int[] distributionOut       = new int[J];
        boolean pBalance            = false;
        boolean cBalance            = false;    
        boolean distributionBalance = false;
        boolean positiveFlow        = true;
        boolean flowConservation    = false;

        for (int i = 0; i < I; i++) {
            int produced = 0;
            for (int j = 0; j < J; j++) {
                    firstStageFlow += transportedProductS1[i][j];
                    produced += transportedProductS1[i][j];
                    if(transportedProductS1[i][j] < 0)
                        positiveFlow = false;
                    distributionIn[j] += transportedProductS1[i][j];
            }
            pBalance = produced == quantityProduced[i];
        }
        int[] sent = new int[K];
        for (int i = 0; i < J; i++) {

            for (int j = 0; j < K; j++) {
                secondStageFlow += transportedProductS2[i][j];
                sent[j] += transportedProductS2[i][j];
                distributionOut[i] += transportedProductS2[i][j];
                if(transportedProductS2[i][j] < 0)
                    positiveFlow = false;
            }
        }

        for (int i = 0; i < this.K; i++) {
            cBalance = sent[i] == customerDemand[i];
        }
        for (int i = 0; i < this.J; i++) {
            distributionBalance = distributionInbound[i] == distributionOutbound[i];
        }
        boolean distributionInBalance = false;
        boolean distributionOutBalance = false;

        for (int i = 0; i < this.J; i++) {
            distributionInBalance = distributionInbound[i] == distributionIn[i];
        }

        for (int i = 0; i < this.J; i++) {
            distributionOutBalance = distributionOutbound[i] == distributionOut[i];
        }

        if(firstStageFlow == secondStageFlow && firstStageFlow == totalDemand && secondStageFlow == totalDemand ){
            flowConservation = true;
        }

        return distributionOutBalance &&
               distributionInBalance  && 
               distributionBalance    && 
               flowConservation       && 
               positiveFlow           &&
               pBalance               && 
               cBalance;
    }
    
    @Override
    public String toString(){
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("----Network----");sb.append(NEWLINE);
        for (int i = 0; i < I; i++) {
            sb.append(Arrays.toString(transportedProductS1[i]));
            sb.append(NEWLINE);
        }
        sb.append("---");
        sb.append(NEWLINE);
        for (int j = 0; j < J; j++) {
            sb.append(Arrays.toString(transportedProductS2[j]));
            sb.append(NEWLINE);
        }
        sb.append("Production balance").append(Arrays.toString(productionBalance));
        sb.append(NEWLINE);
        sb.append("Production quantity").append(Arrays.toString(quantityProduced));
        sb.append(NEWLINE);
        sb.append("Distribution inbound").append(Arrays.toString(distributionInbound));
        sb.append(NEWLINE);
        sb.append("Distribution outbound").append(Arrays.toString(distributionOutbound));
        sb.append(NEWLINE);
        sb.append("Customer demand").append(Arrays.toString(customerDemand));
        sb.append(NEWLINE);
        sb.append("Customer balance").append(Arrays.toString(customerBalance));
        sb.append(NEWLINE);
        sb.append("Test: ").append(this.testNetwork());
        sb.append(NEWLINE);
        sb.append("------------");sb.append(NEWLINE);
        return sb.toString();
    }
    
}
