import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class TestUtil {

    public static int[][] source = {
            {100,200},
            {150,70} ,
            {48,90}  ,
            {150,70} ,
            {-85,320},
            {100,200}};
    public static int[][] target = {
            {400,200},
            {150,70} ,
            {85,-15}  ,
            {150,70} ,
            {260,375},
            {100,200}};
    @Test
    void testValidatePointsOne(){
        for (int i = 0; i < source.length; i++) {
            for (int j = i+1; j < source.length; j++) {
                Assertions.assertTrue(!Arrays.equals(source[i],source[j]));
            }
        }
    }

    @Test
    void testValidatePointsOneTwo(){

    }
}
