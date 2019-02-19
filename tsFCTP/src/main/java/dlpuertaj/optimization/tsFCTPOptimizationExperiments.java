/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dlpuertaj.optimization;

import dlpuertaj.optimization.domain.tsFCTP;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Davosoft
 */
public class tsFCTPOptimizationExperiments {
    public static void main(String argv[]) {
        
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        BufferedWriter totalExperimentWriter;
        BufferedWriter instanceExperimentWriter;
        
        
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
        int TATALEXP = 30;
        int POPSIZE  = 20;
        int MAXITER  = 10000;
        int run;
        try {
            totalExperimentWriter = new BufferedWriter(new OutputStreamWriter(
                                                           new FileOutputStream("Results.txt"), "utf-8"));
            totalExperimentWriter.write("POPSIZE: "+POPSIZE);
            totalExperimentWriter.write(NEWLINE);
            totalExperimentWriter.write("MAXITER: "+MAXITER);
            totalExperimentWriter.write(NEWLINE);
            long startExperiments = System.nanoTime();
            for (String instance : instances) {
                double[][] runData;
                try (BufferedWriter bestSolutions = new BufferedWriter(new OutputStreamWriter(
                                                    new FileOutputStream(instance+" - Best.txt"), "utf-8"))) {
                    instanceExperimentWriter      = new BufferedWriter(new OutputStreamWriter(
                                                    new FileOutputStream("Runs-"+instance+".txt"), "utf-8"));
                    System.out.println("Running problem "+instance);
                    runData = new double[TATALEXP][];
                    sb.append(instance);
                    sb.append(" | ");
                    
                    
                    tsFCTP problem = new tsFCTP(instance);
                    // Search Space
                    
                    for ( run = 0; run < TATALEXP; run++) {
//                        System.out.println("Run: "+run);
	                    runData[run] = applyOptimization(   problem,
	                    									instance,
	                                                        run        ,
	                                                        POPSIZE    ,
	                                                        MAXITER    ,
	                                                        bestSolutions);                    
	
	                    instanceExperimentWriter.write(String.valueOf(run) + " - " + 
							       String.valueOf(runData[run][0]) + "," +
			                       String.valueOf(runData[run][1]));

						instanceExperimentWriter.newLine();
						bestSolutions.newLine();

                    }
                    
                    instanceExperimentWriter.close();
                }
                
//                double [][] totalInstanceRunData = readResults(instance,TATALEXP);
                sb.append(resultInstance(runData));
                sb.append(NEWLINE);

            }
            long endExperiments = System.nanoTime() - startExperiments;
            totalExperimentWriter.write(sb.toString());
            totalExperimentWriter.write(String.valueOf((endExperiments/1000000.0)/1000.0)+" seconds");
            totalExperimentWriter.close();
        }   catch (IOException ex) {
                Logger.getLogger(tsFCTPOptimizationExperiments.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    
    public static String resultInstance(double runData[][]){
         StringBuilder sb = new StringBuilder();
        //Best
        double b = runData[0][0];
        double w = runData[0][1];
        double T = 0;
        double Me;
        ArrayList<Double> v = new ArrayList<>();
        for (int i = 0; i < runData.length ; i++) {
            v.add(runData[i][0]);
            T += runData[i][0];
            if(runData[i][0] < b)
                b = runData[i][0];
            if(runData[i][1] > w)
                w = runData[i][1];
        }
        int N = runData.length;
        sb.append(b);sb.append(" | ");
        sb.append(w);
        sb.append(" | ");
        sb.append(String.format( "%.2f", T/N ));
        
        Collections.sort(v);
        double midVal1 = v.get((N-1)/2);
        double midVal2 = v.get(((N-1)/2)+1);
        if(N%2 == 0)
            Me = midVal1;
        else
            Me = (midVal1 + midVal2) / 2;
        
        sb.append(" | ");
        sb.append(String.format( "%.2f", Me ));
        
        double sd = 0;
        for (double[] runData1 : runData) {
            sd += Math.pow((runData1[0]) - Me, 2);
        }
        sd = sd / N;
        
        sd = Math.sqrt(sd);
        
        sb.append(" | ");
        sb.append(String.format( "%.2f", sd ));
        
        return sb.toString();
    }
    
    public static double[] applyOptimization(tsFCTP problem   ,
    									     String instance  ,
                                             int run          , 
                                             int POPSIZE      ,
                                             int MAXITERS     ,
                                             BufferedWriter best){
        
    	/*Space<TwoStageFlowNetwork> space = new TwoStageNetworkSpace(problem);
    	
    	// Optimization Function
    	OptimizationFunction<TwoStageFlowNetwork> function = new TwoStageNetworkFlowFitness(problem.transportCostS1,
    																						problem.fixedCostS1,
    																						problem.transportCostS2,
    																						problem.fixedCostS2);
    	
		OptimizationGoal<TwoStageFlowNetwork> oGoal = new OptimizationGoal<TwoStageFlowNetwork>(function);	
		
        Goal<TwoStageFlowNetwork,Double> goal = oGoal;//new OptimizationGoal<>(function); // minimizing   	
     
     	// Variation definition
         ArityOne<TwoStageFlowNetwork> mutation1 = new DistributionDisablerMutation(0.0);
         ArityOne<TwoStageFlowNetwork> mutation2 = new NetworkProductionMutation(0.0);
         ArityTwo<TwoStageFlowNetwork> xover = new XOverNetworkCustomers();
         
     	@SuppressWarnings("unchecked")
         Operator<TwoStageFlowNetwork>[] opers = (Operator<TwoStageFlowNetwork>[])new Operator[3];
     	opers[0] = mutation1;
     	opers[1] = mutation2;
        opers[2] = xover;
     	HaeaOperators<TwoStageFlowNetwork> operators = new SimpleHaeaOperators<>(opers);
         
         // Search method

         EAFactory<TwoStageFlowNetwork> factory = new EAFactory<>();
         PopulationSearch<TwoStageFlowNetwork,Double> search = 
         factory.HAEA(POPSIZE, operators, new Tournament<>(4), MAXITERS );

         // Tracking the goal evaluations
         WriteDescriptors write_desc = new WriteDescriptors();
         Write.set(double[].class, new DoubleArrayPlainWrite(false));
         Write.set(Population.class, write_desc);
         Write.set(HaeaStep.class, new WriteHaeaStep<int[]>());
         Descriptors.set(Population.class, new PopulationDescriptors<int[]>());
         Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<int[]>());
         Write.set(HaeaOperators.class, write_desc);
         IntArrayPlainWrite write = new IntArrayPlainWrite(',',false);
         Write.set(int[].class, write);
//           
         FileTracer fTracer = new FileTracer("Run"+run+"-"+instance+".txt");
//         ConsoleTracer tracer = new ConsoleTracer();       
//         Tracer.addTracer(search, tracer);  // Uncomment if you want to trace the function evaluations
         Tracer.addTracer(search, fTracer); // Uncomment if you want to trace the hill-climbing algorithm
         
//          Apply the search method
         final long start = System.nanoTime();
         Solution<TwoStageFlowNetwork> popBest = search.solve(space, goal);
         final long end = System.nanoTime() - start;
//         System.out.println("fCount: "+oGoal.getFitnessEvaluations());
         fTracer.close();
         double[] data = {
        		 (double) popBest.info(Goal.class.getName()),(double) popBest.info(Goal.class.getName())};
         
         try{
             best.write(Arrays.toString(Distributor.flowArray(popBest.object())) + " - " + 
             						  (double) popBest.info(Goal.class.getName())+" - " +
             						  String.valueOf((end/1000000.0)/1000.0)+" seconds");

         } catch (IOException ex) {
             Logger.getLogger(tsFCTPOptimizationExperiments.class.getName()).log(Level.SEVERE, null, ex);
         }*/
         
         return null;//data;
    }
}

