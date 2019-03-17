import dlpuertaj.optimization.utils.Generator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGenerator {

    Generator gen = new Generator();
    public static final int NUMBER_OF_POINTS = 20;
    @Test
    public void testGeneratePoints(){
        int[][] points1 = gen.generatePoints(NUMBER_OF_POINTS);
        int[][] points2 = gen.generatePoints(NUMBER_OF_POINTS,points1);
        for (int[] p : points1) {
            System.out.println(Arrays.toString(p));
        }
        System.out.println("---------------");
        for (int[] p : points2) {
            System.out.println(Arrays.toString(p));
        }
        assertEquals(NUMBER_OF_POINTS,points1.length);
        assertEquals(NUMBER_OF_POINTS,points2.length);
    }
}
