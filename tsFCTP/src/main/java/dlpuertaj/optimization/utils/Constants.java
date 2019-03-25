package dlpuertaj.optimization.utils;

public class Constants {
    public static final int FIXED_PLANT_SUPPLY = 80;
    public static final int DEMAND_LOWER_BOUND = 10;
    public static final int DEMAND_UPPER_BOUND = 30;
    public static final int POINTS_LOWER_BOUND = -400;
    public static final int POINTS_UPPER_BOUND = 400;
    public static final int FIXED_COST_LOWER_BOUND_FIRST_STAGE = 10;
    public static final int FIXED_COST_LOWER_BOUND_SECOND_STAGE = 5;
    public static final int FIXED_COST_UPPER_BOUND_FIRST_STAGE = 15;
    public static final int FIXED_COST_UPPER_BOUND_SECOND_STAGE = 10;
    public static final String TXT = ".txt";
    public static final String NEW_INSTANCE_FILE_PATH = "created_instances\\";
    public static final String STRING_BUILDER_NEW_LINE = "line.separator";

    /**
     * I*J*K global supply global demand
     */
    public static final int[][] SIZES = {
            {2, 4, 6, 160, 120},
            {2, 4, 8, 160, 160},
            {4, 8, 12, 320, 240},
            {4, 8, 16, 320, 320},
            {6, 12, 18, 480, 360},
            {6, 12, 24, 480, 480},
            {8, 16, 24, 640, 480},
            {8, 16, 32, 640, 640},
            {10, 20, 30, 800, 600},
            {10, 20, 40, 800, 800},
            {20, 40, 60, 1600, 1200},
            {20, 40, 80, 1600, 1600},
            {30, 60, 90, 2400, 1800},
            {30, 60, 120, 2400, 2400},
            {40, 80, 120, 3200, 2400},
            {40, 80, 160, 3200, 3200},
            {50, 100, 150, 4000, 3000},
            {50, 100, 200, 4000, 4000},
            {60, 120, 180, 4800, 3600},
            {60, 120, 240, 4800, 4800}};
}
