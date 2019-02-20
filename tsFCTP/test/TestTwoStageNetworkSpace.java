import dlpuertaj.optimization.TwoStageNetworkSpace;
import dlpuertaj.optimization.domain.TwoStageFlowNetwork;
import dlpuertaj.optimization.domain.tsFCTP;
import dlpuertaj.optimization.utils.Distributor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTwoStageNetworkSpace {
    private tsFCTP[] problems;
    private TwoStageNetworkSpace[] space;
    private static final String[] INSTANCES ={"223","224","225","226","227",
            "233","234","236","238","248",
            "256","324","325","334","335",
            "336","337_01","337_02","346","435",};


    @BeforeEach
    public void init() {
        System.out.println("\nBuilding instances and networks...");

        problems = new tsFCTP[INSTANCES.length];
        space = new TwoStageNetworkSpace[INSTANCES.length];
        int i = 0;
        for(String instance : INSTANCES) {
            problems[i] = new tsFCTP(instance);
            space[i] = new TwoStageNetworkSpace(problems[i]);
            i++;
        }
    }

    @Test
    public void testPick(){
        System.out.println("\nTesting network creation...");
        for(int i = 0 ; i < space.length ; i++){
            System.out.print("Testig with instance: " + INSTANCES[i]);

            assertNotNull(space[i]);

            System.out.println();
            for(int j = 0 ; j < 50 ; j++){
                TwoStageFlowNetwork network = space[i].pick();
                assertTrue(network.testNetwork());
            }


            System.out.println(" ok...");
        }
    }
}
