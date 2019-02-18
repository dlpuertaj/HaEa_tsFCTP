package dlpuertaj.optimization.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class tsFCTP {

	protected String instance;
	public final int I;
	public final int J;
	public final int K;
	public int totalDemand;
	public int totalProductionCapacity;
	
	public int[] productionCapacity;
	public int[] distributionCapacity;// unlimited capacity
	public int[] customerDemand;


	public int[][] firstStageTransportCost;
	public int[][] firstStageFixedCost;
	public int[][] secondStageTransportCost;
	public int[][] secondStageFixedCost;
	
	
	public tsFCTP(String instance) {
		String[] inst = instance.split("");
		
		this.I = Integer.valueOf(inst[0]);
		this.J = Integer.valueOf(inst[1]);
		this.K = Integer.valueOf(inst[2]);
		
		this.totalDemand = 0;
		this.totalProductionCapacity = 0;
		
		this.productionCapacity = new int[I];
		this.distributionCapacity = new int[J];
		this.customerDemand = new int[K];
		
		this.firstStageTransportCost = new int[I][J];
		this.secondStageTransportCost = new int[J][K];
		
		this.firstStageFixedCost = new int[I][J];
		this.secondStageFixedCost = new int[J][K];
		
		build(instance);
	}
	
	public void build(String instance){
		
		
		File file = new File("Instances/"+instance+"/"+instance+".txt");
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    br.readLine();
		    br.readLine();
		    br.readLine();
		    
		    //System.out.println("Reading first stage unit transport costs...");
		    for (int i = 0; i < I; i++) {
		    	for (int j = 0; j < J; j++) {
                    this.firstStageTransportCost[i][j] = Integer.valueOf(br.readLine());
                    //        System.out.println("(" + i + "-" + j +")= " + this.transportCostS1[i][j]);
                }
		    }
		    
		    //System.out.println("Reading first stage fixed costs...");
		    for (int i = 0; i < I; i++) {
		    	for (int j = 0; j < J; j++) {
                    this.firstStageFixedCost[i][j] = Integer.valueOf(br.readLine());
                    //      System.out.println("(" + i + "-" + j +")= " + this.fixedCostS1[i][j]);
                }
		    }
		    
		    //System.out.println("Reading second stage unit transport costs...");
		    for (int j = 0; j < J; j++) {
		    	for (int k = 0; k < K; k++) {
                    this.secondStageTransportCost[j][k] = Integer.valueOf(br.readLine());
                    //      System.out.println("(" + j + "-" + k +")= " + this.transportCostS2[j][k]);
                }
		    }

		    //System.out.println("Reading second stage fixed costs...");
		    for (int j = 0; j < J; j++) {
		    	for (int k = 0; k < K; k++) {
                    this.secondStageFixedCost[j][k] = Integer.valueOf(br.readLine());
                    //      System.out.println("(" + j + "-" + k +")= " + this.fixedCostS2[j][k]);
                }
		    }
		    		    
		    //System.out.println("Reading production capacity...");
            for (int i = 0; i < I; i++) {
		    	this.productionCapacity[i] = Integer.valueOf(br.readLine());
		    	this.totalProductionCapacity += this.productionCapacity[i];
            }
            //System.out.println("Production capacity= " + Arrays.toString(productionCapacity) + " total production capacity= "+totalProductionCapacity);

            // System.out.println("Reading demand of customers and calculating total demand...");
		    for (int k = 0; k < K; k++) {
		    	this.customerDemand[k] = Integer.valueOf(br.readLine());
		    	this.totalDemand += this.customerDemand[k];
            }
		    //System.out.println("Customer demands= " + Arrays.toString(customerDemand) + " total demand= "+totalDemand);
		    
		    //System.out.println("Reading Distribution capacity...");
            for (int j = 0; j < J; j++) {
                this.distributionCapacity[j] = totalProductionCapacity;
            }
		    br.close();
		    //System.out.println("Distribution capacity= " + Arrays.toString(distributionCapacity));
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public int[][] getTransportCostS1() {
        return firstStageTransportCost;
    }

    public void setTransportCostS1(int[][] transportCostS1) {
        this.firstStageTransportCost = transportCostS1;
    }

    public int[][] getFixedCostS1() {
        return firstStageFixedCost;
    }

    public void setFixedCostS1(int[][] fixedCostS1) {
        this.firstStageFixedCost = fixedCostS1;
    }

    public int[][] getTransportCostS2() {
        return secondStageTransportCost;
    }

    public void setTransportCostS2(int[][] transportCostS2) {
        this.secondStageTransportCost = transportCostS2;
    }

    public int[][] getFixedCostS2() {
        return secondStageFixedCost;
    }

    public void setFixedCostS2(int[][] fixedCostS2) {
        this.secondStageFixedCost = fixedCostS2;
    }

	public void showProblemInstance(){
		
		System.out.println("I = "+I);
		System.out.println("J = "+J);
		System.out.println("K = "+K);
		System.out.println("Total demand = "+totalDemand);
		System.out.println("Total production capacity = "+totalProductionCapacity);
		System.out.println("---");
		System.out.println("First stage fixed costs");
		for (int[] fixedCost : firstStageFixedCost) {
			System.out.println(Arrays.toString(fixedCost));
		}
		System.out.println("Second stage fixed costs");
		for (int[] fixedCost : secondStageFixedCost) {
			System.out.println(Arrays.toString(fixedCost));
		}
		System.out.println("---");
		System.out.println("First stage transportation costs");
		for (int[] transportCost : firstStageTransportCost) {
			System.out.println(Arrays.toString(transportCost));
		}
		System.out.println("Second stage transportation costs");
		for (int[] transportCost : secondStageTransportCost) {
			System.out.println(Arrays.toString(transportCost));
		}
		System.out.println("---");
		System.out.println("Customer demand");
		System.out.println(Arrays.toString(customerDemand));
		System.out.println("---");
		System.out.println("Production capacity");
		System.out.println(Arrays.toString(productionCapacity));
	}
}
