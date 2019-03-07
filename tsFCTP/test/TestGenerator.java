import dlpuertaj.optimization.utils.Generator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGenerator {

    Generator gen = new Generator();
    @Test
    public void testGeneratePoints(){
        int[][] points = gen.generatePoints(10);
        for (int[] p:points) {
            System.out.println(Arrays.toString(p));
        }
        assertEquals(10,points.length);
    }
}
