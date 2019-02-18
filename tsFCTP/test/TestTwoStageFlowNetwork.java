
import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.domain.tsFCTP;
import dlpuertaj.optimization.utils.Distributor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTwoStageFlowNetwork {
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
            Distributor.startProduction(networks[i]);
            Distributor.firstStageInitialDistribution(networks[i]);
            Distributor.secondStageInitialDistribution(networks[i]);
            i++;
        }
    }

    @Test
    public void testProductionBalance(){
        System.out.println("\nTesting network production balance...");
        for(int i = 0 ; i < networks.length ; i++){
            System.out.print("Testig with instance: " + INSTANCES[i]);
            assertNotNull(networks[i]);

            assertTrue(networks[i].productionBalance());
            System.out.println(" ok...");
        }
    }

    @Test
    public void testDistributionBalance(){
        System.out.println("\nTesting network production balance...");
        for(int i = 0 ; i < networks.length ; i++){
            System.out.print("Testig with instance: " + INSTANCES[i]);
            assertNotNull(networks[i]);

            assertTrue(networks[i].distributionBalance());
            System.out.println(" ok...");
        }
    }

    @Test
    public void testCustomerBalance(){
        System.out.println("\nTesting network production balance...");
        for(int i = 0 ; i < networks.length ; i++){
            System.out.print("Testig with instance: " + INSTANCES[i]);
            assertNotNull(networks[i]);

            assertTrue(networks[i].customerBalance());
            System.out.println(" ok...");
        }
    }

    @Test
    public void testNetworkBalance(){
        System.out.println("\nTesting network production balance...");
        for(int i = 0 ; i < networks.length ; i++){
            System.out.print("Testig with instance: " + INSTANCES[i]);
            assertNotNull(networks[i]);

            assertTrue(networks[i].testNetwork());
            System.out.println(" ok...");
        }
    }
}