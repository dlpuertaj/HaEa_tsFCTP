package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.tsFCTP;

class TestProblem {
	public final String INSTANCE = "223";
	public tsFCTP problem = null;  
	public TwoStageFlowNetwork network = null;
	
	@BeforeEach
	void ini() {
		this.problem = new tsFCTP(INSTANCE);
	}
	@Test
	void testProblemInstance() {
		assertNotNull(problem);
	}
	
	@Test
	void testBuildInstance() {
		
		problem.build(INSTANCE);
		
		assertTrue(this.problem.totalDemand >= this.problem.totalProductionCapacity);
		
		problem.showProblemInstance();		
	}

}
