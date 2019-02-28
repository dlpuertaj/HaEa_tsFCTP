package dlpuertaj.optimization.domain;

import java.util.Arrays;

public class TwoStageFlowNetwork {
	
    public final int I;
    public final int J;
    public final int K;
    public int totalDemand;
    public int totalProductionCapacity;

    public int[] productionCapacity;
    public int[] productionBalance;
    public int[] quantityProduced; //TODO: not necessary
    public int[] distributionInbound;//TODO: use only one array for the distribution balance
    public int[] distributionOutbound;
    public int[] distributionCapacity;// unlimited capacity = totalDemand
    public int[] customerDemand;
    public int[] customerBalance;

    public int[][] firstStage;
    public int[][] secondStage;


    public TwoStageFlowNetwork(tsFCTP instance){
        
		this.I = instance.I;
		this.J = instance.J;
		this.K = instance.K;
		
		this.customerDemand       = instance.customerDemand.clone();
		this.customerBalance      = customerDemand.clone();
		this.productionCapacity   = instance.productionCapacity.clone();
		this.productionBalance    = new int[I];
		this.quantityProduced     = new int[I];
		this.distributionInbound  = new int[J];
		this.distributionOutbound = new int[J];
		this.distributionCapacity = new int[J];

		this.firstStage = new int[I][J];
		this.secondStage = new int[J][K];

		this.totalProductionCapacity = instance.totalProductionCapacity;
		this.distributionCapacity = instance.distributionCapacity.clone();
		this.totalDemand = instance.totalDemand;
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

        firstStage = new int[I][J];
        secondStage = new int[J][K];

        for (int i = 0; i < firstStage.length; i++) {
            firstStage[i] = network.firstStage[i].clone();
        }
        for (int j = 0; j < secondStage.length; j++) {
            secondStage[j] = network.secondStage[j].clone();
        }
    }


    /**
     * Method that test the production of the network. It test the production balance and
     * the production capacity
     **/
    public boolean productionBalance(){
        int produced;
        int totalProduction = 0;
        for (int i = 0; i < I; i++) {
            produced = 0;
            for(int j = 0 ; j < J ; j++){
                produced += firstStage[i][j];
            }
            if(produced > productionCapacity[i])
                return false;
            else
                totalProduction += produced;
        }
        return (totalProduction == totalDemand) && (totalProduction <= totalProductionCapacity);
    }

    /**
     * Method that test the distribution centers. It tests the distribution inbound and outbound
     * */
    public boolean distributionBalance(){

        int inbound;
        int outbound;
        int totalDistribution = 0;
        for(int j = 0 ; j < J ; j++){
            inbound = 0;
            for(int i = 0 ; i < I ; i++){
                inbound += firstStage[i][j];
            }
            outbound = 0;
            for(int k = 0 ; k < K ; k++){
                outbound += secondStage[j][k];
            }
            if(inbound != outbound)
                return false;
            else
                totalDistribution += outbound;
        }
        return totalDistribution == totalDemand;
    }

    /**
     * Method that test the balance of customers*/
    public boolean customerBalance(){
        int received;
        int totalReceived = 0;
        for(int k = 0 ; k < K ; k++){
            received = 0;
            for(int j = 0 ; j < J ; j++){
                received += secondStage[j][k];
            }
            if(received != customerDemand[k] && customerBalance[k] != 0)
                return false;
            else
                totalReceived += received;
        }
        return totalReceived == totalDemand;
    }

    public boolean testNetwork(){
        return productionBalance() && distributionBalance() && customerBalance();
    }

    public void allocateInFirstStage(int source, int target,int quantity){
        firstStage[source][target] += quantity;
        productionBalance[source] -= quantity;
        distributionInbound[target] += quantity;
    }
    public void allocateInSecondStage(int source, int target, int quantity){
        secondStage[source][target] += quantity;
        distributionOutbound[source] += quantity;
        customerBalance[target] -= quantity;
    }

    public int[] getQuantityProduced(){
        int[] produced = new int[I];
        for (int i = 0 ; i < I ; i++) {
            for (int j = 0; j < J; j++) {
                produced[i] += firstStage[i][j];
            }
        }
        return produced;
    }

    public void closeProductionCenter(int productionCenter){
        for (int j = 0 ; j < J ; j++) {
            distributionInbound[j] -= firstStage[productionCenter][j];
            productionBalance[productionCenter] += firstStage[productionCenter][j];
            firstStage[productionCenter][j] = 0;
        }
    }

    @Override
    public String toString(){
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("----Network----");sb.append(NEWLINE);
        for (int i = 0; i < I; i++) {
            sb.append(Arrays.toString(firstStage[i]));
            sb.append(NEWLINE);
        }
        sb.append("---");
        sb.append(NEWLINE);
        for (int j = 0; j < J; j++) {
            sb.append(Arrays.toString(secondStage[j]));
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
        sb.append("------------");sb.append(NEWLINE);
        return sb.toString();
    }
}