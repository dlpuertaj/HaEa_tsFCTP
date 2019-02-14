package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.tsFCTP;
import evolution.dlpuertaj.utils.Distributor;

class TestDistributor {

	private static final String[] INSTANCES ={"223","224","225","226","227",
			            					  "233","234","236","238","248",
			        					 	  "256","324","325","334","335",
			        					 	  "336","337_01","337_02","346","435",};
	private tsFCTP[] problems;
	private TwoStageFlowNetwork[] networks;
	
	@BeforeEach
	public void init() {
		System.out.println("\nBuilding instances and networks...");
		
		problems = new tsFCTP[INSTANCES.length];
		networks = new TwoStageFlowNetwork[INSTANCES.length];
		int i = 0;
		for(String instance : INSTANCES) {
			problems[i] = new tsFCTP(instance);
			networks[i] = new TwoStageFlowNetwork(problems[i]);
			i++;
		}
	}
	
	@Test
	public void startProductionTest() {
		System.out.println("Testing startProduction method...");
		int initialProduction;
		for(int i = 0 ; i < INSTANCES.length ; i++) {
			System.out.print("Testig with instance: " + INSTANCES[i]);
			
			assertNotNull(this.problems[i]);
			assertNotNull(this.networks[i]);
			initialProduction = 0;
			
			for(int c : this.networks[i].quantityProduced) {
				initialProduction += c;
			}
			
			assertEquals(0, initialProduction);
			
			Distributor.startProduction(this.networks[i]);
			
			initialProduction = 0;
			for(int pc = 0 ; pc < networks[i].I ; pc++) {
				initialProduction += networks[i].quantityProduced[pc];
				assertTrue(networks[i].quantityProduced[pc] <= networks[i].productionCapacity[pc]);
			}
			
			assertTrue(initialProduction <= networks[i].totalProductionCapacity);
			assertTrue(initialProduction >= networks[i].totalDemand);
			
			for(int j = 0 ; j < networks[i].I ; j++ ) {
				assertTrue(networks[i].quantityProduced[j] <= problems[i].productionCapacity[j]);
			}
			System.out.println("...ok");
		}
	}
	
	@Test
	public void testRandomAllocationWithCapacities() {
		System.out.println("Testing randomAllocationWithCapacities method...");
		
		for(int i = 0 ; i < INSTANCES.length ; i++) {
			System.out.print("Testig with instance: " + INSTANCES[i]);
			
			int[] capacities =	problems[i].productionCapacity;
			int[] availableNodes = new int[capacities.length];
			int quantity = networks[i].totalProductionCapacity;
			
			for(int j = 0 ; j < availableNodes.length ; j++){
				availableNodes[j] = j;
			}
			
			int[] allocated = Distributor.randomAllocationWithCapacities(capacities, availableNodes, quantity);
			
			for(int k = 0 ; k < availableNodes.length ; k++){
				assertTrue(allocated[k] >= 0);
				assertTrue(allocated[k] <= capacities[k]);
			}
			System.out.println("...ok");
		}
	}
	
	@Test
	public void testFirstStageInitialDistribution() {
		System.out.println("Testing firstStageInitialDistributions method...");
		
		int totalDistributionInbound;
		
		for(int i = 0 ; i < INSTANCES.length ; i++) {
			System.out.print("Testig with instance: " + INSTANCES[i]);
			
			totalDistributionInbound = 0;
			
			Distributor.startProduction(networks[i]);
			
			Distributor.firstStageInitialDistribution(networks[i]);
			
			for(int pc = 0 ; pc < networks[i].I ; pc++) {
				assertEquals(0, networks[i].productionBalance[pc]);
			}
			
			for(int inbound : networks[i].distributionInbound) {
				assertTrue(inbound <= networks[i].totalDemand);
				totalDistributionInbound += inbound;
			}
			
			assertTrue(networks[i].totalProductionCapacity >= totalDistributionInbound);
			System.out.println("...ok");
		}
	}
	
	@Test
	public void testSecondStageInitialDistribution() {
		System.out.println("Testing secondStageInitialDistributions method...");
		
		int totalDistributionOutbound;
		
		for(int i = 0 ; i < INSTANCES.length ; i++) {
			totalDistributionOutbound = 0;
			System.out.print("Testig with instance: " + INSTANCES[i]);
			
			Distributor.startProduction(this.networks[i]);
			
			Distributor.firstStageInitialDistribution(networks[i]);
			
			Distributor.secondStageInitialDistribution(networks[i]);
				
			int j = 0;
			for(int out : networks[i].distributionOutbound) {
				assertTrue(networks[i].distributionInbound[j] - networks[i].distributionInbound[j] == 0);
				totalDistributionOutbound += out;
				j++;
			}
			
			assertTrue(totalDistributionOutbound == networks[i].totalDemand);
			
			for(int balance : networks[i].customerBalance) {
				assertTrue(balance == 0);
			}
			System.out.println("...ok");
		}
	}
}
