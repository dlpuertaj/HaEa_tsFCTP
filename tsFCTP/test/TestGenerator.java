import dlpuertaj.optimization.utils.Generator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestGenerator {

    Generator gen = new Generator();
    public static final int NUMBER_OF_POINTS1 = 20;
    public static final int NUMBER_OF_POINTS2 = 25;
    public static final int NUMBER_OF_POINTS3 = 30;
    @Test
    public void testGeneratePoints(){
        int[][] productionPoints = gen.generatePoints(NUMBER_OF_POINTS1, new int[NUMBER_OF_POINTS1][2],
                new int[NUMBER_OF_POINTS1][2]);
        int[][] distributionPoints = gen.generatePoints(NUMBER_OF_POINTS2, productionPoints, new int[NUMBER_OF_POINTS2][2]);
        int[][] customerPoints = gen.generatePoints(NUMBER_OF_POINTS3,distributionPoints,productionPoints);

        for (int[] p : productionPoints) {
            System.out.println(Arrays.toString(p));
        }
        System.out.println("---------------");
        for (int[] p : distributionPoints) {
            System.out.println(Arrays.toString(p));
        }
        System.out.println("---------------");
        for (int[] p : customerPoints) {
            System.out.println(Arrays.toString(p));
        }

        assertEquals(NUMBER_OF_POINTS1,productionPoints.length);
        assertEquals(NUMBER_OF_POINTS2,distributionPoints.length);
        assertEquals(NUMBER_OF_POINTS3,customerPoints.length);
    }
}
