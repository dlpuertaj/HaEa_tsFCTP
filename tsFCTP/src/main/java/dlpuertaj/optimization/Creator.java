package dlpuertaj.optimization;

import dlpuertaj.optimization.domain.tsFCTP;
import dlpuertaj.optimization.utils.Generator;
import dlpuertaj.optimization.utils.Util;

public class Creator {

    public static void main(String args[]){
        Generator gen = new Generator();

        tsFCTP instance = gen.generateInstance(2,4,6,120);

        Util.exportInstanceToTextFile(instance);

        /**
         *  I*J*K global supply global demand
         *  1 - 2 — 4 — 6 160 120
         *  2 - 2 — 4 — 8 160 160
         *  3 - 4 — 8 — 12 320 240
         *  4 - 4 — 8 — 16 320 320
         *  5 - 6 — 12 — 18 480 360
         *  6 - 6 — 12 — 24 480 480
         *  7 - 8 — 16 — 24 640 480
         *  8 - 8 — 16 — 32 640 640
         *  9 - 10 — 20 — 30 800 600
         * 10 - 10 — 20 — 40 800 800
         * 11 - 20 — 40 — 60 1600 1200
         * 12 - 20 — 40 — 80 1600 1600
         * 13 - 30 — 60 — 90 2400 1800
         * 14 - 30 — 60 — 120 2400 2400
         * 15 - 40 — 80 — 120 3200 2400
         * 16 - 40 — 80 — 160 3200 3200
         * 17 - 50 — 100 — 150 4000 3000
         * 18 - 50 — 100 — 200 4000 4000
         * 19 - 60 — 120 — 180 4800 3600
         * 20 - 60 — 120 — 240 4800 4800*/
    }
}
