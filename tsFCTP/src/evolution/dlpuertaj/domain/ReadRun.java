package evolution.dlpuertaj.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ReadRun {
	public static void main(String[] argv){
		String[] instances = {
                "223",
                "224",
                "225",
                "226",
                "227",
                "233",
                "234",
                "236",
                "238",
                "248",
                "256",
                "324",
                "325",
                "334",
                "335",
                "336",
                "337a",
                "337b",
                "346",
                "435",
		};
		int iters = 10001;
		int experiments = 30;
		
		try {
			System.out.println("Reading files");
			for (String instance : instances) {
				System.out.println("Instance "+instance);
				double[][] fitnessBest    = new double[iters][experiments];
				double[][] fitnessWorst   = new double[iters][experiments];
			    double[][] median         = new double[iters][experiments];
			    double[][] averageFitBest = new double[iters][experiments];
			    double[][] deviation      = new double[iters][experiments];
			    double[][] distRate       = new double[iters][experiments];
			    double[][] prodRate       = new double[iters][experiments];
			    double[][] xRate          = new double[iters][experiments];
			    double[] averageBest      = new double[iters];
			    double[] averageWorst     = new double[iters];
			    double[] averageMedian    = new double[iters];
			    double[] averageAvgBest   = new double[iters];
			    double[] averageDeviation = new double[iters];
			    double[] averageDistRate  = new double[iters];
			    double[] averageProdRate  = new double[iters];
			    double[] averageXOverRate = new double[iters];
			    
				for (int exp = 0 ; exp < experiments ; exp++) {
//					System.out.println(exp);
					BufferedReader br = new BufferedReader(new FileReader("Runs100-10000/Run"+exp+"-"+instance+".txt"));
					String line = br.readLine();
					line = br.readLine();
					String[] arrLine;
					int iter = 0;
					BufferedWriter expData = new BufferedWriter(new OutputStreamWriter(
		                    new FileOutputStream("Data"+instance+"-Exp"+exp+".dat"), "utf-8"));
					expData.write("iter      "+"   "+
		                          "best      "+"   "+
							      "worst     "+"   "+
		                          "median    "+"   "+
		                          "avgBest   "+"   "+
		                          "variance  "+"   "+
							      "deviation "+"   "+
		                          "distRate  "+"   "+
							      "prodRate  "+"   "+
		                          "xRate");
					expData.newLine();
				    while (line != null) {
//				    	System.out.println(line);
//				        sb.append(line);
//				        sb.append(System.lineSeparator());
				    	arrLine = line.split(" ");
				    	fitnessBest   [iter][exp] = Double.valueOf(arrLine[1]);
				    	fitnessWorst  [iter][exp] = Double.valueOf(arrLine[2]);
				    	median        [iter][exp] = Double.valueOf(arrLine[3]);
				    	averageFitBest[iter][exp] = Double.valueOf(arrLine[4]);
//				    	variance      [iter][exp] = Double.valueOf(arrLine[5]);
				    	deviation     [iter][exp] = Double.valueOf(arrLine[6]);

				    	distRate[iter][exp] = Double.valueOf(arrLine[7]);
				    	prodRate[iter][exp] = Double.valueOf(arrLine[8]);
				    	xRate[iter][exp]    = Double.valueOf(arrLine[9]);
				    	expData.write(iter       +"   "+
				    			      arrLine[1] +"   "+
				    			      arrLine[2] +"   "+
				    			      arrLine[3] +"   "+
				    			      arrLine[4] +"   "+
				    			      arrLine[5] +"   "+
				    			      arrLine[6] +"   "+
				    			      arrLine[7] +"   "+
				    			      arrLine[8] +"   "+
				    			      arrLine[9]);
				    	expData.newLine();
//				    	System.out.println(Arrays.toString(arrLine));
				        line = br.readLine();
				        iter++;
				    }
				    expData.close();
				    br.close();
				}
				System.out.println("Saving average");
				for(int iter = 0; iter < iters ; iter++){
					int sumBest      = 0;
					int sumWorst     = 0;
					int sumAverage   = 0;
					int sumMedian    = 0;
					int sumDeviation = 0;
					double sumRate1     = 0;
					double sumRate2     = 0;
					double sumRate3     = 0;
					
					
					
					for(int exp = 0 ; exp < experiments ; exp++){
						
						
						sumBest      += fitnessBest   [iter][exp];
						sumWorst     += fitnessWorst  [iter][exp];
						sumAverage   += averageFitBest[iter][exp];
						sumMedian    += median        [iter][exp];
						sumDeviation += deviation     [iter][exp];
						sumRate1     += distRate      [iter][exp];
						sumRate2     += prodRate      [iter][exp];
						sumRate3     += xRate         [iter][exp];
						
					}
					averageBest     [iter] = sumBest/30.0;
					averageWorst    [iter] = sumWorst/30.0;
					averageMedian   [iter] = sumMedian/30.0;
					averageAvgBest  [iter] = sumAverage/30.0;
					averageDeviation[iter] = sumDeviation/30.0;
					averageDistRate [iter] = sumRate1/30.0;
					averageProdRate [iter] = sumRate2/30.0;
					averageXOverRate[iter] = sumRate3/30.0;
				}
				System.out.println("Saving data...");
				BufferedWriter Data = new BufferedWriter(new OutputStreamWriter(
	                    new FileOutputStream("Data"+instance+".dat"), "utf-8"));
				Data.write("iter"+"   "+"best"+"   "+"worst"+"   "+"median"+"   "+"deviation"+"   "+"distRate"+"   "+"prodRate"+"   "+"xRate");
				Data.newLine();
				for (int i = 0 ; i < iters-1 ; i++) {
					Data.write(i+"   "+averageBest[i] +"   "+
							           averageWorst[i]+"   "+
							           averageMedian[i]+"   "+
							           averageDeviation[i]+"   "+
							           averageDistRate[i]+"   "+
							           averageProdRate[i]+"   "+
							           averageXOverRate[i]);
//					System.out.println(i+"   "+averageBest[i]+"   "+averageWorst[i]+"   "+averageMedian[i]+"   "+averageDeviation[i]+"   "+
//							           averageDistRate[i]+"   "+
//					                   averageProdRate[i]+"   "+
//					                   averageXOverRate[i]);
					Data.newLine();
				}
			    Data.close();
			    System.out.println("----------------");
			}
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void plot(double[] fitness,double[] median){
		
		
	}
}
