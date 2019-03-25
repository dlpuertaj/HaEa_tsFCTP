package dlpuertaj.optimization;

import dlpuertaj.optimization.domain.tsFCTP;
import dlpuertaj.optimization.utils.Constants;
import dlpuertaj.optimization.utils.Generator;
import dlpuertaj.optimization.utils.Util;

public class Creator {

    public static void main(String args[]){
        Generator gen = new Generator();

        for (int i = 0; i < Constants.SIZES.length; i++) {
            tsFCTP instance = gen.generateInstance(Constants.SIZES[i][0],
                    Constants.SIZES[i][1],
                    Constants.SIZES[i][2],
                    Constants.SIZES[i][3]);
            Util.exportInstanceToTextFile(instance);
        }
    }
}
