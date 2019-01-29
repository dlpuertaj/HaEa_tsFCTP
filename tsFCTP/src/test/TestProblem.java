package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import evolution.dlpuertaj.TwoStageFlowNetwork;
import evolution.dlpuertaj.tsFCTP;

class TestProblem {
	public final String INSTANCE = "223";
	public tsFCTP problem = null;  
	public TwoStageFlowNetwork network = null;
	
	@Test
	void testProblemInstance() {
		this.problem = new tsFCTP(INSTANCE);
		assertNotNull(problem);
		
		problem.showProblemInstance();
	}

}
