import static org.junit.jupiter.api.Assertions.*;

import dlpuertaj.optimization.TwoStageNetworkSpace;
import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.domain.tsFCTP;
import dlpuertaj.optimization.utils.Distributor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class TestDistributor {

	private static final String[] INSTANCES ={"223","224","225","226","227",
			            					  "233","234","236","238","248",
			        					 	  "256","324","325","334","335",
			        					 	  "336","337_01","337_02","346","435",};
	private tsFCTP[] problems;
	private TwoStageFlowNetwork[] networks;
	private TwoStageNetworkSpace[] space;
	
	@BeforeEach
	void init() {
		System.out.println("\nBuilding instances and networks...");
		
		problems = new tsFCTP[INSTANCES.length];
		networks = new TwoStageFlowNetwork[INSTANCES.length];
		space = new TwoStageNetworkSpace[INSTANCES.length];
		int i = 0;
		for(String instance : INSTANCES) {
			problems[i] = new tsFCTP(instance);
			space[i] = new TwoStageNetworkSpace(problems[i]);
			networks[i] = new TwoStageFlowNetwork(problems[i]);
			i++;
		}
	}

    @Test
    void testRandomAllocationWithCapacities() {
        System.out.println("Testing randomAllocationWithCapacities method...");

        for(int i = 0 ; i < INSTANCES.length ; i++) {
            System.out.print("Testig with instance: " + INSTANCES[i]);

			System.out.print(" test 1");
            int[] capacities =	problems[i].productionCapacity.clone();
            int quantity = networks[i].totalProductionCapacity;
            int totalAllocated = 0;
            int[] allocated = Distributor.randomAllocationWithCapacities(capacities, quantity);

            for(int k = 0 ; k < capacities.length ; k++){
                assertTrue(allocated[k] >= 0);
                assertTrue(allocated[k] <= problems[i].productionCapacity[k]);
                totalAllocated += allocated[k];
            }
            assertEquals(totalAllocated,quantity);

			System.out.print(" test 2");
            capacities =	problems[i].productionCapacity.clone();
            quantity = networks[i].totalProductionCapacity +250;
            totalAllocated = 0;

            allocated = Distributor.randomAllocationWithCapacities(capacities, quantity);

            for(int k = 0 ; k < capacities.length ; k++){
                assertTrue(allocated[k] >= 0);
                assertTrue(allocated[k] <= problems[i].productionCapacity[k]);
                totalAllocated += allocated[k];
            }
            assertEquals(totalAllocated,quantity - 250);

			System.out.print(" test 3");
            capacities =	problems[i].productionCapacity.clone();
            quantity = networks[i].totalProductionCapacity -250;
            totalAllocated = 0;

            allocated = Distributor.randomAllocationWithCapacities(capacities, quantity);

            for(int k = 0 ; k < capacities.length ; k++){
                assertTrue(allocated[k] >= 0);
                assertTrue(allocated[k] <= problems[i].productionCapacity[k]);
                totalAllocated += allocated[k];
            }
            assertEquals(totalAllocated,quantity);

            System.out.println("...ok");
        }
    }
	
	@Test
	void startProductionTest() {
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
				assertTrue(networks[i].productionBalance[j] <= problems[i].productionCapacity[j]);
			}
			System.out.println("...ok");
		}
	}
	
	@Test
	void testFirstStageInitialDistribution() {
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
	void testSecondStageInitialDistribution() {
		System.out.println("Testing secondStageInitialDistributions method...");
		
		int totalDistributionOutbound;
		
		for(int i = 0 ; i < INSTANCES.length ; i++) {
			totalDistributionOutbound = 0;

			System.out.print("Testing with instance: " + INSTANCES[i]);
			
			Distributor.startProduction(networks[i]);

			Distributor.firstStageInitialDistribution(networks[i]);

			Distributor.secondStageInitialDistribution(networks[i]);
				
			int j = 0;
			for(int out : networks[i].distributionOutbound) {
                assertEquals(0, networks[i].distributionInbound[j] - networks[i].distributionOutbound[j]);
				totalDistributionOutbound += out;
				j++;
			}

            assertEquals(totalDistributionOutbound, networks[i].totalDemand);
			
			for(int balance : networks[i].customerBalance) {
                assertEquals(0, balance);
			}
			System.out.println("...ok");
		}
	}

	@Test
	void testReturnProduct(){
		System.out.println("Testing returnProduct method...");

		System.out.print("Testig with instance: " + INSTANCES[0]);

		Distributor.startProduction(networks[0]);

		Distributor.firstStageInitialDistribution(networks[0]);

		int originalInbound = networks[0].distributionInbound[0];
		int[][] originalDistribution = new int[networks[0].I][];
		for(int i = 0 ; i < networks[0].I ; i++){
			originalDistribution[i] = networks[0].firstStage[i].clone();
		}

		networks[0].distributionInbound[0] += 50;

		Distributor.returnProduct(0,50,networks[0]);
		assertEquals(originalInbound, networks[0].distributionInbound[0]);

		int balance = 0;
		int sent = 0;
		for(int i = 0 ; i < networks[0].I ; i++){
			balance += networks[0].productionBalance[i];
			sent += (originalDistribution[i][0] - networks[0].firstStage[i][0]);
		}
        assertEquals(50, balance);
        assertEquals(50, sent);
	}

	@Test
	void testImportSecondStagePlan(){
		System.out.println("Testing importSecondStagePlans method...");

		for(int i = 0 ; i < INSTANCES.length ; i++) {
			System.out.print("Testig with instance: " + INSTANCES[i]);
			TwoStageFlowNetwork network = space[i].pick();

			Distributor.importSecondStagePlan(network.secondStage.clone(),networks[i]);

			assertTrue(networks[i].customerBalance());
			int j = 0;
			for(int[] array : network.secondStage){
                assertArrayEquals(array, networks[i].secondStage[j]);
				assertArrayEquals(array,networks[i].secondStage[j]);
				j++;
			}

            assertArrayEquals(network.distributionInbound, networks[i].distributionOutbound);
			System.out.println(" ok...");
		}
	}

	@Test
    void testFirstStageXOverBalance(){
		System.out.println("Testing firstStageXOverBalance method...");

		for(int i = 0 ; i < INSTANCES.length ; i++) {
			System.out.print("Testig with instance: " + INSTANCES[i]);

			Distributor.startProduction(networks[i]);
            Distributor.firstStageInitialDistribution(networks[i]);
            Distributor.secondStageInitialDistribution(networks[i]);

            assertTrue(networks[i].testNetwork());

			TwoStageFlowNetwork network = space[i].pick();

			while(comparePlan(network.secondStage,networks[i].secondStage)){
                network = space[i].pick();
            }

			assertTrue(network.testNetwork());
			Distributor.importSecondStagePlan(network.secondStage,networks[i]);

            for (int dc = 0 ; dc < networks[i].J ; dc++) {
                if(networks[i].distributionInbound[dc] - networks[i].distributionOutbound[dc] > 0){
                    Distributor.returnProduct(dc,(networks[i].distributionInbound[dc] - networks[i].distributionOutbound[dc]),networks[i]);
                    assertEquals(networks[i].distributionInbound[dc],networks[i].distributionOutbound[dc]);
                }
            }
            assertTrue(!networks[i].testNetwork());
            assertTrue(!Arrays.equals(networks[i].productionBalance,new int[network.I]));

            for (int pc = 0 ; pc < networks[i].I ; pc++) {
                if(networks[i].productionBalance[pc] > 0) {
                    Distributor.firstStageXOverBalance(pc, networks[i]);
                    assertEquals(0,networks[i].productionBalance[pc] );
                }
            }
            for (int dc = 0 ; dc < networks[i].J ; dc++) {
                    assertEquals(networks[i].distributionInbound[dc],networks[i].distributionOutbound[dc]);
            }

            assertTrue(networks[i].testNetwork());
            System.out.println(" ok...");
		}
	}

    @Test
    void testFirstStageDistributionBalance(){
        //TODO: implement test
    }

    boolean comparePlan(int[][] p1, int[][] p2){
	    for(int i = 0 ; i < p1.length ; i++){
	        if(!Arrays.equals(p1[i],p2[i]))
                return false;
        }
	    return true;
    }


}
