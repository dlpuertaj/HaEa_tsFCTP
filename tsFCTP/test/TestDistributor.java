import static org.junit.jupiter.api.Assertions.*;

import dlpuertaj.optimization.domain.TwoStageNetworkSpace;
import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.domain.tsFCTP;
import dlpuertaj.optimization.utils.Distributor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import unalcol.random.util.RandBool;

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
        //Its possible that de dc 0 has no inbound from any production centers
        //So there is no positive edges and we cant return product
        int dcWithInbound = 0;
		for(int j = 0 ; j < networks[0].J ; j++){
            if(networks[0].distributionInbound[j] > 0){
                dcWithInbound = j;
                break;
            }
        }
		networks[0].distributionInbound[dcWithInbound] += 50;

		Distributor.returnProduct(0,50,networks[0]);
		assertEquals(originalInbound, networks[0].distributionInbound[dcWithInbound]);

		int balance = 0;
		int sent = 0;
		for(int i = 0 ; i < networks[0].I ; i++){
			balance += networks[0].productionBalance[i];
			sent += (originalDistribution[i][dcWithInbound] - networks[0].firstStage[i][dcWithInbound]);
		}
        assertEquals(50, balance);
        assertEquals(50, sent);

        System.out.println(" ok...");
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
            assertEquals(false,networks[i].testNetwork());
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
        System.out.println("Testing firstStageDistributionBalance method...");

        for(int i = 0 ; i < INSTANCES.length ; i++) {
            System.out.print("Testig with instance: " + INSTANCES[i]);

            Distributor.startProduction(networks[i]);
            Distributor.firstStageInitialDistribution(networks[i]);
            Distributor.secondStageInitialDistribution(networks[i]);

            assertTrue(networks[i].testNetwork());
            int closed = 0;
            int returned = 0;
            for (int j = 0 ; j < networks[i].J ; j++) {
                if (networks[i].distributionInbound[j] > 0) {
                    closed = j;
                    returned = networks[i].distributionInbound[j];
                    Distributor.closeDistributionCenter(j,networks[i]);
                    break;
                }
            }

            int customerBalance = 0;
            for (int balance : networks[i].customerBalance) {
                customerBalance += balance;
            }

            assertEquals(customerBalance,returned);
            assertEquals(0,networks[i].distributionInbound[closed]);
            assertEquals(0,networks[i].distributionOutbound[closed]);
            assertTrue(!networks[i].testNetwork());

            int productionBalance = 0;
            int newInbound = 0;
            for (int j = 0 ; j < networks[i].I ; j++) {
                if(networks[i].productionBalance[j] > 0) {
                    productionBalance += networks[i].productionBalance[j];
                    Distributor.firstStageDistributionBalance(j, closed, networks[i]);
                }
                assertEquals(0,networks[i].productionBalance[j]);
            }

            for (int j = 0; j < networks[i].J; j++) {
                newInbound += networks[i].distributionInbound[j] - networks[i].distributionOutbound[j];
            }

            assertEquals(productionBalance,customerBalance);
            assertEquals(productionBalance,returned);
            assertEquals(productionBalance,newInbound);
            assertTrue(networks[i].productionBalance());

            System.out.println(" ok...");
        }
    }

    @Test
    void testSecondStageDistributionBalance(){
        System.out.println("Testing secondStageDistributionBalance method...");

        for(int i = 0 ; i < INSTANCES.length ; i++) {
            System.out.print("Testig with instance: " + INSTANCES[i]);

            Distributor.startProduction(networks[i]);
            Distributor.firstStageInitialDistribution(networks[i]);
            Distributor.secondStageInitialDistribution(networks[i]);

            assertTrue(networks[i].testNetwork());//Create network

            int closed = 0;//The first dc found with positive inbound is closed
            for (int j = 0 ; j < networks[i].J ; j++) {
                if (networks[i].distributionInbound[j] > 0) {
                    closed = j;
                    Distributor.closeDistributionCenter(j,networks[i]);
                    assertEquals(0,networks[i].distributionInbound[j]);
                    assertEquals(0,networks[i].distributionOutbound[j]);
                    break;
                }
            }

            assertEquals(false,networks[i].customerBalance());
            assertEquals(false,networks[i].productionBalance());

            int dcBalance = 0;
            int demand = 0;
            int production = 0;

            for (int j = 0; j < networks[i].K; j++) {
                demand += networks[i].customerBalance[j];
            }
            for (int j = 0; j < networks[i].I; j++) {
                production += networks[i].productionBalance[j];
            }

            assertEquals(production,demand);// customer balance and production balance must be equal

            for (int j = 0 ; j < networks[i].I ; j++) {
                if(networks[i].productionBalance[j] > 0)
                    Distributor.firstStageDistributionBalance(j,closed,networks[i]);
            }

            for (int j = 0; j < networks[i].J; j++) {
                dcBalance += networks[i].distributionInbound[j] - networks[i].distributionOutbound[j];
            }
            assertEquals(dcBalance,demand);

            assertTrue(networks[i].productionBalance());
            assertEquals(false,networks[i].distributionBalance());
            assertEquals(false,networks[i].customerBalance());

            for (int dc = 0 ; dc < networks[i].J ; dc++) {
                if(networks[i].distributionInbound[dc] > networks[i].distributionOutbound[dc]) {
                    Distributor.secondStageDistributionBalance(dc, networks[i]);
                }
            }

            assertTrue(networks[i].testNetwork());
            System.out.println(" ok...");
        }
    }

    @Test
    void testRandomAllocationFromProductionCenter(){
        System.out.println("Testing randomAllocationFromProductionCenter method...");

        for(int i = 0 ; i < INSTANCES.length ; i++) {
            System.out.print("Testig with instance: " + INSTANCES[i]);

            Distributor.startProduction(networks[i]);
            Distributor.firstStageInitialDistribution(networks[i]);
            Distributor.secondStageInitialDistribution(networks[i]);

            int productionCenter = 0;
            //int[] inboundBefore = networks[i].distributionInbound.clone();

            for (int pc = 0 ; pc < networks[i].I ; pc++) {
                if (networks[i].getQuantityProduced()[pc] > 0 && networks[i].productionBalance[pc] == 0) {
                    productionCenter = pc;
                    networks[i].closeProductionCenter(pc);// close production center
                    assertTrue(networks[i].productionBalance[pc] > 0);
                    assertFalse(networks[i].distributionBalance());
                    break;
                }
            }
            //int[] inboundAfter = networks[i].distributionInbound.clone();
            //It is possible that it allocates to the same distribution centers
            Distributor.randomAllocationFromProductionCenter(productionCenter,networks[i]);

            //inboundAfter = networks[i].distributionInbound.clone();
            assertEquals(0,networks[i].productionBalance[productionCenter]);
            assertTrue(networks[i].productionBalance());
            //assertFalse(networks[i].distributionBalance());
            //assertEquals(false,Arrays.equals(inboundBefore,inboundAfter));
            System.out.println(" ok...");
        }
    }

    @Test
    void testProductionMutationBalance(){

        System.out.println("Testing randomAllocationFromProductionCenter method...");

        for(int i = 0 ; i < INSTANCES.length ; i++) {
            System.out.print("Testing with instance: " + INSTANCES[i]);

            Distributor.startProduction(networks[i]);
            Distributor.firstStageInitialDistribution(networks[i]);
            Distributor.secondStageInitialDistribution(networks[i]);

            int distributionCenter = -1;

            for (int j = 0; j < networks[i].J; j++) {
                if(networks[i].distributionInbound[i] > 0){
                    networks[i].distributionInbound[j] += 50;
                    distributionCenter = j;
                    break;
                }
            }

            if(distributionCenter == -1){
                System.out.println("Network did not change...");
            }else{
                Distributor.returnToUnbalancedDistributionCenters(networks[i]);
            }

            /**TODO (?) what about if I don't return all the product to the unbalanced distribution centers*/
            for (int j = 0 ; j < networks[i].J ; j++) {
                if(networks[i].distributionInbound[j] != networks[i].distributionOutbound[j]){
                    Distributor.secondStageDistributionBalance(j,networks[i]);
                }
            }

            assertTrue(networks[i].testNetwork());

            System.out.println(" ok...");
        }

    }
    boolean comparePlan(int[][] p1, int[][] p2){
	    for(int i = 0 ; i < p1.length ; i++){
	        if(!Arrays.equals(p1[i],p2[i]))
                return false;
        }
	    return true;
    }


}
