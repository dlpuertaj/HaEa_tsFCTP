import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.domain.Distributor;
import evolution.dlpuertaj.tsFCTP;
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
            i++;
        }
    }

    @Test
    public void testFlowNetwork(){
        System.out.println("\nTesting network balance process...");
        for(int i = 0 ; i < networks.length ; i++){
            System.out.print("Testig with instance: " + INSTANCES[i]);
            assertNotNull(networks[i]);

            Distributor.startProduction(networks[i]);
            Distributor.firstStageInitialDistribution(networks[i]);
            Distributor.secondStageInitialDistribution(networks[i]);

            assertTrue(networks[i].testNetwork());
        }
    }
}
