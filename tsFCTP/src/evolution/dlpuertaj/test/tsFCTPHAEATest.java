/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution.dlpuertaj.test;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.TwoStageNetworkFlowFitness;
import evolution.dlpuertaj.TwoStageNetworkSpace;
import evolution.dlpuertaj.tsFCTP;
import evolution.dlpuertaj.operators.DistributionDisablerMutation;
import evolution.dlpuertaj.operators.NetworkProductionMutation;
import evolution.dlpuertaj.operators.XOverNetworkCustomers;
import evolution.dlpuertaj.utils.MethodTest;
import unalcol.descriptors.WriteDescriptors;
import unalcol.evolution.EAFactory;
import unalcol.evolution.haea.HaeaOperators;
import unalcol.evolution.haea.HaeaStep;
import unalcol.evolution.haea.SimpleHaeaOperators;
import unalcol.evolution.haea.SimpleHaeaOperatorsDescriptor;
import unalcol.evolution.haea.WriteHaeaStep;
import unalcol.optimization.OptimizationFunction;
import unalcol.search.Search;
import unalcol.search.population.PopulationSearch;
import unalcol.search.selection.Tournament;
import unalcol.search.solution.SolutionDescriptors;
import unalcol.search.solution.SolutionWrite;
import unalcol.search.space.Space;
import unalcol.search.variation.Variation_1_1;
import unalcol.services.ProvidersSet;
import unalcol.services.Service;
import unalcol.tracer.ConsoleTracer;
import unalcol.tracer.Tracer;
import unalcol.tracer.VectorTracer;
import unalcol.types.collection.vector.Vector;
import unalcol.types.object.Tagged;
import unalcol.types.real.array.DoubleArrayPlainWrite;

/**
 *
 * @author jgomez
 */
public class tsFCTPHAEATest {
	public static void main(String[] args) {
		for (int i = 0; i < 1; i++) {
			tsFCTPOptimization();
		}
	}

	public static void tsFCTPOptimization() {

		String instance = "346";
		tsFCTP problem = new tsFCTP(instance);

		// Search space
    	Space<TwoStageFlowNetwork> space = new TwoStageNetworkSpace(problem);

		// Optimization Function
		OptimizationFunction<TwoStageFlowNetwork> function = new TwoStageNetworkFlowFitness(problem.transportCostS1,
				                                                              problem.fixedCostS1, 
				                                                              problem.transportCostS2, 
				                                                              problem.fixedCostS2);
		
		
		// Variation definition
		Variation_1_1<TwoStageFlowNetwork> distributionMutation = new DistributionDisablerMutation(0.0);
		Variation_1_1<TwoStageFlowNetwork> productionMutation = new NetworkProductionMutation(0.0);
		XOverNetworkCustomers xover = new XOverNetworkCustomers();
		HaeaOperators<TwoStageFlowNetwork> operators = new SimpleHaeaOperators<TwoStageFlowNetwork>(distributionMutation, productionMutation,xover);
		
		
		// Search method
		int POPSIZE = 100;
		int MAXITERS = 2;
		EAFactory<TwoStageFlowNetwork> factory = new EAFactory<TwoStageFlowNetwork>();
		
		PopulationSearch<TwoStageFlowNetwork,Double> search = factory.HAEA(POPSIZE, operators, new Tournament<TwoStageFlowNetwork,Double>(function,4), MAXITERS );
		
		search.setGoal(function);
		
		
		// Apply the search method
		// Services
		service( function, search );
        Service.register(new DoubleArrayPlainWrite(',',false), double[].class);
        Service.register(new SolutionDescriptors<TwoStageFlowNetwork>(function), Tagged.class);
        Service.register(new SolutionWrite<TwoStageFlowNetwork>(function,true), Tagged.class);
		haea_service(function);
		
		// Apply the search method
		// Tagged<BitArray> sol = 
		search.solve(space);
		print_function(function);	
		/* * 
		*/
	}
	
	@SuppressWarnings("rawtypes")
	public static void haea_service(OptimizationFunction<?> function){
        Service.register( new WriteHaeaStep(), HaeaStep.class);
        Service.register( new SimpleHaeaOperatorsDescriptor(), HaeaOperators.class);
        Service.register( new WriteDescriptors(), HaeaOperators.class);
		MethodTest.population_service(function);
	}
	
	public static void service(OptimizationFunction<?> function, Search<?, Double> search){
        // Tracking the goal evaluations
		Tracer t = new ConsoleTracer();
		t.start();
		Service.register(t, search);
		t = new VectorTracer();
		t.start();
		Service.register(t, function);
	}
	
	public static void print_function(OptimizationFunction<?> function){
		try{
			ProvidersSet tracers = Service.providers(Tracer.class, function);
			Tracer s = (Tracer)tracers.get("VectorTracer");
			@SuppressWarnings("unchecked")
			Vector<Object[]> v = (Vector<Object[]>)s.get();
			Object[] f = (Object[])v.get(0);
			// The fitness value is located as the second element in the array (the first one is the object)
			double bf = (Double)(f[1]);
			for( int i=0; i<v.size(); i++ ){
				f = (Object[])v.get(i);
				double cf = (Double)(f[1]);
				if( function.order().compare(bf, cf) < 0 ) bf = cf;
				System.out.println(i+" "+bf);
			}
		}catch(Exception e){ e.printStackTrace(); }
	}
	
}
